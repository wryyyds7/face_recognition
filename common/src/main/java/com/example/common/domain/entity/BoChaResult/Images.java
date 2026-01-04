package com.example.common.domain.entity.BoChaResult;

import java.util.List;

public class Images {
    private String id;
    private String readLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReadLink() {
        return readLink;
    }

    public void setReadLink(String readLink) {
        this.readLink = readLink;
    }

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public List<ImageValue> getValue() {
        return value;
    }

    public void setValue(List<ImageValue> value) {
        this.value = value;
    }

    private String webSearchUrl;
    private List<ImageValue> value;


}