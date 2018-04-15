package org.manlier.srapp.domain;


import org.manlier.common.utils.ThreeTuple;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * 同义词矩阵，矩阵的键为单词，矩阵的值为两个单词之间的关联度0-1
 */
public class SynonymMatrix implements Iterable<ThreeTuple<Word, Word, Float>> {

	private float initRelation;
	private Synonym synonym;
	private Map<Word, Map<Word, Float>> matrix;

	public SynonymMatrix(Synonym synonym, float initRelation) {
		this.synonym = synonym;
		this.initRelation = initRelation;
	}

	public SynonymMatrix(Map<Word, Map<Word, Float>> matrix) {
		this.matrix = matrix;
	}

	public SynonymMatrix(Synonym synonym) {
		this(synonym, 1);
	}


	@Override
	public Iterator<ThreeTuple<Word, Word, Float>> iterator() {

		// 处理数据为矩阵的情况
		if (matrix != null) {

			Iterator<Map.Entry<Word, Map<Word, Float>>> iterator = matrix.entrySet().iterator();

			return new Iterator<ThreeTuple<Word, Word, Float>>() {

				// 指示iterator是否需要进行下一次迭代
				private boolean needNext = iterator.hasNext();
				private Map.Entry<Word, Map<Word, Float>> outEntry;
				private Iterator<Map.Entry<Word, Float>> innerIterator;
				// copy 将k1->k2的关联度反转为k2->k1的关联度
				private ThreeTuple<Word, Word, Float> copy;
				// 指示是否需要将k1->k2反转
				private boolean needReverse;

				{
					// 进行首次迭代
					if (needNext) {
						outEntry = iterator.next();
						innerIterator = outEntry.getValue().entrySet().iterator();
						needNext = false;
					}
				}

				@Override
				public boolean hasNext() {
					// 查看是否有copy或其它数据
					return needReverse || innerIterator.hasNext();
				}

				@Override
				public ThreeTuple<Word, Word, Float> next() {
					// 如果有copy直接返回
					if (needReverse) {
						needReverse = false;
						return copy;
					}
					// 进行iterator的下次迭代
					if (needNext) {
						outEntry = iterator.next();
						innerIterator = outEntry.getValue().entrySet().iterator();
						needNext = false;
					}
					Map.Entry<Word, Float> innerEntry = innerIterator.next();
					if (!innerIterator.hasNext()) {
						needNext = true;
					}
					copy = new ThreeTuple<>(innerEntry.getKey(), outEntry.getKey(), innerEntry.getValue());
					needReverse = true;
					return new ThreeTuple<>(outEntry.getKey(), innerEntry.getKey(), innerEntry.getValue());
				}
			};
		}

		// 处理数据为synonyms的情况
		return new Iterator<ThreeTuple<Word, Word, Float>>() {

			private Queue<Word> words = synonym.getSynonyms();
			private int len = words.size();
			private boolean needNext = len != 0;
			private int count = 0;
			private Word word;
			private Iterator<Word> innerIterator;

			{
				if (needNext) {
					++count;
					word = words.poll();
					innerIterator = words.iterator();
					needNext = false;
				}
			}

			@Override
			public boolean hasNext() {
				return innerIterator.hasNext() || count < len;
			}

			@Override
			public ThreeTuple<Word, Word, Float> next() {
				if (needNext) {
					++count;
					words.offer(word);
					word = words.poll();
					innerIterator = words.iterator();
					needNext = false;
				}

				Word nextWord = innerIterator.next();
				if (!innerIterator.hasNext()) {
					needNext = true;
				}

				return new ThreeTuple<>(word, nextWord, initRelation);
			}
		};
	}

}
