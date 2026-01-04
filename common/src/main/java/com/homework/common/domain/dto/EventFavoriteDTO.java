package com.homework.common.domain.dto;

import lombok.Data;

/**
 * 活动收藏DTO
 *
 * @author ruoyi
 */
@Data
public class EventFavoriteDTO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 活动ID
     */
    private Long eventId;
}
