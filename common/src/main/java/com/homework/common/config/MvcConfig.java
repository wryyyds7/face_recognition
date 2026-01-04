package com.homework.common.config;

import com.homework.common.interceptor.UserInfoInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class MvcConfig implements WebMvcConfigurer {

    private final UserInfoInterceptor userInfoInterceptor;

    public MvcConfig(UserInfoInterceptor userInfoInterceptor) {
        this.userInfoInterceptor = userInfoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInfoInterceptor);
    }

//    @Override
//    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
//        registry.addInterceptor(new UserInfoInterceptor());
//    }

}
