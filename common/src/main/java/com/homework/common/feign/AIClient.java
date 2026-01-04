package com.homework.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * AI服务Feign客户端
 * 用于调用AI服务的接口进行企业官网过滤
 */
@FeignClient(value = "AI")
public interface AIClient {

    /**
     * 企业官网过滤接口
     *
     * @param request 请求参数，包含message和history
     * @return AI返回的过滤结果
     */
    @PostMapping(value = "/ai/api/spark/enterpriseFilterByAI", consumes = "application/json")
    Map<String, Object> enterpriseFilterByAI(@RequestBody Map<String, Object> request);

    /**
     * 默认方法，简化调用
     *
     * @param message 搜索结果消息
     * @return AI返回的过滤结果
     */
    default Map<String, Object> filterEnterpriseWebsite(String message) {
        Map<String, Object> request = new java.util.HashMap<>();
        request.put("message", message);
        request.put("history", new java.util.ArrayList<>());
        return enterpriseFilterByAI(request);
    }
}