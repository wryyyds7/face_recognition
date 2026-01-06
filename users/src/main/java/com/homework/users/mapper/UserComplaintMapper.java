package com.homework.users.mapper;

import com.homework.users.domain.entity.UserComplaint;
import com.homework.users.domain.dto.ComplaintDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserComplaintMapper {
    /**
     * 创建投诉
     */
    @Insert("INSERT INTO complaint (user_id, complained_type, complained_id, complaint_title, complaint_content, complaint_status, create_time, update_time) " +
            "VALUES (#{userId}, #{complainedType}, #{complainedId}, #{complaintTitle}, #{complaintContent}, #{complaintStatus}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "complaintId")
    Long insertComplaint(UserComplaint complaint);

    /**
     * 获取投诉列表
     */
    @Select("SELECT * FROM complaint")
    @Results({
            @Result(property = "complaintId", column = "complaint_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "complainedType", column = "complained_type"),
            @Result(property = "complainedId", column = "complained_id"),
            @Result(property = "complaintTitle", column = "complaint_title"),
            @Result(property = "complaintContent", column = "complaint_content"),
            @Result(property = "complaintStatus", column = "complaint_status"),
            @Result(property = "handleResult", column = "handle_result"),
            @Result(property = "handlerId", column = "handler_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    Page<UserComplaint> selectComplaintList(ComplaintDTO complaintDTO);

    /**
     * 根据ID获取投诉详情
     */
    @Select("SELECT * FROM complaint WHERE complaint_id = #{complaintId}")
    @Results({
            @Result(property = "complaintId", column = "complaint_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "complainedType", column = "complained_type"),
            @Result(property = "complainedId", column = "complained_id"),
            @Result(property = "complaintTitle", column = "complaint_title"),
            @Result(property = "complaintContent", column = "complaint_content"),
            @Result(property = "complaintStatus", column = "complaint_status"),
            @Result(property = "handleResult", column = "handle_result"),
            @Result(property = "handlerId", column = "handler_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    UserComplaint selectComplaintById(Long complaintId);

    /**
     * 更新投诉状态
     */
    @Update("UPDATE complaint SET complaint_status = #{complaintStatus}, update_time = NOW() WHERE complaint_id = #{complaintId}")
    Long updateComplaintStatus(@Param("complaintId") Long complaintId, @Param("complaintStatus") String complaintStatus);

    /**
     * 处理投诉
     */
    @Update("UPDATE complaint SET complaint_status = #{complaintStatus}, handle_result = #{handleResult}, handler_id = #{handlerId}, update_time = NOW() WHERE complaint_id = #{complaintId}")
    Long handleComplaint(@Param("complaintId") Long complaintId, 
                         @Param("complaintStatus") String complaintStatus, 
                         @Param("handleResult") String handleResult, 
                         @Param("handlerId") Long handlerId);

    /**
     * 根据用户ID获取投诉列表
     */
    @Select("SELECT * FROM complaint WHERE user_id = #{userId}")
    @Results({
            @Result(property = "complaintId", column = "complaint_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "complainedType", column = "complained_type"),
            @Result(property = "complainedId", column = "complained_id"),
            @Result(property = "complaintTitle", column = "complaint_title"),
            @Result(property = "complaintContent", column = "complaint_content"),
            @Result(property = "complaintStatus", column = "complaint_status"),
            @Result(property = "handleResult", column = "handle_result"),
            @Result(property = "handlerId", column = "handler_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<UserComplaint> selectComplaintsByUserId(Long userId);
}
