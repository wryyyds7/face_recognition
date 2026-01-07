package com.homework.recognition.service;

import com.homework.recognition.domain.entity.AttendanceRecord;
import com.homework.recognition.domain.entity.FaceRecognitionLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 打卡服务接口
 *
 * @author homework
 */
public interface AttendanceService {
    
    /**
     * 保存人脸识别人物识别日志
     *
     * @param log 识别日志
     * @return 保存后的日志
     */
    FaceRecognitionLog saveRecognitionLog(FaceRecognitionLog log);
    
    /**
     * 生成打卡记录
     *
     * @param logId 识别日志ID
     * @param userId 用户ID
     * @param userName 用户名
     * @param realName 真实姓名
     * @param punchType 打卡类型（1：上班，2：下班）
     * @param status 打卡状态（1：成功，0：失败）
     * @param remark 备注
     * @return 生成的打卡记录
     */
    AttendanceRecord generateAttendanceRecord(Long logId, Long userId, String userName, String realName, 
                                            Integer punchType, Integer status, String remark);
    
    /**
     * 根据用户ID查询打卡记录
     *
     * @param userId 用户ID
     * @return 打卡记录列表
     */
    List<AttendanceRecord> getAttendanceRecordsByUserId(Long userId);
    
    /**
     * 根据时间范围查询打卡记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 打卡记录列表
     */
    List<AttendanceRecord> getAttendanceRecordsByTimeRange(Date startTime, Date endTime);
    
    /**
     * 根据用户ID和时间范围查询打卡记录
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 打卡记录列表
     */
    List<AttendanceRecord> getAttendanceRecordsByUserIdAndTimeRange(Long userId, Date startTime, Date endTime);
    
    /**
     * 获取打卡统计数据
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据
     */
    Map<String, Object> getAttendanceStatistics(Long userId, Date startTime, Date endTime);
    
    /**
     * 获取识别日志列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 识别日志列表
     */
    List<FaceRecognitionLog> getRecognitionLogs(Date startTime, Date endTime);
    
    /**
     * 根据日志ID获取识别日志
     *
     * @param logId 日志ID
     * @return 识别日志
     */
    FaceRecognitionLog getRecognitionLogById(Long logId);
    
    /**
     * 检查用户是否在默认时间内（12小时）已打卡
     *
     * @param userId 用户ID
     * @return 是否已打卡
     */
    boolean hasRecentAttendance(Long userId);
    
    /**
     * 检查用户是否在指定时间内已打卡
     *
     * @param userId 用户ID
     * @param hours 指定时间（小时）
     * @return 是否已打卡
     */
    boolean hasRecentAttendance(Long userId, int hours);
}