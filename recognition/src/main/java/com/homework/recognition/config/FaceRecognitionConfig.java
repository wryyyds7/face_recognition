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

    /**
     * 识别成功照片保存路径
     */
    private String successPhotoPath;

    /**
     * 识别失败照片保存路径
     */
    private String failPhotoPath;

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

    public String getSuccessPhotoPath() {
        return successPhotoPath;
    }

    public void setSuccessPhotoPath(String successPhotoPath) {
        this.successPhotoPath = successPhotoPath;
    }

    public String getFailPhotoPath() {
        return failPhotoPath;
    }

    public void setFailPhotoPath(String failPhotoPath) {
        this.failPhotoPath = failPhotoPath;
    }
}
