package com.homework.recognition.service.impl;

import com.homework.recognition.domain.entity.AttendanceRecord;
import com.homework.recognition.domain.entity.FaceRecognitionLog;
import com.homework.recognition.mapper.AttendanceRecordMapper;
import com.homework.recognition.mapper.FaceRecognitionLogMapper;
import com.homework.recognition.service.AttendanceCacheService;
import com.homework.recognition.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 打卡服务实现类
 *
 * @author homework
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {
    
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    
    private final FaceRecognitionLogMapper faceRecognitionLogMapper;
    private final AttendanceRecordMapper attendanceRecordMapper;
    private final AttendanceCacheService attendanceCacheService;
    
    @Autowired
    public AttendanceServiceImpl(FaceRecognitionLogMapper faceRecognitionLogMapper, 
                               AttendanceRecordMapper attendanceRecordMapper,
                               AttendanceCacheService attendanceCacheService) {
        this.faceRecognitionLogMapper = faceRecognitionLogMapper;
        this.attendanceRecordMapper = attendanceRecordMapper;
        this.attendanceCacheService = attendanceCacheService;
    }
    
    @Override
    public FaceRecognitionLog saveRecognitionLog(FaceRecognitionLog log1) {
        try {
            log1.setRecognitionTime(new Date());
            faceRecognitionLogMapper.insert(log1);
            return log1;
        } catch (Exception e) {
            log.error("保存人脸识别人物识别日志失败：{}", e.getMessage(), e);
            throw new RuntimeException("保存人脸识别人物识别日志失败", e);
        }
    }
    
    @Override
    public AttendanceRecord generateAttendanceRecord(Long logId, Long userId, String userName, String realName, 
                                                   Integer punchType, Integer status, String remark) {
        try {
            AttendanceRecord record = new AttendanceRecord();
            record.setRecognitionLogId(logId);
            record.setUserId(userId);
            record.setUserName(userName);
            record.setRealName(realName);
            record.setPunchTime(new Date());
            record.setPunchType(punchType);
            record.setStatus(status);
            record.setRemark(remark);
            
            attendanceRecordMapper.insert(record);
            
            // 清除相关缓存，确保下次查询获取最新数据
            attendanceCacheService.clearUserAttendanceCache(userId);
            
            return record;
        } catch (Exception e) {
            log.error("生成打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("生成打卡记录失败", e);
        }
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceRecordsByUserId(Long userId) {
        try {
            // 先检查缓存
            List<AttendanceRecord> cachedRecords = attendanceCacheService.getUserAttendanceRecords(userId);
            if (cachedRecords != null) {
                log.info("从缓存中获取用户{}的考勤记录，共{}条", userId, cachedRecords.size());
                return cachedRecords;
            }
            
            // 缓存不存在，查询数据库
            List<AttendanceRecord> records = attendanceRecordMapper.selectByUserId(userId);
            
            // 将结果缓存
            attendanceCacheService.cacheUserAttendanceRecords(userId, records);
            
            return records;
        } catch (Exception e) {
            log.error("根据用户ID查询打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("根据用户ID查询打卡记录失败", e);
        }
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceRecordsByTimeRange(Date startTime, Date endTime) {
        try {
            // 先检查缓存
            List<AttendanceRecord> cachedRecords = attendanceCacheService.getAttendanceRecordsByTimeRange(startTime, endTime);
            if (cachedRecords != null) {
                log.info("从缓存中获取时间范围{}至{}的考勤记录，共{}条", startTime, endTime, cachedRecords.size());
                return cachedRecords;
            }
            
            // 缓存不存在，查询数据库
            List<AttendanceRecord> records = attendanceRecordMapper.selectByPunchTimeBetween(startTime, endTime);
            
            // 将结果缓存
            attendanceCacheService.cacheAttendanceRecordsByTimeRange(startTime, endTime, records);
            
            return records;
        } catch (Exception e) {
            log.error("根据时间范围查询打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("根据时间范围查询打卡记录失败", e);
        }
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceRecordsByUserIdAndTimeRange(Long userId, Date startTime, Date endTime) {
        try {
            // 先检查缓存
            List<AttendanceRecord> cachedRecords = attendanceCacheService.getUserAttendanceRecordsByTimeRange(userId, startTime, endTime);
            if (cachedRecords != null) {
                log.info("从缓存中获取用户{}在时间范围{}至{}的考勤记录，共{}条", userId, startTime, endTime, cachedRecords.size());
                return cachedRecords;
            }
            
            // 缓存不存在，查询数据库
            List<AttendanceRecord> records = attendanceRecordMapper.selectByUserIdAndPunchTimeBetween(userId, startTime, endTime);
            
            // 将结果缓存
            attendanceCacheService.cacheUserAttendanceRecordsByTimeRange(userId, startTime, endTime, records);
            
            return records;
        } catch (Exception e) {
            log.error("根据用户ID和时间范围查询打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("根据用户ID和时间范围查询打卡记录失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getAttendanceStatistics(Long userId, Date startTime, Date endTime) {
        try {
            // 先检查缓存
            Map<String, Object> cachedStatistics = attendanceCacheService.getAttendanceStatistics(userId, startTime, endTime);
            if (cachedStatistics != null) {
                String userIdStr = userId != null ? userId.toString() : "global";
                log.info("从缓存中获取用户{}在时间范围{}至{}的考勤统计数据", userIdStr, startTime, endTime);
                return cachedStatistics;
            }
            
            // 缓存不存在，查询数据库
            List<AttendanceRecord> records;
            if (userId != null) {
                records = attendanceRecordMapper.selectByUserIdAndPunchTimeBetween(userId, startTime, endTime);
            } else {
                records = attendanceRecordMapper.selectByPunchTimeBetween(startTime, endTime);
            }
            
            Map<String, Object> statistics = new HashMap<>();
            
            // 总打卡次数
            statistics.put("totalRecords", records.size());
            
            // 按状态统计
            Map<Integer, Long> statusCount = records.stream()
                    .collect(Collectors.groupingBy(AttendanceRecord::getStatus, Collectors.counting()));
            statistics.put("statusCount", statusCount);
            
            // 按类型统计
            Map<Integer, Long> typeCount = records.stream()
                    .collect(Collectors.groupingBy(AttendanceRecord::getPunchType, Collectors.counting()));
            statistics.put("typeCount", typeCount);
            
            // 成功打卡次数
            long successCount = statusCount.getOrDefault(1, 0L);
            statistics.put("successCount", successCount);
            
            // 失败打卡次数
            long failureCount = statusCount.getOrDefault(0, 0L);
            statistics.put("failureCount", failureCount);
            
            // 成功率
            double successRate = records.size() > 0 ? (double) successCount / records.size() : 0;
            statistics.put("successRate", successRate);
            
            // 将结果缓存
            attendanceCacheService.cacheAttendanceStatistics(userId, startTime, endTime, statistics);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取打卡统计数据失败：{}", e.getMessage(), e);
            throw new RuntimeException("获取打卡统计数据失败", e);
        }
    }
    
    @Override
    public List<FaceRecognitionLog> getRecognitionLogs(Date startTime, Date endTime) {
        try {
            // 先检查缓存
            List<FaceRecognitionLog> cachedLogs = attendanceCacheService.getRecognitionLogs(startTime, endTime);
            if (cachedLogs != null) {
                log.info("从缓存中获取时间范围{}至{}的识别日志，共{}条", startTime, endTime, cachedLogs.size());
                return cachedLogs;
            }
            
            // 缓存不存在，查询数据库
            List<FaceRecognitionLog> logs = faceRecognitionLogMapper.selectByRecognitionTimeBetween(startTime, endTime);
            
            // 将结果缓存
            attendanceCacheService.cacheRecognitionLogs(startTime, endTime, logs);
            
            return logs;
        } catch (Exception e) {
            log.error("获取识别日志列表失败：{}", e.getMessage(), e);
            throw new RuntimeException("获取识别日志列表失败", e);
        }
    }
    
    @Override
    public FaceRecognitionLog getRecognitionLogById(Long logId) {
        try {
            FaceRecognitionLog log = faceRecognitionLogMapper.selectById(logId);
            if (log == null) {
                throw new RuntimeException("识别日志不存在");
            }
            return log;
        } catch (Exception e) {
            log.error("根据日志ID获取识别日志失败：{}", e.getMessage(), e);
            throw new RuntimeException("根据日志ID获取识别日志失败", e);
        }
    }
    
    @Override
    public boolean hasRecentAttendance(Long userId) {
        // 默认使用12小时
        return hasRecentAttendance(userId, 12);
    }
    
    @Override
    public boolean hasRecentAttendance(Long userId, int hours) {
        try {
            // 先检查缓存
            Boolean cachedResult = attendanceCacheService.getUserRecentAttendance(userId, hours);
            if (cachedResult != null) {
                log.info("从缓存中获取用户{}在最近{}小时的打卡状态：{}", userId, hours, cachedResult);
                return cachedResult;
            }
            
            // 缓存不存在，查询数据库
            // 查询用户最近的打卡记录
            List<AttendanceRecord> records = attendanceRecordMapper.selectByUserId(userId);
            if (records == null || records.isEmpty()) {
                // 将结果缓存
                attendanceCacheService.cacheUserRecentAttendance(userId, false, hours);
                return false;
            }
            
            // 找到最近的一条打卡记录
            AttendanceRecord recentRecord = records.stream()
                    .max(Comparator.comparing(AttendanceRecord::getPunchTime))
                    .orElse(null);
            
            boolean result;
            if (recentRecord == null) {
                result = false;
            } else {
                // 计算时间差（毫秒）
                long timeDiff = System.currentTimeMillis() - recentRecord.getPunchTime().getTime();
                
                // 转换为小时
                long hoursDiff = timeDiff / (1000 * 60 * 60);
                
                // 如果时间差小于指定小时数，返回true
                result = hoursDiff < hours;
            }
            
            // 将结果缓存
            attendanceCacheService.cacheUserRecentAttendance(userId, result, hours);
            
            return result;
        } catch (Exception e) {
            log.error("检查用户最近打卡记录失败：{}", e.getMessage(), e);
            // 异常情况下默认返回false，允许用户打卡
            return false;
        }
    }
}