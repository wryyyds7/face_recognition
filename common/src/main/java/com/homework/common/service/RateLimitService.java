package com.homework.common.service;

import com.homework.common.config.RateLimitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 限流服务类
 * 用于实现接口限流逻辑
 */
@Service
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    private final RedisService redisService;
    private final RateLimitConfig rateLimitConfig;
    
    // 限流规则映射，key为请求路径，value为每分钟允许的请求数
    private final Map<String, Integer> rateLimitRules = new HashMap<>();

    @Autowired
    public RateLimitService(RedisService redisService, RateLimitConfig rateLimitConfig) {
        this.redisService = redisService;
        this.rateLimitConfig = rateLimitConfig;
        
        // 初始化限流规则
        initRateLimitRules();
    }

    /**
     * 初始化限流规则
     */
    private void initRateLimitRules() {
        // 添加默认规则
        rateLimitRules.put("default", rateLimitConfig.getDefaultLimitPerMinute());
        
        // 解析自定义规则
        if (rateLimitConfig.getCustomRules() != null && !rateLimitConfig.getCustomRules().isEmpty()) {
            String[] rules = rateLimitConfig.getCustomRules().split(",");
            for (String rule : rules) {
                String[] parts = rule.split("=");
                if (parts.length == 2) {
                    String path = parts[0].trim();
                    int limit = Integer.parseInt(parts[1].trim());
                    rateLimitRules.put(path, limit);
                    log.info("添加自定义限流规则：{}，每分钟{}次请求", path, limit);
                }
            }
        }
    }

    /**
     * 检查请求是否允许通过
     * @param ip 请求IP
     * @param path 请求路径
     * @return true表示允许通过，false表示限流
     */
    public boolean isAllowed(String ip, String path) {
        // 如果限流未开启，直接允许通过
        if (!rateLimitConfig.isEnabled()) {
            return true;
        }
        
        // 获取限流规则
        int limit = getRateLimitForPath(path);
        
        // 构建Redis键：rate_limit:ip:path:yyyyMMddHHmm
        String key = buildRateLimitKey(ip, path);
        
        // 增加请求计数
        long count = redisService.increment(key, 1);
        
        // 如果是第一次请求，设置过期时间为1分钟
        if (count == 1) {
            redisService.expire(key, 1, TimeUnit.MINUTES);
        }
        
        // 检查是否超过限制
        boolean allowed = count <= limit;
        if (!allowed) {
            log.warn("IP {} 请求路径 {} 超过限流限制，当前请求数：{}，限制：{}", ip, path, count, limit);
        }
        
        return allowed;
    }

    /**
     * 构建限流Redis键
     * @param ip 请求IP
     * @param path 请求路径
     * @return Redis键
     */
    private String buildRateLimitKey(String ip, String path) {
        // 格式化当前时间为yyyyMMddHHmm
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return "rate_limit:" + ip + ":" + path + ":" + timestamp;
    }

    /**
     * 获取指定路径的限流规则
     * @param path 请求路径
     * @return 每分钟允许的请求数
     */
    private int getRateLimitForPath(String path) {
        // 精确匹配路径
        if (rateLimitRules.containsKey(path)) {
            return rateLimitRules.get(path);
        }
        
        // 前缀匹配路径
        for (Map.Entry<String, Integer> entry : rateLimitRules.entrySet()) {
            String rulePath = entry.getKey();
            if (rulePath.endsWith("/*") && path.startsWith(rulePath.substring(0, rulePath.length() - 1))) {
                return entry.getValue();
            }
        }
        
        // 使用默认规则
        return rateLimitRules.get("default");
    }

    /**
     * 刷新限流规则
     */
    public void refreshRateLimitRules() {
        rateLimitRules.clear();
        initRateLimitRules();
        log.info("已刷新限流规则");
    }
}