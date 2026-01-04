package com.homework.common.feign;

import com.homework.common.domain.dto.EnterpriseRecommendationRequestDTO;
import com.homework.common.domain.dto.EnterpriseRecommendationResponseDTO;
import com.homework.common.domain.dto.TagRecommendationRequestDTO;
import com.homework.common.domain.dto.TagRecommendationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 企业推荐服务Feign客户端
 * 用于调用Python企业推荐服务的接口
 */
@FeignClient(value = "enterprise-recommendation", url = "http://localhost:5000")
public interface EnterpriseRecommendationClient {

    /**
     * 推荐企业接口
     *
     * @param request 请求参数，包含userId和tags
     * @return 企业推荐结果
     */
    @PostMapping(value = "/api/recommend/enterprise", consumes = "application/json")
    EnterpriseRecommendationResponseDTO recommendEnterprise(@RequestBody EnterpriseRecommendationRequestDTO request);

    /**
     * 推荐相关标签接口
     *
     * @param request 请求参数，包含tags
     * @return 相关标签推荐结果
     */
    @PostMapping(value = "/api/recommend/tags", consumes = "application/json")
    TagRecommendationResponseDTO recommendTags(@RequestBody TagRecommendationRequestDTO request);
}