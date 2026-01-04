package com.example.common.domain.dto;

import lombok.Data;

/**
 * 活动收藏DTO
 *
 * @author ruoyi
 */
@Data
public class ActivityFavoriteDTO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 活动ID
     */
    private Long activityId;
}
