package org.manlier.srapp.domain;

import java.util.Objects;

/**
 * 单词
 */
public class Word {

    private String name;        // 单词的名称
    private long weight;    // （在所有单词中所占的）权重
    private String tag;   // 词性

    public Word(String name) {
        this.name = name;
    }

    public Word(String name, long weight) {
        this.name = name;
        this.weight = weight;
    }

    public Word(String name, long weight, String tag) {
        this.name = name;
        this.weight = weight;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return weight == word.weight &&
                Objects.equals(name, word.name) &&
                Objects.equals(tag, word.tag);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, weight, tag);
    }

    @Override
    public String toString() {
        return "Word{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                ", tag='" + tag + '\'' +
                '}';
    }
}
