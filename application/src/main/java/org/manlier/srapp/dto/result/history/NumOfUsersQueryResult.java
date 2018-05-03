package org.manlier.srapp.dto.result.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.domain.NumOfUsers;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class NumOfUsersQueryResult extends QueryResult<NumOfUsers> {
    public NumOfUsersQueryResult(int numFound, int start, List<NumOfUsers> data) {
        super(numFound, start, data);
    }

    public NumOfUsersQueryResult(int numFound, List<NumOfUsers> data) {
        super(numFound, data);
    }

    public NumOfUsersQueryResult(List<NumOfUsers> data) {
        super(data);
    }

    @JsonProperty("numOfUsers")
    @Override
    public List<NumOfUsers> getData() {
        return super.getData();
    }
}
