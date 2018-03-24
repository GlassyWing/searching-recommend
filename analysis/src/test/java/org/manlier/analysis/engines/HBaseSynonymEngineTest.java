package org.manlier.analysis.engines;

import com.google.protobuf.ServiceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Arrays;

public class HBaseSynonymEngineTest {

	private HBaseSynonymEngine engine;

	@Before
	public void setUp() throws IOException {
		Configuration config = HBaseConfiguration.create();
		engine = new HBaseSynonymEngine(config);
	}

	@Test
	public void testConnect() throws IOException, ServiceException {
		Configuration config = HBaseConfiguration.create();
		Connection connection = ConnectionFactory.createConnection(config);
		Table table = connection.getTable(TableName.valueOf("thesaurus"));
		HTableDescriptor descriptor = table.getTableDescriptor();
		System.out.println(Arrays.toString(descriptor.getColumnFamilies()));
	}

	@Test
	public void getSynonyms() throws IOException {
		engine.getSynonyms("增添").forEach(System.out::println);
	}

    @Test
    public void scanThesaurus() throws IOException {
		engine.scanThesaurus(System.out::println);
    }

}