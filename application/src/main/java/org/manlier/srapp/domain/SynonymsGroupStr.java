package org.manlier.srapp.domain;

import java.util.Objects;

public class SynonymsGroupStr {

    private Integer groupId;
    private String synonyms;

    public SynonymsGroupStr(String synonyms) {
        this.synonyms = synonyms;
    }

    public SynonymsGroupStr(Integer groupId, String synonyms) {
        this.groupId = groupId;
        this.synonyms = synonyms;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynonymsGroupStr that = (SynonymsGroupStr) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(synonyms, that.synonyms);
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupId, synonyms);
    }

    @Override
    public String toString() {
        return "SynonymsGroupStr{" +
                "groupId=" + groupId +
                ", synonyms='" + synonyms + '\'' +
                '}';
    }
}
