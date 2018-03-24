package org.manlier.analysis.filters;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.*;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.manlier.analysis.engines.HBaseSynonymEngine;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 自定义同义词过滤器工厂
 */
public class HBaseSynonymGraphFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
    private Logger logger = Logger.getLogger(getClass());
    private final String synonyms;          //同义词词典的位置
    private final boolean ignoreCase;       //ignoreCase表示再分词匹配的时候要不要忽略大小写
    private final String tokenizerFactory;  //在词典表中读取一个字符串要进行分词，这个指定使用的分词器工厂
    private final String format;
    private final boolean expand;
    private final String analyzerName;      //在词典表中读取一个字符串要进行分词，这个指定使用的分词器
    private final Map<String, String> tokArgs = new HashMap<>();
    private SynonymMap map;
    private HBaseSynonymEngine engine;

    /**
     * Initialize this factory via a set of key-value pairs.
     *
     * @param args
     */
    public HBaseSynonymGraphFilterFactory(Map<String, String> args) throws IOException {
        super(args);
        this.synonyms = this.require(args, "synonyms");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        this.format = this.get(args, "format");
        this.expand = this.getBoolean(args, "expand", true);
        this.analyzerName = this.get(args, "analyzer");
        this.tokenizerFactory = this.get(args, "tokenizerFactory");

//        根据HBase连接参数创建配置对象
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", get(args, "ZKQuorum"));
        config.set("hbase.zookeeper.property.clientPort", get(args, "ZKPort"));
        config.set("zookeeper.znode.parent", get(args, "ZKZnode"));
        if (this.analyzerName != null && this.tokenizerFactory != null) {
            throw new IllegalArgumentException("Analyzer and TokenizerFactory can't be specified both: " + this.analyzerName + " and " + this.tokenizerFactory);
        } else {
            if (this.tokenizerFactory != null) {
                this.tokArgs.put("luceneMatchVersion", this.getLuceneMatchVersion().toString());
                Iterator itr = args.keySet().iterator();

                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    this.tokArgs.put(key.replaceAll("^tokenizerFactory\\.", ""), args.get(key));
                    itr.remove();
                }
            }

            if (!args.isEmpty()) {
                throw new IllegalArgumentException("Unknown parameters: " + args);
            }
        }

//        创建HBase数据库访问对象
        engine = new HBaseSynonymEngine(config);

    }

    @Override
    public TokenStream create(TokenStream input) {
        return this.map.fst == null ? input : new SynonymGraphFilter(input, this.map, this.ignoreCase);
    }


    @Override
    public void inform(ResourceLoader loader) throws IOException {
        final TokenizerFactory factory = this.tokenizerFactory == null ? null : this.loadTokenizerFactory(loader, this.tokenizerFactory);
        Analyzer analyzer;
        if (this.analyzerName != null) {
            analyzer = this.loadAnalyzer(loader, this.analyzerName);
        } else {
            analyzer = new Analyzer() {
                protected TokenStreamComponents createComponents(String fieldName) {
                    Tokenizer tokenizer = factory == null ? new WhitespaceTokenizer() : factory.create();
                    TokenStream stream = ignoreCase ? new LowerCaseFilter(tokenizer) : tokenizer;
                    return new TokenStreamComponents(tokenizer, stream);
                }
            };
        }

        try {
            Throwable var5 = null;

            try {
                String formatClass = this.format;
                if (this.format != null && !this.format.equals("solr")) {
                    if (this.format.equals("wordnet")) {
                        formatClass = WordnetSynonymParser.class.getName();
                    }
                } else {
                    formatClass = SolrSynonymParser.class.getName();
                }

                this.map = this.loadSynonyms(loader, formatClass, true, analyzer);
            } catch (Throwable var15) {
                var5 = var15;
                throw var15;
            } finally {
                if (analyzer != null) {
                    if (var5 != null) {
                        try {
                            analyzer.close();
                        } catch (Throwable var14) {
                            var5.addSuppressed(var14);
                        }
                    } else {
                        analyzer.close();
                    }
                }

            }

        } catch (ParseException var17) {
            throw new IOException("Error parsing synonyms file:", var17);
        }
    }

    /**
     * 载入同义词库
     * @param loader 资源载入器
     * @param cname 使用的格式化对象的类名
     * @param dedup 载入字典时是否排除重复
     * @param analyzer  分词器
     * @return  SynonymMap 对象
     * @throws IOException  连接数据库时的异常
     * @throws ParseException   格式不正确时报错
     */
    @SuppressWarnings("unchecked")
    protected SynonymMap loadSynonyms(ResourceLoader loader, String cname, boolean dedup, Analyzer analyzer) throws IOException, ParseException {

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        Class clazz = loader.findClass(cname, SynonymMap.Parser.class);

        SynonymMap.Parser parser;
        try {
            parser = (SynonymMap.Parser) clazz.getConstructor(Boolean.TYPE, Boolean.TYPE, Analyzer.class).newInstance(dedup, this.expand, analyzer);
        } catch (Exception var11) {
            throw new RuntimeException(var11);
        }

        List<String> files = this.splitFileNames(this.synonyms);

        for (String file : files) {
            logger.info("Loading synonyms from file: " + file);
            decoder.reset();
            parser.parse(new InputStreamReader(loader.openResource(file), decoder));
        }

        logger.info("Loading synonyms from database");
        engine.scanThesaurus(record -> {
            try {
                String r = String.join(",", record);
                Reader reader = new StringReader(r);
                parser.parse(reader);
            } catch (ParseException | IOException e) {
                logger.error("Format error, skip this record:", e);
            }
        });


        return parser.build();
    }

    @SuppressWarnings("unchecked")
    private TokenizerFactory loadTokenizerFactory(ResourceLoader loader, String cname) throws IOException {
        Class clazz = loader.findClass(cname, TokenizerFactory.class);

        try {
            TokenizerFactory tokFactory = (TokenizerFactory) clazz.getConstructor(Map.class).newInstance(this.tokArgs);
            if (tokFactory instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) tokFactory).inform(loader);
            }

            return tokFactory;
        } catch (Exception var5) {
            throw new RuntimeException(var5);
        }
    }

    @SuppressWarnings("unchecked")
    private Analyzer loadAnalyzer(ResourceLoader loader, String cname) throws IOException {
        Class clazz = loader.findClass(cname, Analyzer.class);

        try {
            Analyzer analyzer = (Analyzer) clazz.getConstructor().newInstance();
            if (analyzer instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) analyzer).inform(loader);
            }

            return analyzer;
        } catch (Exception var5) {
            throw new RuntimeException(var5);
        }
    }
}
