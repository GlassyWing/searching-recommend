package org.manlier.srapp.constraints;

import org.apache.hadoop.hbase.util.Bytes;

public class Schemas {

    /**
     * 存储于HBase中的构件表结构
     */
    public static class HBaseComponentSchema {
        public final static String TABLE_NAME = "components";
        public final static byte[] INFO_COLUMNFAMILY = Bytes.toBytes("info");
        public final static byte[] NAME_QUALIFIER = Bytes.toBytes("name");
        public final static byte[] DESC_QUALIFIER = Bytes.toBytes("desc");
    }

    /**
     * 存储于HBase中的jieba字典表结构
     */
    public static class HBaseJiebaDictSchema {
        public final static String TABLE_NAME = "jieba_dict";
        public final static byte[] INFO_COLUMNFAMILY = Bytes.toBytes("info");
        public final static byte[] WEIGHT_QUALIFIER = Bytes.toBytes("weight");
        public final static byte[] TAG_QUALIFIER = Bytes.toBytes("tag");
    }

    /**
     * 存储于HBase中的同义词字典表结构
     */
    public static class HBaseSynonymSchema {
        public final static String TABLE_NAME = "thesaurus";
        public final static byte[] SYNONYMS_COLUMNFAMILY = Bytes.toBytes("synonyms");
    }
}
