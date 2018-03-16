package org.manlier.srapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.manlier.srapp.dto.result.Result;

import java.util.Date;
import java.util.Map;

/**
 * 返回给前台的响应对象
 *
 * @param <T>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    // 响应头
    private ResponseHeader responseHeader;
    // 响应的负载
    private T response;

    protected Response(ResponseHeader responseHeader, T response) {
        this.responseHeader = responseHeader;
        this.response = response;
    }


    /**
     * 设置响应头
     *
     * @param responseHeader 响应头
     */
    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    /**
     * 设置响应的负载
     *
     * @param response 负载
     */
    public void setResponse(T response) {
        this.response = response;
    }

    public T getResponse() {
        return response;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    /**
     * 响应对象构件器
     *
     * @param <T>
     */
    public static class Builder<T> {

        protected ResponseHeader responseHeader;
        protected T response;
        private long startTime;

        public Builder() {
            this.responseHeader = new ResponseHeader();
        }

        public Builder<T> startTiming() {
            this.startTime = new Date().getTime();
            return this;
        }

        public Builder<T> endTime() {
            this.responseHeader.addHeader("QTime", new Date().getTime() - startTime);
            return this;
        }

        public Builder<T> setPathVariable(String pathVariable) {
            this.responseHeader.setPath(pathVariable);
            return this;
        }

        public Builder<T> setRequestUrl(String url) {
            this.responseHeader.setRequestUrl(url);
            return this;
        }

        /**
         * 添加响应头
         *
         * @param name  头部名
         * @param value 头部值
         * @return 本身
         */
        public Builder<T> addResponseHeader(String name, Object value) {
            this.responseHeader.addHeader(name, value);
            return this;
        }

        /**
         * 设置响应头的状态标识
         *
         * @param status 状态
         * @return 本身
         */
        public Builder<T> setResponseHeaderStatus(int status) {
            this.responseHeader.setStatus(status);
            return this;
        }

        /**
         * 添加参数到param响应头
         *
         * @param param 参数名
         * @param value 参数值
         * @return 本身
         */
        public Builder<T> addResponseHeaderParam(String param, Object value) {
            this.responseHeader.getParams().put(param, value);
            return this;
        }

        /**
         * 直接设置param响应头
         *
         * @param params param响应头
         * @return 本身
         */
        public Builder<T> setResponseHeaderParams(Map<String, Object> params) {
            this.responseHeader.setParams(params);
            return this;
        }

        /**
         * 设置负载
         *
         * @param response 负载
         * @return 本身
         */
        public Builder<T> setResponse(T response) {
            this.response = response;
            return this;
        }

        /**
         * 创建响应对象
         *
         * @return 响应对象
         */
        public Response<T> build() {
            return new Response<>(responseHeader, response);
        }
    }
}
