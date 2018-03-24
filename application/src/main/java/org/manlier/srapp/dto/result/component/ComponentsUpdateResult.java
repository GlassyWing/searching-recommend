package org.manlier.srapp.dto.result.component;

import org.manlier.srapp.dto.result.UpdateResult;
import org.manlier.srapp.entities.Component;

import java.util.List;

public class ComponentsUpdateResult extends UpdateResult<Component> {

    public ComponentsUpdateResult(List<Component> data) {
        super(data);
    }

}
