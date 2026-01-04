package com.homework.common.domain.dto;

import lombok.Data;

/**
 * 活动注册DTO
 */
@Data
public class EventRegistrationDTO {
    private Long userId;     // 用户ID
    private Long eventId;    // 活动ID
    private String status;   // 注册状态
    private String remarks;  // 备注信息
}