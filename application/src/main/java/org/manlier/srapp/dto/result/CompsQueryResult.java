package org.manlier.srapp.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.entities.Component;

import java.util.Collections;
import java.util.List;

public class CompsQueryResult extends QueryResult<List<Component>> {

    public CompsQueryResult(int numFound, int start, List<Component> data) {
        super(numFound, start, data);
    }

    public CompsQueryResult(List<Component> data) {
        this(data.size(), 0, data);
    }

    public CompsQueryResult(Component data) {
        this(1, 0, Collections.singletonList(data));
    }

    @Override
    public int getNumFound() {
        return getData().size();
    }

    @Override
    @JsonProperty("comps")
    public List<Component> getData() {
        return super.getData();
    }
}
