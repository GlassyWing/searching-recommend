package org.manlier.common.schemes;

import org.apache.hadoop.hbase.util.Bytes;

public class HBaseComponentsQuery {
    public final static String TABLE_NAME = "components";
    public final static byte[] INFO_COLUMNFAMILY = Bytes.toBytes("info");
    public final static byte[] DESC_QUALIFIER = Bytes.toBytes("desc");
}
