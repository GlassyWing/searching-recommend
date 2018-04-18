package org.manlier.srapp.dto.result.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class HistoryQueryResult extends QueryResult<String> {
    public HistoryQueryResult(int numFound, int start, List<String> data) {
        super(numFound, start, data);
    }

    public HistoryQueryResult(int numFound, List<String> data) {
        super(numFound, data);
    }

    public HistoryQueryResult(List<String> data) {
        super(data);
    }

    @JsonProperty("records")
    @Override
    public List<String> getData() {
        return super.getData();
    }
}
