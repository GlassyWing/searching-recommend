package org.manlier.srapp.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UpdateResult<R> extends QueryResult<R> {


    public UpdateResult(List<R> data) {
        super(data);
    }

    @JsonProperty("changes")
    @Override
    public List<R> getData() {
        return super.getData();
    }
}
