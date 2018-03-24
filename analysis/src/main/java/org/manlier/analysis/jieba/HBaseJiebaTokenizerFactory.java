package org.manlier.analysis.jieba;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.util.BitSet;
import java.util.Map;

public class HBaseJiebaTokenizerFactory extends TokenizerFactory {

    private JiebaSegmenter.SegMode segMode; //  jieba分词模式
    private boolean HMM;                    //  是否启用HMM
    private boolean useDefaultDict;         //  是否使用jieba的默认字典

    private HBaseJiebaDictSource dictSource;    // HBase 字典源

    public HBaseJiebaTokenizerFactory(Map<String, String> args) {
        super(args);
        if (null == get(args, "segMode"))
            segMode = JiebaSegmenter.SegMode.SEARCH;
        else
            segMode = JiebaSegmenter.SegMode.valueOf(get(args, "segMode"));

        if (null == get(args, "useDefaultDict")) {
            useDefaultDict = true;
        } else {
            useDefaultDict = Boolean.valueOf(get(args, "useDefaultDict"));
        }

        if (null == get(args, "HMM"))
            HMM = true;
        else
            HMM = Boolean.valueOf(get(args, "HMM"));

        // HBase 连接设置
        String ZKQuorum = get(args, "ZKQuorum");
        String ZKPort = get(args, "ZKPort");
        String ZKZnode = get(args, "ZKZnode");

        BitSet bits = new BitSet(3);
        bits.set(0, ZKQuorum != null);
        bits.set(1, ZKPort != null);
        bits.set(2, ZKZnode != null);
        // 三个配置都需设置，或者都不设置(不设置意味着不使用用户字典)
        if (bits.cardinality() == 3) {
            Configuration config = HBaseConfiguration.create();
            config.set("hbase.zookeeper.quorum", ZKQuorum);
            config.set("hbase.zookeeper.property.clientPort", ZKPort);
            config.set("zookeeper.znode.parent", ZKZnode);
            dictSource = new HBaseJiebaDictSource(config);
        } else if (bits.cardinality() != 0) {
            throw new IllegalArgumentException("Params ZKQuorum, ZKPort and ZKZnode must be set together.");
        }

        // 必须设置一个字典
        if (dictSource == null && !useDefaultDict) {
            throw new IllegalArgumentException("You must set a dict source for tokenizer");
        }

    }

    @Override
    public Tokenizer create(AttributeFactory attributeFactory) {
        try {
            return new HBaseJiebaTokenizer(dictSource, segMode, useDefaultDict, HMM, attributeFactory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
