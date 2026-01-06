package com.homework.recognition.service.impl;

import com.homework.recognition.config.FaceRecognitionConfig;
import com.homework.recognition.config.PythonServiceConfig;
import com.homework.recognition.service.FaceDataCleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 人脸数据清理服务实现类
 */
@Service
public class FaceDataCleanupServiceImpl implements FaceDataCleanupService {

    private static final Logger log = LoggerFactory.getLogger(FaceDataCleanupServiceImpl.class);

    private final PythonServiceConfig pythonServiceConfig;
    private final FaceRecognitionConfig faceRecognitionConfig;

    @Autowired
    public FaceDataCleanupServiceImpl(PythonServiceConfig pythonServiceConfig, FaceRecognitionConfig faceRecognitionConfig) {
        this.pythonServiceConfig = pythonServiceConfig;
        this.faceRecognitionConfig = faceRecognitionConfig;
    }

    /**
     * 定期执行全部清理操作
     * 清理间隔从配置文件读取
     */
    @Scheduled(fixedRateString = "${python.port.cleanup-interval}")
    @Override
    public void executeAllCleanup() {
        log.info("开始执行全部人脸数据清理操作,当前时间为"+LocalDateTime.now());
        
        // 1. 清理Python服务的临时文件
        cleanupTempFiles();
        
        // 2. 清理检测文件夹
        cleanupDetectionFiles();
        
        // 3. 清理过期的人脸数据
        cleanupExpiredFaceData();
        
        log.info("全部人脸数据清理操作执行完成,当前时间为"+LocalDateTime.now());
    }

    /**
     * 清理Python服务的临时文件
     */
    @Override
    public void cleanupTempFiles() {
        try {
            log.info("开始清理Python服务临时文件，目录：{}", pythonServiceConfig.getTempPath());
            
            Path tempDir = Paths.get(pythonServiceConfig.getTempPath());
            if (Files.exists(tempDir)) {
                // 遍历临时文件夹，删除所有文件
                Files.walk(tempDir)
                        .filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                log.info("删除临时文件成功：{}", path.toString());
                            } catch (IOException e) {
                                log.error("删除临时文件失败：{}", path.toString(), e);
                            }
                        });
                log.info("Python服务临时文件清理完成");
            } else {
                log.info("Python服务临时文件目录不存在，跳过清理");
            }
        } catch (IOException e) {
            log.error("清理Python服务临时文件失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 清理检测文件夹
     */
    @Override
    public void cleanupDetectionFiles() {
        try {
            log.info("开始清理检测文件夹，目录：{}", faceRecognitionConfig.getPhotoPath());
            
            Path detectionDir = Paths.get(faceRecognitionConfig.getPhotoPath());
            if (Files.exists(detectionDir)) {
                // 遍历检测文件夹，删除所有超过1小时的文件
                LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
                
                Files.walk(detectionDir)
                        .filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                // 获取文件最后修改时间
                                LocalDateTime lastModified = LocalDateTime.ofInstant(
                                        Files.getLastModifiedTime(path).toInstant(),
                                        java.time.ZoneId.systemDefault()
                                );
                                
                                // 如果文件超过1小时，删除
                                if (lastModified.isBefore(oneHourAgo)) {
                                    Files.delete(path);
                                    log.info("删除过期检测文件成功：{}", path.toString());
                                }
                            } catch (IOException e) {
                                log.error("删除检测文件失败：{}", path.toString(), e);
                            }
                        });
                log.info("检测文件夹清理完成");
            } else {
                log.info("检测文件夹不存在，跳过清理");
            }
        } catch (IOException e) {
            log.error("清理检测文件夹失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 清理过期的人脸数据
     * 这里暂时只清理超过7天未使用的临时人脸数据
     */
    @Override
    public void cleanupExpiredFaceData() {
        try {
            log.info("开始清理过期人脸数据，人脸数据库目录：{}", pythonServiceConfig.getDbPath());
            
            Path dbDir = Paths.get(pythonServiceConfig.getDbPath());
            if (Files.exists(dbDir)) {
                // 遍历人脸数据库目录，删除超过7天未修改的用户人脸数据
                LocalDateTime sevenDaysAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
                
                Files.walk(dbDir)
                        .filter(Files::isDirectory)
                        .filter(path -> !path.equals(dbDir)) // 跳过根目录
                        .forEach(userDir -> {
                            try {
                                // 获取用户目录最后修改时间
                                LocalDateTime lastModified = LocalDateTime.ofInstant(
                                        Files.getLastModifiedTime(userDir).toInstant(),
                                        java.time.ZoneId.systemDefault()
                                );
                                
                                // 如果用户目录超过7天未修改，删除整个目录
                                if (lastModified.isBefore(sevenDaysAgo)) {
                                    // 先删除目录下的所有文件
                                    Files.walk(userDir)
                                            .filter(Files::isRegularFile)
                                            .forEach(file -> {
                                                try {
                                                    Files.delete(file);
                                                    log.info("删除过期人脸数据文件成功：{}", file.toString());
                                                } catch (IOException e) {
                                                    log.error("删除过期人脸数据文件失败：{}", file.toString(), e);
                                                }
                                            });
                                    // 再删除目录
                                    Files.delete(userDir);
                                    log.info("删除过期用户人脸数据目录成功：{}", userDir.toString());
                                }
                            } catch (IOException e) {
                                log.error("清理过期人脸数据失败：{}", userDir.toString(), e);
                            }
                        });
                log.info("过期人脸数据清理完成");
            } else {
                log.info("人脸数据库目录不存在，跳过清理");
            }
        } catch (IOException e) {
            log.error("清理过期人脸数据失败：{}", e.getMessage(), e);
        }
    }
}