package org.manlier.srapp.dto.result.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.dto.result.QueryResult;
import org.manlier.srapp.entities.Component;

import java.util.List;

public class ComponentsQueryResult extends QueryResult<Component> {

    public ComponentsQueryResult(int numFound, int start, List<Component> data) {
        super(numFound, start, data);
    }

    public ComponentsQueryResult(int numFound, List<Component> data) {
        super(numFound, data);
    }

    public ComponentsQueryResult(List<Component> data) {
        super(data);
    }

    @JsonProperty("comps")
    @Override
    public List<Component> getData() {
        return super.getData();
    }
}
