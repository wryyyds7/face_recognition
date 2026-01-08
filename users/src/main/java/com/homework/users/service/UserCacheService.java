package com.homework.users.service;

import com.homework.common.service.RedisService;
import com.homework.common.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 用户信息缓存服务
 * 用于缓存热点用户信息，提高查询性能
 */
@Service
public class UserCacheService {

    private static final Logger log = LoggerFactory.getLogger(UserCacheService.class);

    private final RedisService redisService;
    
    // Redis键前缀
    private static final String USER_INFO_KEY_PREFIX = "user:info:";
    private static final String USER_NAME_KEY_PREFIX = "user:name:";
    
    // 缓存过期时间（30分钟）
    private static final long USER_CACHE_EXPIRE_TIME = 30;

    @Autowired
    public UserCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 缓存用户信息
     * @param user 用户对象
     */
    public void cacheUserInfo(User user) {
        if (user == null) {
            return;
        }
        
        // 按用户ID缓存
        String userIdKey = USER_INFO_KEY_PREFIX + user.getUserId();
        redisService.set(userIdKey, user, USER_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        // 按用户名缓存
        if (user.getUserName() != null) {
            String userNameKey = USER_NAME_KEY_PREFIX + user.getUserName();
            redisService.set(userNameKey, user, USER_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        
        log.info("已缓存用户信息：{}，用户名：{}", user.getUserId(), user.getUserName());
    }

    /**
     * 根据用户ID获取缓存的用户信息
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserInfoById(Long userId) {
        String key = USER_INFO_KEY_PREFIX + userId;
        return (User) redisService.get(key);
    }

    /**
     * 根据用户名获取缓存的用户信息
     * @param userName 用户名
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserInfoByUserName(String userName) {
        String key = USER_NAME_KEY_PREFIX + userName;
        return (User) redisService.get(key);
    }

    /**
     * 删除用户信息缓存
     * @param user 用户对象
     */
    public void deleteUserCache(User user) {
        if (user == null) {
            return;
        }
        
        // 删除用户ID缓存
        String userIdKey = USER_INFO_KEY_PREFIX + user.getUserId();
        redisService.delete(userIdKey);
        
        // 删除用户名缓存
        if (user.getUserName() != null) {
            String userNameKey = USER_NAME_KEY_PREFIX + user.getUserName();
            redisService.delete(userNameKey);
        }
        
        log.info("已删除用户缓存：{}，用户名：{}", user.getUserId(), user.getUserName());
    }

    /**
     * 根据用户ID删除用户信息缓存
     * @param userId 用户ID
     */
    public void deleteUserCacheById(Long userId) {
        String key = USER_INFO_KEY_PREFIX + userId;
        redisService.delete(key);
        log.info("已删除用户ID {} 的缓存", userId);
    }

    /**
     * 根据用户名删除用户信息缓存
     * @param userName 用户名
     */
    public void deleteUserCacheByUserName(String userName) {
        String key = USER_NAME_KEY_PREFIX + userName;
        redisService.delete(key);
        log.info("已删除用户名 {} 的缓存", userName);
    }

    /**
     * 刷新用户信息缓存
     * @param user 用户对象
     */
    public void refreshUserCache(User user) {
        // 先删除旧缓存
        deleteUserCache(user);
        // 再添加新缓存
        cacheUserInfo(user);
    }
}