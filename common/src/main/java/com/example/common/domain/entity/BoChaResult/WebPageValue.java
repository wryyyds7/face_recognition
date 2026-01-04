package com.example.common.domain.entity.BoChaResult;

public class WebPageValue {
    private String name;
    private String url;
    private String displayUrl;
    private String snippet;
    private String siteName;
    private String siteIcon;
    private String dateLastCrawled;
    private String cachedPageUrl;
    private String language;
    private Boolean isFamilyFriendly;
    private Boolean isNavigational;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteIcon() {
        return siteIcon;
    }

    public void setSiteIcon(String siteIcon) {
        this.siteIcon = siteIcon;
    }

    public String getDateLastCrawled() {
        return dateLastCrawled;
    }

    public void setDateLastCrawled(String dateLastCrawled) {
        this.dateLastCrawled = dateLastCrawled;
    }

    public String getCachedPageUrl() {
        return cachedPageUrl;
    }

    public void setCachedPageUrl(String cachedPageUrl) {
        this.cachedPageUrl = cachedPageUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getFamilyFriendly() {
        return isFamilyFriendly;
    }

    public void setFamilyFriendly(Boolean familyFriendly) {
        isFamilyFriendly = familyFriendly;
    }

    public Boolean getNavigational() {
        return isNavigational;
    }

    public void setNavigational(Boolean navigational) {
        isNavigational = navigational;
    }

    @Override
    public String toString() {
        return "WebPageValue{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", snippet='" + snippet + '\'' +
                ", siteName='" + siteName + '\'' +
                ", displayUrl='" + displayUrl + '\'' +
                "}";
    }
}
