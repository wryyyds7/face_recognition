package com.homework.common.domain.dto;

import lombok.Data;

/**
 * 学习资源推荐请求DTO
 *
 * @author ruoyi
 */
@Data
public class LearningResourceRecommendationRequestDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 技能ID
     */
    private String skillId;

    /**
     * 推荐资源数量限制
     */
    private Integer limit;
}
