package com.homework.recognition.service.impl;

import com.homework.common.feign.PythonPortClient;
import com.homework.recognition.config.FaceRecognitionConfig;
import com.homework.recognition.config.PythonServiceConfig;
import com.homework.recognition.service.FaceDetectionService;
import com.github.sarxos.webcam.Webcam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    // 保存最近识别到的名字
    private String recognizedName;

    @Autowired
    public FaceDetectionServiceImpl(PythonPortClient pythonPortClient, FaceRecognitionConfig faceRecognitionConfig, PythonServiceConfig pythonServiceConfig) {
        this.pythonPortClient = pythonPortClient;
        this.faceRecognitionConfig = faceRecognitionConfig;
        this.pythonServiceConfig = pythonServiceConfig;
        this.recognizedName = null;
    }

    /**
     * 实时人脸检测
     * 定时拍照并发送到Python服务进行检测和识别
     */
    @Scheduled(fixedRateString = "${face.detection.interval}")
    @Override
    public Map<String, Object> realTimeFaceDetection() {
        try {
            log.info("开始实时人脸检测");
            
            // 1. 拍照并保存
            String photoPath = takePhoto();
            log.info("拍照成功，保存路径：{}", photoPath);
            
            // 2. 发送到Python服务进行人脸验证和识别
            // verifyFace会调用Python的verify接口，该接口会自动检测人脸并进行识别
            Map<String, Object> verificationResult = verifyFace(photoPath);
            log.info("人脸验证和识别结果：{}", verificationResult);
            
            // 3. 处理验证和识别结果
            return handleVerificationResult(verificationResult);
            
        } catch (Exception e) {
            log.error("实时人脸检测失败：{}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 5);
            return result;
        }
    }

    /**
     * 人脸验证
     * 检测单张图片是否有人脸
     *
     * @param imgPath 待检测图片路径
     * @return 验证结果
     */
    @Override
    public Map<String, Object> verifyFace(String imgPath) {
        try {
            log.info("开始人脸验证，图片路径：{}", imgPath);

            // 构建请求参数
            Map<String, String> request = new HashMap<>();
            request.put("img_path", imgPath);
            
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
            // 1. 获取摄像头实例
            Webcam webcam = Webcam.getDefault();
            if (webcam == null) {
                throw new RuntimeException("未检测到可用摄像头");
            }
            
            // 2. 打开摄像头
            webcam.open();
            
            // 3. 捕获图像
            BufferedImage image = webcam.getImage();
            if (image == null) {
                webcam.close();
                throw new RuntimeException("摄像头捕获图像失败");
            }
            
            // 4. 生成唯一文件名，使用时间戳
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String fileName = "face_" + timestamp + ".jpg";
            
            // 5. 确保保存目录存在
            Path saveDir = Paths.get(faceRecognitionConfig.getPhotoPath());
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }
            
            // 6. 照片保存路径
            Path photoPath = saveDir.resolve(fileName);
            
            // 7. 保存图像到文件
            ImageIO.write(image, "JPG", photoPath.toFile());
            
            // 8. 关闭摄像头
            webcam.close();
            
            log.info("照片保存成功：{}", photoPath.toString());
            return photoPath.toString();
        } catch (IOException e) {
            log.error("拍照失败：{}", e.getMessage(), e);
            throw new RuntimeException("拍照失败", e);
        } catch (RuntimeException e) {
            log.error("摄像头操作失败：{}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理验证和识别结果
     *
     * @param verificationResult 验证和识别结果
     * @return 包含结果代码和识别信息的Map
     */
    private Map<String, Object> handleVerificationResult(Map<String, Object> verificationResult) {
        // 这里可以根据识别结果进行后续处理
        // 例如：更新用户状态、记录识别日志、触发相关业务逻辑等
        log.info("处理验证和识别结果：{}", verificationResult);
        
        Map<String, Object> result = new HashMap<>();
        
        // 处理不同的状态
        String status = (String) verificationResult.get("status");
        if (status != null) {
            switch (status) {
                case "recognized":
                    // 识别成功：返回匹配的文件名
                    String identity = (String) verificationResult.get("identity");
                    log.info("人脸识别成功，匹配到身份：{}", identity);
                    
                    // 从文件名中提取用户名（去掉后缀）
                    if (identity != null) {
                        int lastDotIndex = identity.lastIndexOf('.');
                        if (lastDotIndex > 0) {
                            recognizedName = identity.substring(0, lastDotIndex);
                        } else {
                            recognizedName = identity;
                        }
                    }
                    
                    result.put("code", 1);
                    result.put("name", recognizedName);
                    // TODO: 执行识别成功后的业务逻辑，例如记录考勤、开门等
                    break;

                case "unknown_face":
                    // 存在人脸但不在名单中
                    log.info("检测到人脸但不在数据库中");
                    result.put("code", 2);
                    // TODO: 执行未知人脸的业务逻辑，例如记录异常、报警等
                    break;
                case "no_face":
                    // 没有检测到人脸
                    log.info("没有检测到人脸");
                    result.put("code", 3);
                    // TODO: 执行未检测到人脸的业务逻辑，例如继续检测等
                    break;

                default:
                    // 未知状态
                    log.warn("未知的验证状态：{}", status);
                    result.put("code", 4);
                    break;

            }
        } else {
            result.put("code", 4);
        }
        return result;
    }
}
