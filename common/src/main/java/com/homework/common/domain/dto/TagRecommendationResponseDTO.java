package com.homework.common.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 标签推荐响应DTO
 * 用于接收Python标签推荐服务的响应数据
 */
public class TagRecommendationResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回码，200表示成功
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 推荐标签数据
     */
    private TagRecommendationData data;

    /**
     * 推荐标签数据内部类
     */
    public static class TagRecommendationData {
        /**
         * 推荐的标签列表
         */
        private List<RecommendedTag> tags;

        /**
         * 推荐总数
         */
        private Integer total;

        /**
         * 请求参数
         */
        private TagRecommendationRequestDTO request;

        public List<RecommendedTag> getTags() {
            return tags;
        }

        public void setTags(List<RecommendedTag> tags) {
            this.tags = tags;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public TagRecommendationRequestDTO getRequest() {
            return request;
        }

        public void setRequest(TagRecommendationRequestDTO request) {
            this.request = request;
        }
    }

    /**
     * 推荐标签内部类
     */
    public static class RecommendedTag {
        /**
         * 标签名称
         */
        private String tag;

        /**
         * 相似度得分
         */
        private Double similarityScore;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Double getSimilarityScore() {
            return similarityScore;
        }

        public void setSimilarityScore(Double similarityScore) {
            this.similarityScore = similarityScore;
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public TagRecommendationData getData() {
        return data;
    }

    public void setData(TagRecommendationData data) {
        this.data = data;
    }
}