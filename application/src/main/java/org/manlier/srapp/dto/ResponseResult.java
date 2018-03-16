package org.manlier.srapp.dto;

import org.manlier.srapp.dto.response.Response;
import org.manlier.srapp.dto.response.ResponseHeader;
import org.manlier.srapp.dto.result.Result;

/**
 * 响应结果类，它的负载为Result对象
 */
public class ResponseResult extends Response<Result> {

    private ResponseResult(ResponseHeader responseHeader, Result response) {
        super(responseHeader, response);
    }

    public static class Builder extends Response.Builder<Result> {

        @Override
        public ResponseResult build() {
            return new ResponseResult(responseHeader, response);
        }
    }
}
