package com.homework.recognition.service.impl;

import com.homework.common.feign.PythonPortClient;
import com.homework.recognition.config.FaceRecognitionConfig;
import com.homework.recognition.config.PythonServiceConfig;
import com.homework.recognition.service.FaceDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 人脸检测服务实现类
 */
@Service
public class FaceDetectionServiceImpl implements FaceDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FaceDetectionServiceImpl.class);

    private final PythonPortClient pythonPortClient;
    private final FaceRecognitionConfig faceRecognitionConfig;
    private final PythonServiceConfig pythonServiceConfig;

    @Autowired
    public FaceDetectionServiceImpl(PythonPortClient pythonPortClient, FaceRecognitionConfig faceRecognitionConfig, PythonServiceConfig pythonServiceConfig) {
        this.pythonPortClient = pythonPortClient;
        this.faceRecognitionConfig = faceRecognitionConfig;
        this.pythonServiceConfig = pythonServiceConfig;
    }

    /**
     * 实时人脸检测
     * 定时拍照并发送到Python服务进行检测和识别
     */
    @Scheduled(fixedRateString = "${face.detection.interval}")
    @Override
    public void realTimeFaceDetection() {
        try {
            log.info("开始实时人脸检测");
            
            // 1. 拍照并保存
            String photoPath = takePhoto();
            log.info("拍照成功，保存路径：{}", photoPath);
            
            // 2. 发送到Python服务进行人脸识别
            Map<String, Object> recognitionResult = recognizeFace(photoPath);
            log.info("人脸识别结果：{}", recognitionResult);
            
            // 3. 处理识别结果
            handleRecognitionResult(recognitionResult);
            
        } catch (Exception e) {
            log.error("实时人脸检测失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 人脸验证
     *
     * @param img1Path 第一张图片路径
     * @param img2Path 第二张图片路径
     * @return 验证结果
     */
    @Override
    public Map<String, Object> verifyFace(String img1Path, String img2Path) {
        try {
            log.info("开始人脸验证，图片1：{}，图片2：{}", img1Path, img2Path);
            
            // 构建请求参数
            Map<String, String> request = new HashMap<>();
            request.put("img1_path", img1Path);
            request.put("img2_path", img2Path);
            
            // 调用Python服务
            Map<String, Object> result = pythonPortClient.verifyByPath(request);
            log.info("人脸验证结果：{}", result);
            
            return result;
        } catch (Exception e) {
            log.error("人脸验证失败：{}", e.getMessage(), e);
            throw new RuntimeException("人脸验证失败", e);
        }
    }

    /**
     * 人脸识别
     *
     * @param imgPath 待识别图片路径
     * @return 识别结果
     */
    @Override
    public Map<String, Object> recognizeFace(String imgPath) {
        try {
            log.info("开始人脸识别，图片路径：{}", imgPath);
            
            // 构建请求参数
            Map<String, String> request = new HashMap<>();
            request.put("img_path", imgPath);
            request.put("db_path", pythonServiceConfig.getDbPath());
            
            // 调用Python服务
            Map<String, Object> result = pythonPortClient.findByPath(request);
            log.info("人脸识别结果：{}", result);
            
            return result;
        } catch (Exception e) {
            log.error("人脸识别失败：{}", e.getMessage(), e);
            throw new RuntimeException("人脸识别失败", e);
        }
    }

    /**
     * 拍照并保存
     *
     * @return 照片保存路径
     */
    @Override
    public String takePhoto() {
        try {
            // 这里模拟拍照，实际项目中应该调用摄像头API
            // 生成唯一文件名，使用时间戳
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String fileName = "face_" + timestamp + ".jpg";
            
            // 确保保存目录存在
            Path saveDir = Paths.get(faceRecognitionConfig.getPhotoPath());
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }
            
            // 照片保存路径
            Path photoPath = saveDir.resolve(fileName);
            
            // 这里模拟拍照，实际项目中应该调用摄像头API获取图片数据
            // 生成一个空文件作为示例
            Files.createFile(photoPath);
            
            log.info("照片保存成功：{}", photoPath.toString());
            return photoPath.toString();
        } catch (IOException e) {
            log.error("拍照失败：{}", e.getMessage(), e);
            throw new RuntimeException("拍照失败", e);
        }
    }

    /**
     * 处理识别结果
     *
     * @param recognitionResult 识别结果
     */
    private void handleRecognitionResult(Map<String, Object> recognitionResult) {
        // 这里可以根据识别结果进行后续处理
        // 例如：更新用户状态、记录识别日志、触发相关业务逻辑等
        log.info("处理识别结果：{}", recognitionResult);
    }
}
