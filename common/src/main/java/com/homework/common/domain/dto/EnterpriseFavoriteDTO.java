package com.homework.common.domain.dto;

import lombok.Data;

/**
 * 企业收藏DTO
 *
 * @author ruoyi
 */
@Data
public class EnterpriseFavoriteDTO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 企业ID
     */
    private Long enterpriseId;
}
