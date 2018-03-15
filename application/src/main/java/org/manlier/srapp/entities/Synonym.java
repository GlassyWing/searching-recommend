package org.manlier.srapp.entities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * 同义词
 */
public class Synonym {

	private Queue<Word> synonyms;

	public Synonym(Word... synonyms) {
		this.synonyms = new LinkedList<>(Arrays.asList(synonyms));
	}

	public Synonym(Queue<Word> synonyms) {
		this.synonyms = synonyms;
	}

	public Queue<Word> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Queue<Word> synonyms) {
		this.synonyms = synonyms;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Synonym)) return false;
		Synonym synonym1 = (Synonym) o;
		return Objects.equals(synonyms, synonym1.synonyms);
	}

	@Override
	public int hashCode() {

		return Objects.hash(synonyms);
	}

	@Override
	public String toString() {
		return "Synonym{" +
				"synonyms=" + synonyms +
				'}';
	}
}
