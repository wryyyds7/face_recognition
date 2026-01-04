package com.homework.common.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 学习路径推荐响应DTO
 *
 * @author ruoyi
 */
@Data
public class LearningPathRecommendationResponseDTO {
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
    private LearningPathData data;

    /**
     * 学习路径数据
     */
    @Data
    public static class LearningPathData {
        /**
         * 用户ID
         */
        private String userId;

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

        /**
         * 学习路径
         */
        private List<Skill> path;

        /**
         * 预计学习时间
         */
        private String estimatedTime;

        /**
         * 总技能数
         */
        private Integer totalSkills;

        /**
         * 总资源数
         */
        private Integer totalResources;
    }

    /**
     * 技能信息
     */
    @Data
    public static class Skill {
        /**
         * 技能ID
         */
        private String skillId;

        /**
         * 技能名称
         */
        private String name;

        /**
         * 技能难度
         */
        private String difficulty;

        /**
         * 技能分类
         */
        private String category;

        /**
         * 学习资源列表
         */
        private List<Resource> resources;

        /**
         * 前置技能列表
         */
        private List<String> prerequisites;
    }

    /**
     * 学习资源信息
     */
    @Data
    public static class Resource {
        /**
         * 资源ID
         */
        private String resourceId;

        /**
         * 资源标题
         */
        private String title;

        /**
         * 资源类型
         */
        private String type;

        /**
         * 资源难度
         */
        private String difficulty;

        /**
         * 资源时长
         */
        private Integer duration;

        /**
         * 资源URL
         */
        private String url;

        /**
         * 资源相关性
         */
        private Double relevance;
    }
}
