package org.manlier.srapp.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.constraints.Error;
import org.manlier.srapp.dto.result.base.Result;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResult implements Result<List<Error>> {

    private List<Error> errors;

    public ErrorResult() {
        this.errors = new LinkedList<>();
    }

    public ErrorResult(Error error) {
        this.errors = new LinkedList<>();
        this.errors.add(error);
    }

    public ErrorResult(List<Error> errors) {
        this.errors = errors;
    }

    public void addError(Error error) {
        this.errors.add(error);
    }

    @Override
    @JsonProperty("errors")
    public List<Error> getData() {
        return errors;
    }
}
