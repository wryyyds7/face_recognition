package com.homework.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // ======================== String 操作 ========================
    
    /**
     * Set key-value with expiration time
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
    
    /**
     * Set key-value without expiration time
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    /**
     * Get value by key
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Increment by delta
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    
    /**
     * Decrement by delta
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
    
    // ======================== Hash 操作 ========================
    
    /**
     * Set hash field-value
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }
    
    /**
     * Set hash field-value with expiration time
     */
    public void hSet(String key, String hashKey, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        redisTemplate.expire(key, timeout, timeUnit);
    }
    
    /**
     * Get hash field value
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }
    
    /**
     * Set multiple hash field-value pairs
     */
    public void hMSet(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    /**
     * Set multiple hash field-value pairs with expiration time
     */
    public void hMSet(String key, Map<String, Object> map, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, timeout, timeUnit);
    }
    
    /**
     * Get all hash field-values
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    /**
     * Delete hash field
     */
    public void hDelete(String key, String... hashKeys) {
        redisTemplate.opsForHash().delete(key, (Object[]) hashKeys);
    }
    
    /**
     * Check if hash field exists
     */
    public boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }
    
    // ======================== List 操作 ========================
    
    /**
     * Push element to list left
     */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }
    
    /**
     * Push element to list left with expiration time
     */
    public Long lPush(String key, Object value, long timeout, TimeUnit timeUnit) {
        Long result = redisTemplate.opsForList().leftPush(key, value);
        redisTemplate.expire(key, timeout, timeUnit);
        return result;
    }
    
    /**
     * Push element to list right
     */
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
    
    /**
     * Push element to list right with expiration time
     */
    public Long rPush(String key, Object value, long timeout, TimeUnit timeUnit) {
        Long result = redisTemplate.opsForList().rightPush(key, value);
        redisTemplate.expire(key, timeout, timeUnit);
        return result;
    }
    
    /**
     * Get list range
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    /**
     * Get list size
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }
    
    /**
     * Remove and get the first element of list
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    /**
     * Remove and get the last element of list
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    // ======================== Key 操作 ========================
    
    /**
     * Check if key exists
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    /**
     * Delete key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    /**
     * Delete multiple keys
     */
    public void deleteBatch(Collection<String> keys) {
        redisTemplate.delete(keys);
    }
    
    /**
     * Set key expiration time
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }
    
    /**
     * Get key remaining expiration time
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }
}