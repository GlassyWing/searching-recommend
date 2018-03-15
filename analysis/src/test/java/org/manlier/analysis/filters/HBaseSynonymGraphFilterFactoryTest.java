package org.manlier.analysis.filters;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.solr.core.SolrResourceLoader;
import org.junit.Test;
import org.manlier.common.utils.AnalyzerUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HBaseSynonymGraphFilterFactoryTest {

    @Test
    public void create() throws IOException {
        Map<String, String> args = new HashMap<>();
        args.put("ZKQuorum","172.18.140.150,172.18.140.151,172.18.0.83");
        args.put("ZKPort","2181");
        args.put("ZKZnode","/hbase-unsecure");
        HBaseSynonymGraphFilterFactory fc = new HBaseSynonymGraphFilterFactory(args);
        fc.inform(new SolrResourceLoader());

    }

}