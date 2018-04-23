package org.manlier.srapp.constraints;

import org.apache.hadoop.hbase.util.Bytes;

public class Schemas {

    /**
     * 存储于HBase中的构件表结构
     */
    public static class HBaseComponentSchema {
        public final static String TABLE_NAME = "components";
        public final static String ID_QUALIFIER = "ID";
        public final static String NAME_QUALIFIER = "NAME";
        public final static String DESC_QUALIFIER = "DESCRIBE";
    }

    public static class HBaseUsersSchema {
        public final static String TABLE_NAME = "users";
        public final static String ID_QUALIFIER = "ID";
        public final static String UUID_QUALIFIER = "UUID";
    }

    public static class HBaseHistorySchema {
        public final static String TABLE_NAME = "history";
        public final static String USER_QUALIFIER = "USERNAME";
        public final static String COMP_QUALIFIER = "COMPNAME";
        public final static String FOLLOW_COMP_QUALIFIER = "FOLLOWCOMPNAME";
        public final static String FREQ_QUALIFIER = "FREQ";
    }

    public static class HBasePredictionSchema {
        public final static String TABLE_NAME = "prediction";
        public final static String USER_QUALIFIER = "USERNAME";
        public final static String COMP_QUALIFIER = "COMPNAME";
        public final static String FOLLOW_COMP_QUALIFIER = "FOLLOWCOMPNAME";
        public final static String PREDICTION_QUALIFIER = "FREQ";
    }

    /**
     * 存储于HBase中的jieba字典表结构
     */
    public static class HBaseJiebaDictSchema {
        public final static String TABLE_NAME = "jieba_dict";
        public final static String NAME_QUALIFIER = "name";
        public final static byte[] INFO_COLUMNFAMILY = Bytes.toBytes("info");
        public final static byte[] WEIGHT_QUALIFIER = Bytes.toBytes("weight");
        public final static byte[] TAG_QUALIFIER = Bytes.toBytes("tag");
    }

    /**
     * 存储于HBase中的同义词字典表结构
     */
    public static class HBaseThesaurusGroupSchema {
        public final static String TABLE_NAME = "thesaurus_group";
        public final static String GROUPID_QUALIFIER = "GROUPID";
        public final static String SYNONYMS_QUALIFIER = "SYNONYMS";
    }

    public static class HBaseThesaurusBelongSchema {
        public final static String TABLE_NAME = "thesaurus_belong";
        public final static String GROUPID_QUALIFIER = "GROUPID";
        public final static String WORD_QUALIFIER = "WORD";
    }
}
