package org.manlier.srapp.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.jsoup.Jsoup;
import org.manlier.common.parsers.CompDocumentParser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HBaseCompsDirectImporter extends Configured implements Tool {

    static final String TABLE_NAME = "comp-demo";
    static final byte[] INFO_COLUMNFAMILY = Bytes.toBytes("info");
    static final byte[] DESC_QUALIFIER = Bytes.toBytes("desc");

    public static class HBaseCompsMapper extends Mapper<NullWritable, BytesWritable, Void, Void> {

        private CompDocumentParser parser;
        private Connection connection;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            connection = ConnectionFactory.createConnection(context.getConfiguration());
            parser = new CompDocumentParser();
        }

        @Override
        protected void map(NullWritable key, BytesWritable value, Context context) throws IOException, InterruptedException {
            if (parser.parse(Jsoup.parse(Bytes.toString(value.getBytes())))) {
                List<Put> puts = parser.getRecords().parallelStream()
                        .map(record -> {
                            Put put = new Put(Bytes.toBytes(record.name));
                            put.addColumn(INFO_COLUMNFAMILY, DESC_QUALIFIER
                            , Bytes.toBytes(record.desc));
                            return put;
                        })
                        .collect(Collectors.toList());
                try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
                    table.put(puts);
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            connection.close();
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: HBaseCompsDirectImporter  <input>");
            return -1;
        }
        Configuration configuration = HBaseConfiguration.create(getConf());
        Job job = Job.getInstance(configuration, getClass().getSimpleName());
        job.setJarByClass(getClass());
        WholeFileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(WholeFileInputFormat.class);
        job.setMapperClass(HBaseCompsMapper.class);
        job.setNumReduceTasks(0);
        job.setOutputFormatClass(NullOutputFormat.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new HBaseCompsDirectImporter(), args);
        System.exit(exitCode);
    }
}
