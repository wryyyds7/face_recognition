package com.homework.common.domain.dto;

import com.homework.common.domain.entity.Position;
import lombok.Data;

import java.util.List;

/**
 * 企业推荐响应DTO
 *
 * @author ruoyi
 */
@Data
public class EnterpriseRecommendationResponseDTO {
    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 推荐结果数据
     */
    private List<EnterpriseRecommendation> data;

    /**
     * 企业推荐项
     */
    @Data
    public static class EnterpriseRecommendation {
        /**
         * 企业ID
         */
        private Long enterpriseId;

        /**
         * 企业名称
         */
        private String enterpriseName;

        /**
         * 相关性得分
         */
        private Double relevanceScore;

        /**
         * 匹配的标签列表
         */
        private List<String> matchedTags;

        /**
         * 推荐的职位列表
         */
        private List<Position> positions;
    }
}
