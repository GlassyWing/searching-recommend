package org.manlier.common.schemes;

import org.apache.hadoop.hbase.util.Bytes;

public class HBaseSynonymQuery {

	public final static String TABLE_NAME = "thesaurus";
	public final static byte[] SYNONYMS_COLUMNFAMILY = Bytes.toBytes("synonyms");

}
