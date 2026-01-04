package com.homework.recognition.service;

/**
 * 人脸数据清理服务接口
 * 用于定期清理临时文件和检测文件夹
 */
public interface FaceDataCleanupService {

    /**
     * 清理Python服务的临时文件
     */
    void cleanupTempFiles();

    /**
     * 清理检测文件夹
     */
    void cleanupDetectionFiles();

    /**
     * 清理过期的人脸数据
     */
    void cleanupExpiredFaceData();

    /**
     * 执行全部清理操作
     */
    void executeAllCleanup();
}