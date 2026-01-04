package com.homework.common.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 标签推荐请求DTO
 * 用于调用Python标签推荐服务的请求参数
 */
public class TagRecommendationRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 输入标签列表
     */
    private List<String> tags;

    /**
     * 推荐数量，默认为10
     */
    private Integer topN = 10;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getTopN() {
        return topN;
    }

    public void setTopN(Integer topN) {
        this.topN = topN;
    }
}