package org.manlier.analysis.analyzers;

import org.apache.hadoop.conf.Configuration;
import org.manlier.analysis.engines.HBaseSynonymEngine;
import org.manlier.analysis.engines.SynonymEngine;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.lucene.analysis.Analyzer;
import org.junit.Before;
import org.junit.Test;
import org.manlier.common.utils.AnalyzerUtils;

import java.io.IOException;

public class SynonymAnalyzerTest {

	private SynonymEngine engine;

	@Before
	public void setUp() throws IOException {
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", "172.18.140.150,172.18.140.151,172.18.0.83");
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("zookeeper.znode.parent", "/hbase-unsecure");
		engine = new HBaseSynonymEngine(config);
	}

	@Test
	public void baseTest() throws IOException {
		String text = "我们将会一直在一起";
		Analyzer analyzer = new SynonymAnalyzer(engine);
		AnalyzerUtils.displayTokens(analyzer, text);
	}

}