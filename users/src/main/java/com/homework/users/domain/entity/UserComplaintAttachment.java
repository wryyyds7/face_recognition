package com.homework.users.domain.entity;

import com.homework.common.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


public class UserComplaintAttachment extends BaseEntity {
    private Long attachmentId;
    private Long complaintId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Date createTime;

    public UserComplaintAttachment() {
    }

    public UserComplaintAttachment(Long attachmentId, Long complaintId, String fileName, String filePath, Long fileSize, String fileType, Date createTime) {
        this.attachmentId = attachmentId;
        this.complaintId = complaintId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.createTime = createTime;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
