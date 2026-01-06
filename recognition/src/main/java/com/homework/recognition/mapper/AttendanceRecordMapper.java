package com.homework.recognition.mapper;

import com.homework.recognition.domain.entity.AttendanceRecord;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 打卡记录Mapper
 *
 * @author homework
 */
@Mapper
public interface AttendanceRecordMapper {
    @Insert("INSERT INTO attendance_record(user_id, user_name, real_name, punch_time, punch_type, status, recognition_log_id, remark) VALUES(#{userId}, #{userName}, #{realName}, #{punchTime}, #{punchType}, #{status}, #{recognitionLogId}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    int insert(AttendanceRecord record);

    @Select("SELECT * FROM attendance_record WHERE record_id = #{recordId}")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    AttendanceRecord selectById(Long recordId);

    @Select("SELECT * FROM attendance_record WHERE user_id = #{userId}")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    List<AttendanceRecord> selectByUserId(Long userId);

    @Select("SELECT * FROM attendance_record WHERE punch_time BETWEEN #{startTime} AND #{endTime}")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    Page<AttendanceRecord> selectByPunchTimeBetween(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select("SELECT * FROM attendance_record WHERE user_id = #{userId} AND punch_time BETWEEN #{startTime} AND #{endTime}")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    Page<AttendanceRecord> selectByUserIdAndPunchTimeBetween(@Param("userId") Long userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select("SELECT * FROM attendance_record WHERE status = #{status}")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    List<AttendanceRecord> selectByStatus(Integer status);

    @Select("SELECT * FROM attendance_record WHERE punch_type = #{punchType}")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    List<AttendanceRecord> selectByPunchType(Integer punchType);

    @Select("SELECT * FROM attendance_record")
    @Results({
            @Result(property = "recordId", column = "record_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "punchTime", column = "punch_time"),
            @Result(property = "punchType", column = "punch_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "recognitionLogId", column = "recognition_log_id"),
            @Result(property = "remark", column = "remark")
    })
    Page<AttendanceRecord> selectAll();
}