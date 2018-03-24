package org.manlier.common.schemes;

import org.apache.hadoop.hbase.util.Bytes;

public class HBaseJiebaDictQuery {
    public final static String TABLE_NAME = "jieba_dict";
    public final static byte[] INFO_COLUMNFAMILY = Bytes.toBytes("info");
    public final static byte[] WEIGHT_QUALIFIER = Bytes.toBytes("weight");
    public final static byte[] TAG_QUALIFIER = Bytes.toBytes("tag");
}
