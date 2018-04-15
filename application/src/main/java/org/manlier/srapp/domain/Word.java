package org.manlier.srapp.domain;

import java.util.Objects;

/**
 * 单词
 */
public class Word {

	private String name;        // 单词的名称
	private int weight;    // （在所有单词中所占的）权重
	private String wordClass;   // 词性

	public Word(String name) {
		this.name = name;
	}

	public Word(String name, int weight) {
		this.name = name;
		this.weight = weight;
	}

	public Word(String name, int weight, String wordClass) {
		this.name = name;
		this.weight = weight;
		this.wordClass = wordClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Word)) return false;
		Word word = (Word) o;
		return weight == word.weight &&
				Objects.equals(name, word.name) &&
				Objects.equals(wordClass, word.wordClass);
	}

	@Override
	public int hashCode() {

		return Objects.hash(name, weight, wordClass);
	}

	@Override
	public String toString() {
		return "Word{" +
				"name='" + name + '\'' +
				", weight=" + weight +
				", wordClass='" + wordClass + '\'' +
				'}';
	}
}
