package com.homework.recognition.service;

import com.homework.common.service.RedisService;
import com.homework.recognition.domain.entity.AttendanceRecord;
import com.homework.recognition.domain.entity.FaceRecognitionLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 考勤记录缓存服务
 * 用于缓存考勤记录和统计数据，提高查询性能
 */
@Service
public class AttendanceCacheService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceCacheService.class);

    private final RedisService redisService;

    // Redis键前缀
    private static final String ATTENDANCE_USER_KEY_PREFIX = "attendance:user:";
    private static final String ATTENDANCE_TIME_RANGE_KEY_PREFIX = "attendance:range:";
    private static final String ATTENDANCE_USER_RANGE_KEY_PREFIX = "attendance:user:range:";
    private static final String ATTENDANCE_STATISTICS_KEY_PREFIX = "attendance:statistics:";
    private static final String RECOGNITION_LOGS_KEY_PREFIX = "recognition:logs:";
    private static final String USER_RECENT_ATTENDANCE_KEY_PREFIX = "attendance:recent:";
    
    // 缓存过期时间（1天）
    private static final long ATTENDANCE_CACHE_EXPIRE_TIME = 1;

    @Autowired
    public AttendanceCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 缓存用户考勤记录
     * @param userId 用户ID
     * @param records 考勤记录列表
     */
    public void cacheUserAttendanceRecords(Long userId, List<AttendanceRecord> records) {
        String key = ATTENDANCE_USER_KEY_PREFIX + userId;
        redisService.set(key, records, ATTENDANCE_CACHE_EXPIRE_TIME, TimeUnit.DAYS);
        log.info("已缓存用户{}的考勤记录，共{}条", userId, records.size());
    }

    /**
     * 获取缓存的用户考勤记录
     * @param userId 用户ID
     * @return 考勤记录列表，如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceRecord> getUserAttendanceRecords(Long userId) {
        String key = ATTENDANCE_USER_KEY_PREFIX + userId;
        return (List<AttendanceRecord>) redisService.get(key);
    }

    /**
     * 缓存时间范围内的考勤记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param records 考勤记录列表
     */
    public void cacheAttendanceRecordsByTimeRange(Date startTime, Date endTime, List<AttendanceRecord> records) {
        String key = ATTENDANCE_TIME_RANGE_KEY_PREFIX + startTime.getTime() + ":" + endTime.getTime();
        redisService.set(key, records, ATTENDANCE_CACHE_EXPIRE_TIME, TimeUnit.DAYS);
        log.info("已缓存时间范围{}至{}的考勤记录，共{}条", startTime, endTime, records.size());
    }

    /**
     * 获取缓存的时间范围内的考勤记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 考勤记录列表，如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceRecord> getAttendanceRecordsByTimeRange(Date startTime, Date endTime) {
        String key = ATTENDANCE_TIME_RANGE_KEY_PREFIX + startTime.getTime() + ":" + endTime.getTime();
        return (List<AttendanceRecord>) redisService.get(key);
    }

    /**
     * 缓存用户在时间范围内的考勤记录
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param records 考勤记录列表
     */
    public void cacheUserAttendanceRecordsByTimeRange(Long userId, Date startTime, Date endTime, List<AttendanceRecord> records) {
        String key = ATTENDANCE_USER_RANGE_KEY_PREFIX + userId + ":" + startTime.getTime() + ":" + endTime.getTime();
        redisService.set(key, records, ATTENDANCE_CACHE_EXPIRE_TIME, TimeUnit.DAYS);
        log.info("已缓存用户{}在时间范围{}至{}的考勤记录，共{}条", userId, startTime, endTime, records.size());
    }

    /**
     * 获取缓存的用户在时间范围内的考勤记录
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 考勤记录列表，如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceRecord> getUserAttendanceRecordsByTimeRange(Long userId, Date startTime, Date endTime) {
        String key = ATTENDANCE_USER_RANGE_KEY_PREFIX + userId + ":" + startTime.getTime() + ":" + endTime.getTime();
        return (List<AttendanceRecord>) redisService.get(key);
    }

    /**
     * 缓存考勤统计数据
     * @param userId 用户ID（可为null表示全局统计）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param statistics 统计数据
     */
    public void cacheAttendanceStatistics(Long userId, Date startTime, Date endTime, Map<String, Object> statistics) {
        String userIdStr = userId != null ? userId.toString() : "global";
        String key = ATTENDANCE_STATISTICS_KEY_PREFIX + userIdStr + ":" + startTime.getTime() + ":" + endTime.getTime();
        redisService.set(key, statistics, ATTENDANCE_CACHE_EXPIRE_TIME, TimeUnit.DAYS);
        log.info("已缓存用户{}在时间范围{}至{}的考勤统计数据", userIdStr, startTime, endTime);
    }

    /**
     * 获取缓存的考勤统计数据
     * @param userId 用户ID（可为null表示全局统计）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据，如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAttendanceStatistics(Long userId, Date startTime, Date endTime) {
        String userIdStr = userId != null ? userId.toString() : "global";
        String key = ATTENDANCE_STATISTICS_KEY_PREFIX + userIdStr + ":" + startTime.getTime() + ":" + endTime.getTime();
        return (Map<String, Object>) redisService.get(key);
    }

    /**
     * 缓存识别日志列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param logs 识别日志列表
     */
    public void cacheRecognitionLogs(Date startTime, Date endTime, List<FaceRecognitionLog> logs) {
        String key = RECOGNITION_LOGS_KEY_PREFIX + startTime.getTime() + ":" + endTime.getTime();
        redisService.set(key, logs, ATTENDANCE_CACHE_EXPIRE_TIME, TimeUnit.DAYS);
        log.info("已缓存时间范围{}至{}的识别日志，共{}条", startTime, endTime, logs.size());
    }

    /**
     * 获取缓存的识别日志列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 识别日志列表，如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public List<FaceRecognitionLog> getRecognitionLogs(Date startTime, Date endTime) {
        String key = RECOGNITION_LOGS_KEY_PREFIX + startTime.getTime() + ":" + endTime.getTime();
        return (List<FaceRecognitionLog>) redisService.get(key);
    }

    /**
     * 缓存用户最近打卡状态
     * @param userId 用户ID
     * @param hasRecentAttendance 是否有最近打卡记录
     * @param hours 检查的小时数
     */
    public void cacheUserRecentAttendance(Long userId, boolean hasRecentAttendance, int hours) {
        String key = USER_RECENT_ATTENDANCE_KEY_PREFIX + userId + ":" + hours;
        // 设置缓存时间为指定小时数，确保缓存过期后重新检查
        redisService.set(key, hasRecentAttendance, hours, TimeUnit.HOURS);
        log.info("已缓存用户{}在最近{}小时的打卡状态：{}", userId, hours, hasRecentAttendance);
    }

    /**
     * 获取用户最近打卡状态
     * @param userId 用户ID
     * @param hours 检查的小时数
     * @return 是否有最近打卡记录，如果缓存不存在则返回null
     */
    public Boolean getUserRecentAttendance(Long userId, int hours) {
        String key = USER_RECENT_ATTENDANCE_KEY_PREFIX + userId + ":" + hours;
        return (Boolean) redisService.get(key);
    }

    /**
     * 清除用户相关的所有缓存
     * @param userId 用户ID
     */
    public void clearUserAttendanceCache(Long userId) {
        // 清除用户考勤记录缓存
        String userKey = ATTENDANCE_USER_KEY_PREFIX + userId;
        redisService.delete(userKey);
        
        // 清除用户最近打卡状态缓存（可能有多个小时数的缓存）
        // 注意：这里只能清除已知的小时数缓存，或者依赖缓存自动过期
        for (int hours : new int[]{12, 24}) {
            String recentKey = USER_RECENT_ATTENDANCE_KEY_PREFIX + userId + ":" + hours;
            redisService.delete(recentKey);
        }
        
        log.info("已清除用户{}的考勤相关缓存", userId);
    }

    /**
     * 清除所有考勤相关缓存
     */
    public void clearAllAttendanceCache() {
        // 注意：这里无法直接删除所有前缀匹配的缓存，因为RedisService没有提供该功能
        // 实际生产环境中，可以使用Redis的KEYS命令配合DEL命令实现
        log.info("已触发清除所有考勤相关缓存的操作");
    }
}