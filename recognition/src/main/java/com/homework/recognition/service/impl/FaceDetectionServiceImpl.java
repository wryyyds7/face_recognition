package com.homework.recognition.service.impl;

import com.homework.common.feign.PythonPortClient;
import com.homework.common.feign.VoiceSynthesisClient;
import com.homework.recognition.config.FaceRecognitionConfig;
import com.homework.recognition.config.PythonServiceConfig;
import com.homework.recognition.domain.entity.FaceRecognitionLog;
import com.homework.recognition.service.AttendanceService;
import com.homework.recognition.service.FaceDetectionService;
import com.github.sarxos.webcam.Webcam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

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
    private final AttendanceService attendanceService;
    private final VoiceSynthesisClient voiceSynthesisClient;

    // 保存最近识别到的名字
    private String recognizedName;
    
    // 自动检测开关
    private boolean detectionEnabled = false;
    
    // 识别结果保存开关
    private boolean saveResultEnabled = true;

    @Autowired
    public FaceDetectionServiceImpl(PythonPortClient pythonPortClient, FaceRecognitionConfig faceRecognitionConfig, 
                                  PythonServiceConfig pythonServiceConfig, AttendanceService attendanceService,
                                  VoiceSynthesisClient voiceSynthesisClient) {
        this.pythonPortClient = pythonPortClient;
        this.faceRecognitionConfig = faceRecognitionConfig;
        this.pythonServiceConfig = pythonServiceConfig;
        this.attendanceService = attendanceService;
        this.voiceSynthesisClient = voiceSynthesisClient;
        this.recognizedName = null;
    }

    /**
     * 实时人脸检测
     * 定时拍照并发送到Python服务进行检测和识别
     */
    @Scheduled(fixedRateString = "${face.detection.interval}")
    @Override
    public Map<String, Object> realTimeFaceDetection() {
        // 只有当自动检测开关开启时才执行
        if (!detectionEnabled) {
            return null;
        }
        
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
            Map<String, Object> result = handleVerificationResult(verificationResult, photoPath);
            
            // 4. 保存识别日志
            if (saveResultEnabled) {
                saveRecognitionLog(verificationResult, photoPath, result);
            }
            
            return result;
            
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
            Map<String, Object> result = pythonPortClient.verifyByPath(request, null);
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
            // 传入null表示使用默认的参数
            Map<String, Object> result = pythonPortClient.findByPath(request, null);
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
     * @param photoPath 照片路径
     * @return 包含结果代码和识别信息的Map
     */
    private Map<String, Object> handleVerificationResult(Map<String, Object> verificationResult, String photoPath) {
        // 这里可以根据识别结果进行后续处理
        // 例如：更新用户状态、记录识别日志、触发相关业务逻辑等
        log.info("处理验证和识别结果：{}", verificationResult);
        
        Map<String, Object> result = new HashMap<>();
        String speakText = "";
        
        // 处理不同的状态
        String status = (String) verificationResult.get("status");
        if (status != null) {
            switch (status) {
                case "recognized":
                    // 识别成功：返回匹配的文件名
                    String identity = (String) verificationResult.get("identity");
                    log.info("人脸识别成功，匹配到身份：{}", identity);
                    
                    // 从文件名中提取用户名（去掉完整路径和后缀）
                    if (identity != null) {
                        // 先提取文件名（去掉路径）
                        java.io.File identityFile = new java.io.File(identity);
                        String baseName = identityFile.getName();
                        
                        // 再去掉后缀
                        int lastDotIndex = baseName.lastIndexOf('.');
                        if (lastDotIndex > 0) {
                            recognizedName = baseName.substring(0, lastDotIndex);
                        } else {
                            recognizedName = baseName;
                        }
                    }
                    
                    result.put("code", 1);
                    result.put("name", recognizedName);
                    
                    // 保存成功照片到指定目录
                    saveSuccessPhoto(photoPath, recognizedName);
                    
                    // 设置成功语音播报内容
                    speakText = String.format("欢迎%s，识别成功", recognizedName);
                    
                    // TODO: 执行识别成功后的业务逻辑，例如记录考勤、开门等
                    break;

                case "unknown_face":
                    // 存在人脸但不在名单中
                    log.info("检测到人脸但不在数据库中");
                    result.put("code", 2);
                    
                    // 保存失败照片到指定目录
                    saveFailPhoto(photoPath, "unknown_face");
                    
                    // 设置失败语音播报内容
                    speakText = "抱歉，未识别到您的信息，请勿进入";
                    
                    // TODO: 执行未知人脸的业务逻辑，例如记录异常、报警等
                    break;
                case "no_face":
                    // 没有检测到人脸
                    log.info("没有检测到人脸");
                    result.put("code", 3);
                    // 无语音播报
                    // TODO: 执行未检测到人脸的业务逻辑，例如继续检测等
                    break;

                default:
                    // 未知状态
                    log.warn("未知的验证状态：{}", status);
                    result.put("code", 4);
                    // 设置未知状态语音播报内容
                    speakText = "识别失败，请重试";
                    break;

            }
        } else {
            result.put("code", 4);
            // 设置未知状态语音播报内容
            speakText = "识别失败，请重试";
        }
        
        // 异步调用语音合成服务，避免阻塞主线程
        if (!speakText.isEmpty()) {
            String finalSpeakText = speakText;
            CompletableFuture.runAsync(() -> {
                try {
                    voiceSynthesisClient.speak(finalSpeakText, null);
                    log.info("语音播报完成：{}", finalSpeakText);
                } catch (Exception e) {
                    log.error("语音播报失败：{}", e.getMessage(), e);
                }
            });
        }
        
        return result;
    }
    
    /**
     * 保存识别日志
     *
     * @param verificationResult 验证结果
     * @param photoPath 照片路径
     * @param result 处理结果
     */
    private void saveRecognitionLog(Map<String, Object> verificationResult, String photoPath, Map<String, Object> result) {
        try {
            FaceRecognitionLog recognitionLog = new FaceRecognitionLog(); // 重命名变量

            // 设置识别状态
            recognitionLog.setStatus((String) verificationResult.get("status"));

            // 设置识别到的姓名
            recognitionLog.setRecognizedName((String) result.get("name"));

            // 设置照片路径
            recognitionLog.setPhotoPath(photoPath);

            // 设置置信度
            if (verificationResult.containsKey("confidence")) {
                recognitionLog.setConfidence(Double.parseDouble(verificationResult.get("confidence").toString()));
            }

            // 设置人脸数量
            if (verificationResult.containsKey("face_count")) {
                recognitionLog.setFaceCount(Integer.parseInt(verificationResult.get("face_count").toString()));
            }

            // 保存日志
            attendanceService.saveRecognitionLog(recognitionLog);


            log.info("保存人脸识别人物识别日志成功：{}", recognitionLog.getLogId());
        } catch (Exception e) {
            log.error("保存人脸识别人物识别日志失败：{}", e.getMessage(), e);
        }
    }
    
    /**
     * 保存成功照片到指定目录
     *
     * @param originalPath 原始照片路径
     * @param name 识别到的姓名
     */
    private void saveSuccessPhoto(String originalPath, String name) {
        try {
            // 获取成功照片保存路径
            String successPath = faceRecognitionConfig.getSuccessPhotoPath();
            if (successPath == null || successPath.isEmpty()) {
                log.warn("未配置成功照片保存路径，跳过保存");
                return;
            }
            
            // 确保保存目录存在
            Path saveDir = Paths.get(successPath);
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }
            
            // 生成新的文件名：当前时间_姓名.jpg
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String fileName = timestamp + "_" + name + ".jpg";
            // 清理文件名，移除Windows文件系统不允许的字符
            String cleanFileName = fileName.replaceAll("[<>:\"/\\|?*]", "_");
            Path targetPath = saveDir.resolve(cleanFileName);
            
            // 复制文件
            Files.copy(Paths.get(originalPath), targetPath);
            
            log.info("保存识别成功照片到：{}", targetPath.toString());
        } catch (IOException e) {
            log.error("保存识别成功照片失败：{}", e.getMessage(), e);
        }
    }
    
    /**
     * 保存失败照片到指定目录
     *
     * @param originalPath 原始照片路径
     * @param status 识别状态
     */
    private void saveFailPhoto(String originalPath, String status) {
        try {
            // 获取失败照片保存路径
            String failPath = faceRecognitionConfig.getFailPhotoPath();
            if (failPath == null || failPath.isEmpty()) {
                log.warn("未配置失败照片保存路径，跳过保存");
                return;
            }
            
            // 确保保存目录存在
            Path saveDir = Paths.get(failPath);
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }
            
            // 生成新的文件名：当前时间_状态.jpg
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String fileName = timestamp + "_" + status + ".jpg";
            // 清理文件名，移除Windows文件系统不允许的字符
            String cleanFileName = fileName.replaceAll("[<>:\"/\\|?*]", "_");
            Path targetPath = saveDir.resolve(cleanFileName);
            
            // 复制文件
            Files.copy(Paths.get(originalPath), targetPath);
            
            log.info("保存识别失败照片到：{}", targetPath.toString());
        } catch (IOException e) {
            log.error("保存识别失败照片失败：{}", e.getMessage(), e);
        }
    }
    
    /**
     * 开启自动检测
     */
    public void startDetection() {
        this.detectionEnabled = true;
        log.info("自动检测已开启");
    }
    
    /**
     * 停止自动检测
     */
    public void stopDetection() {
        this.detectionEnabled = false;
        log.info("自动检测已停止");
    }
    
    /**
     * 获取自动检测状态
     *
     * @return 自动检测状态
     */
    public boolean getDetectionStatus() {
        return this.detectionEnabled;
    }
    
    /**
     * 开启识别结果保存
     */
    public void enableSaveResult() {
        this.saveResultEnabled = true;
        log.info("识别结果保存已开启");
    }
    
    /**
     * 关闭识别结果保存
     */
    public void disableSaveResult() {
        this.saveResultEnabled = false;
        log.info("识别结果保存已关闭");
    }
    
    /**
     * 单次人脸检测
     * 仅执行一次人脸检测和识别，不依赖自动检测开关
     */
    @Override
    public Map<String, Object> singleDetect() {
        try {
            log.info("开始单次人脸检测");
            
            // 1. 拍照并保存
            String photoPath = takePhoto();
            log.info("拍照成功，保存路径：{}", photoPath);
            
            // 2. 发送到Python服务进行人脸验证和识别
            Map<String, Object> verificationResult = verifyFace(photoPath);
            log.info("人脸验证和识别结果：{}", verificationResult);
            
            // 3. 处理验证和识别结果
            Map<String, Object> result = handleVerificationResult(verificationResult, photoPath);
            
            // 4. 保存识别日志
            if (saveResultEnabled) {
                saveRecognitionLog(verificationResult, photoPath, result);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("单次人脸检测失败：{}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 5);
            return result;
        }
    }
}
