package org.manlier.srapp.dto.result.thesaurus;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class SynonymsQueryResult extends QueryResult<String> {
    public SynonymsQueryResult(int numFound, int start, List<String> data) {
        super(numFound, start, data);
    }

    public SynonymsQueryResult(int numFound, List<String> data) {
        super(numFound, data);
    }

    public SynonymsQueryResult(List<String> data) {
        super(data);
    }

    @JsonProperty("synonyms")
    @Override
    public List<String> getData() {
        return super.getData();
    }
}
