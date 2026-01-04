package com.homework.recognition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 人脸识别配置类
 * 用于读取application.yml中的人脸识别相关配置
 */
@Component
@ConfigurationProperties(prefix = "face.detection")
public class FaceRecognitionConfig {

    /**
     * 检测间隔（毫秒）
     */
    private long interval;

    /**
     * 拍照保存路径
     */
    private String photoPath;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
