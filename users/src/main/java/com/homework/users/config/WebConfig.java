package com.homework.users.config;

import com.homework.common.interceptor.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserInfoInterceptor userInfoInterceptor;

    @Autowired
    public WebConfig(UserInfoInterceptor userInfoInterceptor) {
        this.userInfoInterceptor = userInfoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册用户信息拦截器，拦截所有请求
        registry.addInterceptor(userInfoInterceptor)
                .addPathPatterns("/**");
    }
}
