package com.homework.common.domain.dto;

import lombok.Data;

/**
 * 技能掌握度预测请求DTO
 *
 * @author ruoyi
 */
@Data
public class SkillMasteryPredictionRequestDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 技能ID
     */
    private String skillId;
}
