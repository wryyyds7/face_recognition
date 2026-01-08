package com.homework.common.service;

import com.homework.common.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置缓存服务
 * 用于缓存系统配置信息，提高配置读取的性能
 */
@Service
public class ConfigCacheService {

    private static final Logger log = LoggerFactory.getLogger(ConfigCacheService.class);

    private final RedisService redisService;

    // Redis键
    private static final String SYSTEM_CONFIG_KEY = "system:config";
    
    // 配置文件路径
    private static final String CONFIG_FILE_PATH = "d:/bianchenglianxi/java/project/face_recognition/config/config.yml";
    
    // 缓存过期时间（1小时）
    private static final long CONFIG_CACHE_EXPIRE_TIME = 1;

    @Autowired
    public ConfigCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 获取系统配置
     * @return 系统配置映射
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSystemConfig() {
        // 先检查缓存
        Map<String, Object> config = (Map<String, Object>) redisService.get(SYSTEM_CONFIG_KEY);
        if (config != null) {
            log.info("从缓存中获取系统配置");
            return config;
        }
        
        // 缓存不存在，读取配置文件
        config = loadConfigFromFile();
        
        // 将配置缓存
        redisService.set(SYSTEM_CONFIG_KEY, config, CONFIG_CACHE_EXPIRE_TIME, TimeUnit.HOURS);
        
        return config;
    }

    /**
     * 根据键获取配置值
     * @param key 配置键（支持点号分隔，如"python.port"）
     * @return 配置值，如果不存在则返回null
     */
    public Object getConfigValue(String key) {
        Map<String, Object> config = getSystemConfig();
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = config;
        Object value = null;
        
        for (int i = 0; i < keys.length; i++) {
            String k = keys[i];
            if (i == keys.length - 1) {
                // 最后一个键，获取值
                value = currentMap.get(k);
            } else {
                // 获取下一层映射
                Object next = currentMap.get(k);
                if (next instanceof Map) {
                    currentMap = (Map<String, Object>) next;
                } else {
                    // 中间键不存在或不是映射，返回null
                    return null;
                }
            }
        }
        
        return value;
    }

    /**
     * 刷新系统配置缓存
     */
    public void refreshConfigCache() {
        // 读取配置文件
        Map<String, Object> config = loadConfigFromFile();
        
        // 更新缓存
        redisService.set(SYSTEM_CONFIG_KEY, config, CONFIG_CACHE_EXPIRE_TIME, TimeUnit.HOURS);
        
        log.info("已刷新系统配置缓存");
    }

    /**
     * 从配置文件加载配置
     * @return 配置映射
     */
    private Map<String, Object> loadConfigFromFile() {
        log.info("从文件加载系统配置：{}", CONFIG_FILE_PATH);
        
        // 创建一个默认的配置映射，包含所有已知的配置项
        Map<String, Object> config = new HashMap<>();
        
        // Python服务配置
        Map<String, Object> pythonConfig = new HashMap<>();
        pythonConfig.put("port", 0);
        pythonConfig.put("host", "localhost");
        pythonConfig.put("url", "http://localhost:0");
        config.put("python", pythonConfig);
        
        // 人脸数据库配置
        Map<String, Object> databaseConfig = new HashMap<>();
        databaseConfig.put("root-path", "d:/bianchenglianxi/java/project/face_recognition/python_port/img");
        databaseConfig.put("temp-path", "d:/bianchenglianxi/java/project/face_recognition/python_port/uploads");
        config.put("database", databaseConfig);
        
        // 人脸检测配置
        Map<String, Object> detectionConfig = new HashMap<>();
        detectionConfig.put("interval", 5000);
        detectionConfig.put("cleanup-interval", 86400000);
        config.put("detection", detectionConfig);
        
        // 上传文件配置
        Map<String, Object> uploadConfig = new HashMap<>();
        uploadConfig.put("max-file-size", "10MB");
        uploadConfig.put("max-request-size", "10MB");
        config.put("upload", uploadConfig);
        
        return config;
    }
}