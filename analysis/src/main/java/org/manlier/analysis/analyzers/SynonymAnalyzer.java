package org.manlier.analysis.analyzers;

import org.manlier.analysis.engines.SynonymEngine;
import org.manlier.analysis.filters.HBaseSynonymFilter;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;


public class SynonymAnalyzer extends Analyzer {

	private SynonymEngine engine;

	public SynonymAnalyzer(SynonymEngine engine) {
		this.engine = engine;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new HMMChineseTokenizer();
		TokenStream tokenStream = new HBaseSynonymFilter(tokenizer, engine);
		tokenStream = new LowerCaseFilter(tokenStream);
		return new TokenStreamComponents(tokenizer, tokenStream);
	}
}
