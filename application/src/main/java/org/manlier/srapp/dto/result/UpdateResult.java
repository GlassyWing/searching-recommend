package org.manlier.srapp.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public abstract class UpdateResult<R> implements Result<Map<String, Object>> {

    protected final Map<String, Object> changes;

    public UpdateResult(R old, R now) {
        this.changes = new HashMap<>();
        this.resolve(old, now);
    }

    public abstract void resolve(R old, R now);


    @JsonProperty("changes")
    public Map<String, Object> getData() {
        return changes;
    }
}
