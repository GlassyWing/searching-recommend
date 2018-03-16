package org.manlier.srapp.dto.result;

public class QueryResult<T> implements Result<T> {

    private int numFound;
    private int start;
    private T data;

    public QueryResult(int numFound, int start, T data) {
        this.numFound = numFound;
        this.start = start;
        this.data = data;
    }

    public int getNumFound() {
        return numFound;
    }

    public int getStart() {
        return start;
    }

    public T getData() {
        return data;
    }
}
