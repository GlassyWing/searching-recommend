package org.manlier.srapp.dto.result;

import org.manlier.srapp.entities.Component;

public class CompsUpdateResult extends UpdateResult<Component> {

    public CompsUpdateResult(Component old, Component now) {
        super(old, now);
    }

    @Override
    public void resolve(Component old, Component now) {
        if (!old.getDesc().equals(now.getDesc()))
            changes.put("desc", now.getDesc());
    }
}
