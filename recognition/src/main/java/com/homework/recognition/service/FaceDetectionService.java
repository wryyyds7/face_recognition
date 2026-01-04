package com.homework.recognition.service;

import com.homework.common.domain.entity.User;

import java.util.Map;

/**
 * 人脸检测服务接口
 */
public interface FaceDetectionService {

    /**
     * 实时人脸检测
     * 定时拍照并发送到Python服务进行检测和识别
     */
    void realTimeFaceDetection();

    /**
     * 人脸验证
     *
     * @param img1Path 第一张图片路径
     * @param img2Path 第二张图片路径
     * @return 验证结果
     */
    Map<String, Object> verifyFace(String img1Path, String img2Path);

    /**
     * 人脸识别
     *
     * @param imgPath 待识别图片路径
     * @return 识别结果
     */
    Map<String, Object> recognizeFace(String imgPath);

    /**
     * 拍照并保存
     *
     * @return 照片保存路径
     */
    String takePhoto();
}