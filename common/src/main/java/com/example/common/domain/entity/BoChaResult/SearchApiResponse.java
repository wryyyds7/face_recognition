package com.example.common.domain.entity.BoChaResult;

public class SearchApiResponse {
    private int code;
    private String log_id;
    private String msg;
    private SearchResponse data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SearchResponse getData() {
        return data;
    }

    public void setData(SearchResponse data) {
        this.data = data;
    }
}
