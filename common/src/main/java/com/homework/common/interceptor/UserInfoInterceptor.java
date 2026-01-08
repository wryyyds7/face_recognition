package com.homework.common.interceptor;

import com.homework.common.domain.entity.UserContext;
import com.homework.common.service.PermittionService;
import com.homework.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;


/**
 * 9.23 用户信息拦截器
 * tnnd,记得这个是mvc的！要加配置类才能生效！而且要扫描到！！
 * 记得放到META-INF下！！
 *  ！！找了我半天！！！
 * @author wry
 */
@Component
public class UserInfoInterceptor implements HandlerInterceptor{

    private final PermittionService permittionService;

    @Autowired
    public UserInfoInterceptor(PermittionService permittionService) {
        System.out.println("UserInfoInterceptor have initialized");
        this.permittionService = permittionService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取用户信息 TODO: 注意头部信息的名字是啥，回头做一个文档出来，别忘了
//        TODO: 之后搞成jwt加密后的
        System.out.println("enter preHandler");
        System.out.println("✅ [UserInfoInterceptor] Request URI: " + request.getRequestURI());

        // 白名单路径，不需要认证
        List<String> whiteList = Arrays.asList(
                "/in/**",
                "/actuator/**",
                "/swagger-ui/**",
                "/voice/**",
                "/v3/api-docs/**",
                "/error"
        );
        
        // 检查请求路径是否在白名单中
        String requestURI = request.getRequestURI();
        for (String whitePath : whiteList) {
            // 使用ant风格路径匹配
            if (org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR.equals(whitePath.substring(whitePath.length() - 1))) {
                // 如果白名单路径以/结尾，检查前缀匹配
                if (requestURI.startsWith(whitePath)) {
                    System.out.println("✅ [UserInfoInterceptor] Path in whitelist: " + requestURI);
                    return true;
                }
            } else if (whitePath.endsWith("/**")) {
                // 处理/**通配符
                String prefix = whitePath.substring(0, whitePath.length() - 3);
                if (requestURI.startsWith(prefix)) {
                    System.out.println("✅ [UserInfoInterceptor] Path in whitelist: " + requestURI);
                    return true;
                }
            } else {
                // 精确匹配
                if (requestURI.equals(whitePath)) {
                    System.out.println("✅ [UserInfoInterceptor] Path in whitelist: " + requestURI);
                    return true;
                }
            }
        }

        // 从Authorization头获取JWT token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization header");
            System.out.println("没有Authorization头，拦截");
            return false;
        }

        try {
            // 解析JWT token前先清理token
            String cleanedToken = authHeader;
            if (cleanedToken != null) {
                cleanedToken = cleanedToken.trim();
                if (cleanedToken.startsWith("Bearer ")) {
                    cleanedToken = cleanedToken.substring(7);
                }
            }
            
            // 解析清理后的JWT token
            Claims claims = JwtUtils.parseToken(cleanedToken);
            Integer userIdInt = (Integer) claims.get("userId");
            if (userIdInt == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token: missing userId");
                System.out.println("token中没有userId，拦截");
                return false;
            }

            Long userId = Long.valueOf(userIdInt);
            List<String> roles = (List<String>) claims.get("roles");

            // 将用户信息存入ThreadLocal
            UserContext.setUser(userId);
            UserContext.setRoles(roles);
            UserContext.setToken(cleanedToken); // 存储清理后的token到UserContext

            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token: " + e.getMessage());
            System.out.println("token解析失败，拦截: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}