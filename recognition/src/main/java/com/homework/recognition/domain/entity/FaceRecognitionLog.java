package com.homework.recognition.domain.entity;

import com.homework.common.domain.entity.BaseEntity;

import java.util.Date;

/**
 * 人脸识别人物识别日志
 *
 * @author homework
 */
public class FaceRecognitionLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 识别时间 */
    private Date recognitionTime;

    /** 识别状态 */
    private String status;

    /** 识别到的姓名 */
    private String recognizedName;

    /** 照片路径 */
    private String photoPath;

    /** 置信度 */
    private Double confidence;

    /** 人脸数量 */
    private Integer faceCount;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Date getRecognitionTime() {
        return recognitionTime;
    }

    public void setRecognitionTime(Date recognitionTime) {
        this.recognitionTime = recognitionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecognizedName() {
        return recognizedName;
    }

    public void setRecognizedName(String recognizedName) {
        this.recognizedName = recognizedName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Integer getFaceCount() {
        return faceCount;
    }

    public void setFaceCount(Integer faceCount) {
        this.faceCount = faceCount;
    }
}