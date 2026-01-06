package com.homework.common.feign;

import com.homework.common.domain.entity.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Feign请求拦截器，为所有内部Feign接口自动添加Authorization头
 * @author wry
 */
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    // 需要跳过token添加的服务名称列表
    private static final List<String> SKIP_TOKEN_SERVICES = Arrays.asList("baiduMap", "BoChaSearchEngine", "ipify");
    
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 检查是否需要跳过token添加
        if (shouldSkipToken(requestTemplate)) {
            System.out.println("⏭️ [FeignAuthInterceptor] Skipping token for Feign request: " + requestTemplate.url());
            return;
        }
        
        // 从UserContext中获取当前用户的token
        String token = UserContext.getToken();
        if (token != null) {
            // 添加Authorization头
            requestTemplate.header("Authorization", token);
            System.out.println("✅ [FeignAuthInterceptor] Added Authorization header to Feign request: " + requestTemplate.url());
        } else {
            System.out.println("⚠️ [FeignAuthInterceptor] No token found in UserContext for Feign request: " + requestTemplate.url());
        }
    }
    
    /**
     * 检查是否需要跳过token添加
     * @param requestTemplate 请求模板
     * @return 是否需要跳过
     */
    private boolean shouldSkipToken(RequestTemplate requestTemplate) {
        // 1. 检查是否为外部服务（URL以http://或https://开头）
        String url = requestTemplate.url();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return true;
        }
        
        // 2. 检查服务名称是否在跳过列表中
        String feignClientName = requestTemplate.feignTarget().name();
        if (feignClientName != null) {
            for (String skipService : SKIP_TOKEN_SERVICES) {
                if (feignClientName.equalsIgnoreCase(skipService)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}