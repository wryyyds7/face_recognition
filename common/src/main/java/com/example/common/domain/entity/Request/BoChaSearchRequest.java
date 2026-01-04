package com.example.common.domain.entity.Request;

/**
 * 这里只做两个参数。其他的用默认的即可
 *
 */
public class BoChaSearchRequest {
    private String query;
    private boolean summary;
    private String exclude;
    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }
}
