package com.example.common.domain.entity.BoChaResult;

import java.util.List;

public class WebPages {
    private String webSearchUrl;
    private long totalEstimatedMatches;
    private List<WebPageValue> value;
    private boolean someResultsRemoved;

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public long getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public void setTotalEstimatedMatches(long totalEstimatedMatches) {
        this.totalEstimatedMatches = totalEstimatedMatches;
    }

    public List<WebPageValue> getValue() {
        return value;
    }

    public void setValue(List<WebPageValue> value) {
        this.value = value;
    }

    public boolean isSomeResultsRemoved() {
        return someResultsRemoved;
    }

    public void setSomeResultsRemoved(boolean someResultsRemoved) {
        this.someResultsRemoved = someResultsRemoved;
    }
}
