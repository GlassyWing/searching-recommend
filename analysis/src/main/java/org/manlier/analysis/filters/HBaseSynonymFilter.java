package org.manlier.analysis.filters;

import org.manlier.analysis.engines.SynonymEngine;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * 自定义同义词过滤器
 */
public final class HBaseSynonymFilter extends TokenFilter {

	public static final String TOKEN_TYPE_SYNONYM = "SYNONYM";

	// 同义词缓存
	private Stack<String> synonymStack;
	private SynonymEngine engine;
	private AttributeSource.State current;

	private final CharTermAttribute termAttr;
	private final PositionIncrementAttribute posIncAttr;

	/**
	 * Construct a token stream filtering the given input.
	 *
	 * @param input
	 */
	public HBaseSynonymFilter(TokenStream input, SynonymEngine engine) {
		super(input);
		synonymStack = new Stack<>();
		this.engine = engine;
		this.termAttr = addAttribute(CharTermAttribute.class);
		this.posIncAttr = addAttribute(PositionIncrementAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (synonymStack.size() > 0) {
			// 弹出缓存的同义词
			String syn = synonymStack.pop();
			restoreState(current);
			termAttr.copyBuffer(syn.toCharArray(), 0, syn.length());
			posIncAttr.setPositionIncrement(0);
			return true;
		}
		if (!input.incrementToken())
			return false;

		if (addAliasesToStack()) {
			current = captureState();
		}
		return true;
	}

	private boolean addAliasesToStack() throws IOException {
		List<String> synonyms = engine.getSynonyms(termAttr.toString());
		if (synonyms == null) {
			return false;
		}
		for (String synonym : synonyms) {
			synonymStack.push(synonym);
		}
		return true;
	}
}
