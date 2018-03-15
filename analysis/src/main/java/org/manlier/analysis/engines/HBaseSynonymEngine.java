package org.manlier.analysis.engines;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.manlier.common.schemes.HBaseSynonymQuery;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class HBaseSynonymEngine implements SynonymEngine {

    private Connection connection;

    public HBaseSynonymEngine(Configuration configuration) throws IOException {
        this.connection = ConnectionFactory.createConnection(configuration);
    }

    public HBaseSynonymEngine(String hbaseConfigPath) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.addResource(new Path(hbaseConfigPath));
        this.connection = ConnectionFactory.createConnection(conf);
    }

    @Override
    public List<String> getSynonyms(String s) throws IOException {
        Get get = new Get(Bytes.toBytes(s));
        get.addFamily(HBaseSynonymQuery.SYNONYMS_COLUMNFAMILY);
        List<String> synonyms = new LinkedList<>();
        try (Table table = connection.getTable(TableName.valueOf(HBaseSynonymQuery.TABLE_NAME))) {
            Result result = table.get(get);

            if (result.isEmpty()) {
                return null;
            }
            for (Cell cell : result.listCells()) {
                byte[] column = CellUtil.cloneQualifier(cell);
                synonyms.add(Bytes.toString(column));
            }
        }
        return synonyms;
    }

    public void scanThesaurus(Consumer<List<String>> consumer) throws IOException {
        Scan scan = new Scan();
        try (Table table = connection.getTable(TableName.valueOf(HBaseSynonymQuery.TABLE_NAME))) {
            ResultScanner scanner = table.getScanner(scan);
            List<String> record = new LinkedList<>();
            scanner.forEach(result -> {
                String key = Bytes.toString(result.getRow());
                record.add(key);
                for (Cell cell : result.listCells()) {
                    byte[] column = CellUtil.cloneQualifier(cell);
                    record.add(Bytes.toString(column));
                }
                consumer.accept(record);
                record.clear();
            });
        }
    }
}
