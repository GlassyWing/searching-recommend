package org.manlier.analysis.analyzers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.manlier.analysis.jieba.HBaseJiebaDictSource;
import org.manlier.common.utils.AnalyzerUtils;

import java.io.IOException;

@RunWith(JUnit4.class)
public class JiebaAnaylyzerTest {

    private HBaseJiebaDictSource dictSource;

    @Before
    public void setUp() {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "172.18.140.150,172.18.140.151,172.18.0.83");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("zookeeper.znode.parent", "/hbase-unsecure");
        dictSource = new HBaseJiebaDictSource(config);
    }

    @Test
    public void init() throws IOException {
        String text = "这是一个伸手不见五指的黑夜";
        JiebaAnaylyzer anaylyzer = new JiebaAnaylyzer(dictSource);
        AnalyzerUtils.displayTokens(anaylyzer, text);
    }
}