package org.manlier.srapp.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.entities.Component;

public class CompsAddResult implements Result<Component> {

    private Component component;

    public CompsAddResult(Component component) {
        this.component = component;
    }

    @JsonProperty("component")
    public Component getData() {
        return component;
    }
}
