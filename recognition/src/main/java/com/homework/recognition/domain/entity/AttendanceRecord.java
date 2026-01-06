package com.homework.recognition.domain.entity;

import com.homework.common.domain.entity.BaseEntity;

import java.util.Date;

/**
 * 打卡记录
 *
 * @author homework
 */
public class AttendanceRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 用户真实姓名 */
    private String realName;

    /** 打卡时间 */
    private Date punchTime;

    /** 打卡类型（1：上班，2：下班） */
    private Integer punchType;

    /** 打卡状态（1：成功，0：失败） */
    private Integer status;

    /** 识别日志ID */
    private Long recognitionLogId;

    /** 备注 */
    private String remark;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Date getPunchTime() {
        return punchTime;
    }

    public void setPunchTime(Date punchTime) {
        this.punchTime = punchTime;
    }

    public Integer getPunchType() {
        return punchType;
    }

    public void setPunchType(Integer punchType) {
        this.punchType = punchType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRecognitionLogId() {
        return recognitionLogId;
    }

    public void setRecognitionLogId(Long recognitionLogId) {
        this.recognitionLogId = recognitionLogId;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }
}