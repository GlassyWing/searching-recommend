package org.manlier.srapp.dto.result.thesaurus;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.domain.SynonymsGroup;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class SynonymsGroupQueryResult extends QueryResult<SynonymsGroup> {
    public SynonymsGroupQueryResult(int numFound, List<SynonymsGroup> data) {
        super(numFound, data);
    }

    public SynonymsGroupQueryResult(List<SynonymsGroup> data) {
        super(data);
    }

    @JsonProperty("synonymsGroup")
    @Override
    public List<SynonymsGroup> getData() {
        return super.getData();
    }
}
