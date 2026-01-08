package com.homework.common.interceptor;

import com.homework.common.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 限流拦截器
 * 用于拦截请求并进行限流控制
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    
    // 白名单路径，不需要限流
    private final List<String> whiteList = Arrays.asList(
            "/in/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
    );

    @Autowired
    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径和IP
        String requestURI = request.getRequestURI();
        String clientIp = getClientIp(request);
        
        // 检查请求路径是否在白名单中
        if (isInWhiteList(requestURI)) {
            return true;
        }
        
        // 检查是否允许通过
        if (!rateLimitService.isAllowed(clientIp, requestURI)) {
            // 返回429 Too Many Requests
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": 429, \"message\": \"请求过于频繁，请稍后再试\", \"data\": null}");
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取客户端IP地址
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是IPv6地址或包含多个IP，只取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // 处理本地回环地址
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "localhost";
        }
        return ip;
    }
    
    /**
     * 检查请求路径是否在白名单中
     * @param requestURI 请求路径
     * @return 是否在白名单中
     */
    private boolean isInWhiteList(String requestURI) {
        for (String whitePath : whiteList) {
            // 使用ant风格路径匹配
            if (whitePath.endsWith("/**")) {
                // 处理/**通配符
                String prefix = whitePath.substring(0, whitePath.length() - 3);
                if (requestURI.startsWith(prefix)) {
                    return true;
                }
            } else if (whitePath.endsWith("/**")) {
                // 处理/*通配符
                String prefix = whitePath.substring(0, whitePath.length() - 2);
                if (requestURI.startsWith(prefix)) {
                    return true;
                }
            } else {
                // 精确匹配
                if (requestURI.equals(whitePath)) {
                    return true;
                }
            }
        }
        return false;
    }
}