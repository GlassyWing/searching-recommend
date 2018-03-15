package org.manlier.analysis.tokenizers;

import org.apache.lucene.analysis.Tokenizer;

import java.io.IOException;

/**
 * 自定义jieba分词器
 */
public class JiebaTokenizer extends Tokenizer {


	@Override
	public boolean incrementToken() throws IOException {
		return false;
	}
}
