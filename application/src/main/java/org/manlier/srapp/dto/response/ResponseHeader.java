package org.manlier.srapp.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseHeader {

    private Map<String, Object> headers;
    private Map<String, Object> params;
    private String path;
    private String requestUrl;
    private int status;

    public ResponseHeader() {
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public int getStatus() {
        return status;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void addHeader(String name, Object value) {
        this.headers.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getHeaders() {
        return headers;
    }
}
