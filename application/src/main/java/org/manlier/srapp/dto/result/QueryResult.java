package org.manlier.srapp.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.manlier.srapp.dto.result.base.WithQTimeResult;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResult<T> extends WithQTimeResult<List<T>> {

    private Integer numFound;
    private Integer start;

    public QueryResult(int numFound, int start, List<T> data) {
        super(data);
        this.numFound = numFound;
        this.start = start;
    }

    public QueryResult(int numFound, List<T> data) {
        super(data);
        this.numFound = numFound;
    }

    public QueryResult(List<T> data) {
        super(data);
        this.numFound = data.size();
    }

    public Integer getNumFound() {
        return numFound;
    }

    public Integer getStart() {
        return start;
    }



}
