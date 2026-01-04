package com.homework.recognition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Python服务配置类
 * 用于读取application.yml中的Python服务相关配置
 */
@Component
@ConfigurationProperties(prefix = "python.port")
public class PythonServiceConfig {

    /**
     * Python服务URL
     */
    private String url;

    /**
     * 人脸数据库路径
     */
    private String dbPath;

    /**
     * 临时文件路径
     */
    private String tempPath;

    /**
     * 清理间隔（毫秒）
     */
    private long cleanupInterval;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public long getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(long cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }
}
