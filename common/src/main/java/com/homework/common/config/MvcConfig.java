package com.homework.common.config;

import com.homework.common.interceptor.RateLimitInterceptor;
import com.homework.common.interceptor.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用模块的MVC配置
 * 注意：该配置类仅在没有其他模块提供WebMvcConfigurer实现时生效
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final UserInfoInterceptor userInfoInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public MvcConfig(UserInfoInterceptor userInfoInterceptor, RateLimitInterceptor rateLimitInterceptor) {
        this.userInfoInterceptor = userInfoInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册限流拦截器，放在第一位
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**");
        
        // 注册用户信息拦截器
        registry.addInterceptor(userInfoInterceptor)
                .addPathPatterns("/**");
    }

}
