package org.manlier.srapp.dto.result.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.domain.HistoryRecord;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class HistoryQueryResultForUser extends QueryResult<HistoryRecord> {
    public HistoryQueryResultForUser(int numFound, int start, List<HistoryRecord> data) {
        super(numFound, start, data);
    }

    public HistoryQueryResultForUser(int numFound, List<HistoryRecord> data) {
        super(numFound, data);
    }

    public HistoryQueryResultForUser(List<HistoryRecord> data) {
        super(data);
    }

    @JsonProperty("history")
    @Override
    public List<HistoryRecord> getData() {
        return super.getData();
    }
}
