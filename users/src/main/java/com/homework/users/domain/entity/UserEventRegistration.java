package com.homework.users.domain.entity;

import com.homework.common.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 用户活动注册实体类
 */
@Data
public class UserEventRegistration extends BaseEntity {
    private Long id;         // 主键ID
    private Long userId;     // 用户ID
    private Long eventId;    // 活动ID
    private String status;   // 注册状态
    private String remarks;  // 备注信息
}