package org.manlier.srapp.dto.result.segment;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class SegmentResult extends QueryResult<String> {
    public SegmentResult(int numFound, int start, List<String> data) {
        super(numFound, start, data);
    }

    public SegmentResult(int numFound, List<String> data) {
        super(numFound, data);
    }

    public SegmentResult(List<String> data) {
        super(data);
    }

    @JsonProperty("words")
    @Override
    public List<String> getData() {
        return super.getData();
    }
}
