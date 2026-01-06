package com.homework.recognition.service.impl;

import com.homework.recognition.domain.entity.AttendanceRecord;
import com.homework.recognition.domain.entity.FaceRecognitionLog;
import com.homework.recognition.mapper.AttendanceRecordMapper;
import com.homework.recognition.mapper.FaceRecognitionLogMapper;
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
    
    @Autowired
    public AttendanceServiceImpl(FaceRecognitionLogMapper faceRecognitionLogMapper, 
                               AttendanceRecordMapper attendanceRecordMapper) {
        this.faceRecognitionLogMapper = faceRecognitionLogMapper;
        this.attendanceRecordMapper = attendanceRecordMapper;
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
            return record;
        } catch (Exception e) {
            log.error("生成打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("生成打卡记录失败", e);
        }
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceRecordsByUserId(Long userId) {
        try {
            return attendanceRecordMapper.selectByUserId(userId);
        } catch (Exception e) {
            log.error("根据用户ID查询打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("根据用户ID查询打卡记录失败", e);
        }
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceRecordsByTimeRange(Date startTime, Date endTime) {
        try {
            return attendanceRecordMapper.selectByPunchTimeBetween(startTime, endTime);
        } catch (Exception e) {
            log.error("根据时间范围查询打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("根据时间范围查询打卡记录失败", e);
        }
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceRecordsByUserIdAndTimeRange(Long userId, Date startTime, Date endTime) {
        try {
            return attendanceRecordMapper.selectByUserIdAndPunchTimeBetween(userId, startTime, endTime);
        } catch (Exception e) {
            log.error("根据用户ID和时间范围查询打卡记录失败：{}", e.getMessage(), e);
            throw new RuntimeException("根据用户ID和时间范围查询打卡记录失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getAttendanceStatistics(Long userId, Date startTime, Date endTime) {
        try {
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
            
            return statistics;
        } catch (Exception e) {
            log.error("获取打卡统计数据失败：{}", e.getMessage(), e);
            throw new RuntimeException("获取打卡统计数据失败", e);
        }
    }
    
    @Override
    public List<FaceRecognitionLog> getRecognitionLogs(Date startTime, Date endTime) {
        try {
            return faceRecognitionLogMapper.selectByRecognitionTimeBetween(startTime, endTime);
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
}