package com.homework.users.domain.entity;

import com.homework.common.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


public class UserComplaint extends BaseEntity {
    private Long complaintId;
    private Long userId;
    private String complainedType;
    private Long complainedId;
    private String complaintTitle;
    private String complaintContent;
    private String complaintStatus;
    private String handleResult;
    private Long handlerId;
    private Date createTime;
    private Date updateTime;

    public UserComplaint() {
    }

    public UserComplaint(Long complaintId, Long userId, String complainedType, Long complainedId, String complaintTitle, String complaintContent, String complaintStatus, String handleResult, Long handlerId, Date createTime, Date updateTime) {
        this.complaintId = complaintId;
        this.userId = userId;
        this.complainedType = complainedType;
        this.complainedId = complainedId;
        this.complaintTitle = complaintTitle;
        this.complaintContent = complaintContent;
        this.complaintStatus = complaintStatus;
        this.handleResult = handleResult;
        this.handlerId = handlerId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComplainedType() {
        return complainedType;
    }

    public void setComplainedType(String complainedType) {
        this.complainedType = complainedType;
    }

    public Long getComplainedId() {
        return complainedId;
    }

    public void setComplainedId(Long complainedId) {
        this.complainedId = complainedId;
    }

    public String getComplaintTitle() {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle) {
        this.complaintTitle = complaintTitle;
    }

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

