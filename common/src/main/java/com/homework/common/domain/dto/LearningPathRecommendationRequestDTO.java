package com.homework.common.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 学习路径推荐请求DTO
 *
 * @author ruoyi
 */
@Data
public class LearningPathRecommendationRequestDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 当前技能列表
     */
    private List<String> currentSkills;

    /**
     * 目标技能列表
     */
    private List<String> targetSkills;

    /**
     * 学习时间框架
     */
    private String timeframe;

    /**
     * 学习风格
     */
    private String learningStyle;
}
