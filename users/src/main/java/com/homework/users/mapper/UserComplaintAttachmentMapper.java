package com.homework.users.mapper;

import com.homework.users.domain.entity.UserComplaintAttachment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserComplaintAttachmentMapper {
    /**
     * 上传投诉附件
     */
    @Insert("INSERT INTO complaint_attachment (complaint_id, file_name, file_path, file_size, file_type, create_time) " +
            "VALUES (#{complaintId}, #{fileName}, #{filePath}, #{fileSize}, #{fileType}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "attachmentId")
    Long insertComplaintAttachment(UserComplaintAttachment attachment);

    /**
     * 根据投诉ID获取附件列表
     */
    @Select("SELECT * FROM complaint_attachment WHERE complaint_id = #{complaintId}")
    @Results({
            @Result(property = "attachmentId", column = "attachment_id"),
            @Result(property = "complaintId", column = "complaint_id"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "fileType", column = "file_type"),
            @Result(property = "createTime", column = "create_time")
    })
    List<UserComplaintAttachment> selectAttachmentsByComplaintId(Long complaintId);

    /**
     * 根据附件ID获取附件详情
     */
    @Select("SELECT * FROM complaint_attachment WHERE attachment_id = #{attachmentId}")
    @Results({
            @Result(property = "attachmentId", column = "attachment_id"),
            @Result(property = "complaintId", column = "complaint_id"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "fileType", column = "file_type"),
            @Result(property = "createTime", column = "create_time")
    })
    UserComplaintAttachment selectAttachmentById(Long attachmentId);

    /**
     * 删除投诉附件
     */
    @Delete("DELETE FROM complaint_attachment WHERE attachment_id = #{attachmentId}")
    Long deleteAttachmentById(Long attachmentId);

    /**
     * 根据投诉ID删除所有附件
     */
    @Delete("DELETE FROM complaint_attachment WHERE complaint_id = #{complaintId}")
    Long deleteAttachmentsByComplaintId(Long complaintId);
}
