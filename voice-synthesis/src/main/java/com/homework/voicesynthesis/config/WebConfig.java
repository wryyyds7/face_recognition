package com.homework.voicesynthesis.config;

import com.homework.common.interceptor.UserInfoInterceptor;
import com.homework.common.service.PermittionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Component("voiceWebConfig")
public class WebConfig implements WebMvcConfigurer {

    private final PermittionService permittionService;

    private final UserInfoInterceptor userInfoInterceptor; // 通过构造注入

    public WebConfig(PermittionService permittionService, UserInfoInterceptor userInfoInterceptor) {
        System.out.println("✅ voice WebConfig initialized with UserInfoInterceptor");
        this.permittionService = permittionService;
        this.userInfoInterceptor = userInfoInterceptor; // 从容器获取实例
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("已增加拦截器");
        registry.addInterceptor(userInfoInterceptor) // 使用容器管理的实例
                .addPathPatterns("/**")

                .excludePathPatterns("/in/**")
                .excludePathPatterns("/voice/**")
                .excludePathPatterns("/ai/api/spark/**"); // 排除AI相关接口
            }
}
