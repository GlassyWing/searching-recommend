package org.manlier.srapp.domain;

import org.manlier.srapp.thesaurus.SynonymsConvertor;

import java.util.Objects;
import java.util.Set;

public class SynonymsGroup {

    private Integer groupId;
    private Set<String> synonyms;

    public SynonymsGroup(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    public SynonymsGroup(Integer groupId, Set<String> synonyms) {
        this.groupId = groupId;
        this.synonyms = synonyms;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynonymsGroup that = (SynonymsGroup) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(synonyms, that.synonyms);
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupId, synonyms);
    }

    @Override
    public String toString() {
        return "SynonymsGroup{" +
                "groupId=" + groupId +
                ", synonyms=" + synonyms +
                '}';
    }

    public SynonymsGroupStr toSynonymsGroupStr() {
        return new SynonymsGroupStr(groupId, SynonymsConvertor.parseToString(synonyms));
    }
}
