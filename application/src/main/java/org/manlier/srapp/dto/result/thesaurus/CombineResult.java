package org.manlier.srapp.dto.result.thesaurus;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.domain.SynonymsGroup;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class CombineResult extends QueryResult<SynonymsGroup> {
    public CombineResult(int numFound, int start, List<SynonymsGroup> data) {
        super(numFound, start, data);
    }

    public CombineResult(int numFound, List<SynonymsGroup> data) {
        super(numFound, data);
    }

    public CombineResult(List<SynonymsGroup> data) {
        super(data);
    }

    @JsonProperty("combination")
    @Override
    public List<SynonymsGroup> getData() {
        return super.getData();
    }
}
