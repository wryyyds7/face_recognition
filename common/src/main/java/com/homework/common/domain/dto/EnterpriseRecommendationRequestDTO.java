package com.homework.common.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 企业推荐请求DTO
 *
 * @author ruoyi
 */
@Data
public class EnterpriseRecommendationRequestDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户标签列表
     */
    private List<String> tags;
}
