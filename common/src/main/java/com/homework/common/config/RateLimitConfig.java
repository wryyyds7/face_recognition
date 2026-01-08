package com.homework.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 限流配置类
 * 用于配置接口限流规则
 */
@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitConfig {
    
    /**
     * 是否开启限流
     */
    private boolean enabled = true;
    
    /**
     * 默认每分钟允许的请求数
     */
    private int defaultLimitPerMinute = 100;
    
    /**
     * 自定义限流规则，格式：path=limit,path=limit
     */
    private String customRules;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDefaultLimitPerMinute() {
        return defaultLimitPerMinute;
    }

    public void setDefaultLimitPerMinute(int defaultLimitPerMinute) {
        this.defaultLimitPerMinute = defaultLimitPerMinute;
    }

    public String getCustomRules() {
        return customRules;
    }

    public void setCustomRules(String customRules) {
        this.customRules = customRules;
    }
}