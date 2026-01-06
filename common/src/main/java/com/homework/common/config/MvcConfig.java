package com.homework.common.config;

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

    public MvcConfig(UserInfoInterceptor userInfoInterceptor) {
        this.userInfoInterceptor = userInfoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 仅注册拦截器，具体的路径匹配由各个业务模块的WebMvcConfigurer实现配置
        // 避免与业务模块的配置冲突
        // registry.addInterceptor(userInfoInterceptor);
    }

//    @Override
//    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
//        registry.addInterceptor(new UserInfoInterceptor());
//    }

}
