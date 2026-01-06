package com.homework.users.domain.entity;

import com.homework.common.domain.entity.BaseEntity;

/**
 * 用户求职偏好实体类
 *
 * @author ruoyi
 */
public class UserJobPreference extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getExpectedIndustry() {
        return expectedIndustry;
    }

    public void setExpectedIndustry(String expectedIndustry) {
        this.expectedIndustry = expectedIndustry;
    }

    public String getExpectedPositionType() {
        return expectedPositionType;
    }

    public void setExpectedPositionType(String expectedPositionType) {
        this.expectedPositionType = expectedPositionType;
    }

    public String getExpectedLocation() {
        return expectedLocation;
    }

    public void setExpectedLocation(String expectedLocation) {
        this.expectedLocation = expectedLocation;
    }

    public String getExpectedSalaryRange() {
        return expectedSalaryRange;
    }

    public void setExpectedSalaryRange(String expectedSalaryRange) {
        this.expectedSalaryRange = expectedSalaryRange;
    }

    public String getExpectedWorkExperience() {
        return expectedWorkExperience;
    }

    public void setExpectedWorkExperience(String expectedWorkExperience) {
        this.expectedWorkExperience = expectedWorkExperience;
    }
}
