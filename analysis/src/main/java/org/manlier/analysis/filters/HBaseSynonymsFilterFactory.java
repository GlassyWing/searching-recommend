package org.manlier.analysis.filters;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.manlier.analysis.engines.HBaseSynonymEngine;

import java.io.IOException;
import java.util.Map;

public class HBaseSynonymsFilterFactory extends TokenFilterFactory {

    private HBaseSynonymEngine engine;

    public HBaseSynonymsFilterFactory(Map<String, String> args) throws IOException {
        super(args);
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", get(args, "ZKQuorum"));
        config.set("hbase.zookeeper.property.clientPort", get(args, "ZKPort"));
        config.set("zookeeper.znode.parent", get(args, "ZKZnode"));
        engine = new HBaseSynonymEngine(config);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        tokenStream = new HBaseSynonymFilter(tokenStream, engine);
        return tokenStream;
    }
}
