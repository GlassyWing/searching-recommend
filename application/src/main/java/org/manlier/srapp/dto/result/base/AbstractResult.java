package org.manlier.srapp.dto.result.base;

public abstract class AbstractResult<T> implements Result<T> {

    private T data;

    AbstractResult(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

}
