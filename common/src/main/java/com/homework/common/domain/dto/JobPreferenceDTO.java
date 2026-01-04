package com.homework.common.domain.dto;

import lombok.Data;

/**
 * 用户求职偏好DTO
 *
 * @author ruoyi
 */
@Data
public class JobPreferenceDTO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 期望行业
     */
    private String expectedIndustry;

    /**
     * 期望职位类型
     */
    private String expectedPositionType;

    /**
     * 期望工作地点
     */
    private String expectedLocation;

    /**
     * 期望薪资范围
     */
    private String expectedSalaryRange;

    /**
     * 期望工作经验
     */
    private String expectedWorkExperience;
}
