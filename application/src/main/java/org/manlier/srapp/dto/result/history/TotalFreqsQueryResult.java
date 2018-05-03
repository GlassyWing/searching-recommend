package org.manlier.srapp.dto.result.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.domain.TotalFreq;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class TotalFreqsQueryResult extends QueryResult<TotalFreq> {
    public TotalFreqsQueryResult(int numFound, int start, List<TotalFreq> data) {
        super(numFound, start, data);
    }

    public TotalFreqsQueryResult(int numFound, List<TotalFreq> data) {
        super(numFound, data);
    }

    public TotalFreqsQueryResult(List<TotalFreq> data) {
        super(data);
    }

    @JsonProperty("totalFreqs")
    @Override
    public List<TotalFreq> getData() {
        return super.getData();
    }
}
