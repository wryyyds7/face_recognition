package com.homework.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "hm.auth")
public class AuthProperties {
    List<String> excludePaths;
    List<String> includePaths;

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public List<String> getIncludePaths() {
        return includePaths;
    }

    public void setIncludePaths(List<String> includePaths) {
        this.includePaths = includePaths;
    }
}
