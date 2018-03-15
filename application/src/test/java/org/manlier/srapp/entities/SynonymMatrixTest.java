package org.manlier.srapp.entities;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SynonymMatrixTest {

	private SynonymMatrix matrix1;
	private SynonymMatrix matrix2;

	@Before
	public void setUp() {
		Map<Word, Float> subMap = new HashMap<>();
		subMap.put(new Word("一贯"), 1f);
		subMap.put(new Word("不断"), 1f);
		subMap.put(new Word("连续"), 1f);
		Map<Word, Map<Word, Float>> map = new HashMap<>();
		map.put(new Word("一直"), subMap);
		matrix1 = new SynonymMatrix(map);

		Synonym synonym = new Synonym(new Word("一贯"),
				new Word("不断"),
				new Word("连续"),
				new Word("一直"));
		matrix2 = new SynonymMatrix(synonym);
	}

	@Test
	public void iterator() {
		matrix1.forEach(tuple -> {
			System.out.printf("%s - %f - %s\n", tuple.first.getName(), tuple.third, tuple.second.getName());
		});
	}

	@Test
	public void iterator2() {
		matrix2.forEach(tuple -> {
			System.out.printf("%s - %f - %s\n", tuple.first.getName(), tuple.third, tuple.second.getName());
		});
	}
}