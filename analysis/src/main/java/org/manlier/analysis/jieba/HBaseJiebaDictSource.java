package org.manlier.analysis.jieba;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.manlier.analysis.jieba.dao.DictSource;
import org.manlier.common.schemes.HBaseJiebaDictQuery;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class HBaseJiebaDictSource implements DictSource {

    private Configuration configuration;

    public HBaseJiebaDictSource(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void loadDict(Charset charset, Consumer<String[]> consumer) throws IOException {
        this.loadDict(consumer);
    }

    @Override
    public void loadDict(Consumer<String[]> consumer) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(configuration)) {
            try (Table table = connection.getTable(TableName.valueOf(HBaseJiebaDictQuery.TABLE_NAME))) {
                Scan scan = new Scan();
                ResultScanner scanner = table.getScanner(scan);
                scanner.forEach(result -> {
                    if (!result.isEmpty()) {

                        String word = Bytes.toString(result.getRow());
                        String weight = Bytes.toString(result.getValue(HBaseJiebaDictQuery.INFO_COLUMNFAMILY
                                , HBaseJiebaDictQuery.WEIGHT_QUALIFIER));
                        String tag = Bytes.toString(result.getValue(HBaseJiebaDictQuery.INFO_COLUMNFAMILY
                                , HBaseJiebaDictQuery.TAG_QUALIFIER));
                        consumer.accept(new String[]{word, weight, tag});
                    }
                });
            }
        }

    }
}
