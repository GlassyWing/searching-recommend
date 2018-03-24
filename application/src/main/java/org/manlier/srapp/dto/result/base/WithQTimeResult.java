package org.manlier.srapp.dto.result.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithQTimeResult<T> extends AbstractResult<T> {

    private Long QTime;

    public WithQTimeResult(T data) {
        super(data);
    }

    public void setQTime(Long QTime) {
        this.QTime = QTime;
    }

    public Long getQTime() {
        return QTime;
    }

    private long now() {
        return new Date().getTime();
    }

}
