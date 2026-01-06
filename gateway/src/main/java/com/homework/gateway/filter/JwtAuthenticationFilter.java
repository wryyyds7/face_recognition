package com.homework.gateway.filter;

import com.homework.common.domain.entity.UserDetailsImpl;
import com.homework.common.utils.CurrentHolder;
import com.homework.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//import java.security.SignatureException;
import java.util.List;

// JWT认证过滤器
public class JwtAuthenticationFilter implements WebFilter {
    private final AntPathMatcher pathMatcher = new AntPathMatcher(); // 路径匹配器

    public JwtAuthenticationFilter() {
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 从 Authorization 头获取 token
        String path = exchange.getRequest().getPath().value();
        if (isExcludedPath(path)) {
            return chain.filter(exchange); // 直接放行
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null) {
            try {
                // 验证 token 并获取 userId
                Claims claims = JwtUtils.parseToken(authHeader);
                Integer userId = (Integer) claims.get("userId");
                String password = (String) claims.get("password");
                String username = (String) claims.get("username");
                
                // 从jwt获取角色信息
                List<String> roles = (List<String>) claims.get("roles");
                Long userIdLong = Long.valueOf(userId);
                UserDetailsImpl userDetails = new UserDetailsImpl(userIdLong, username, password, roles);
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                password,
                                userDetails.getAuthorities() // 必须传入权限列表
                        );
                
                // 在WebFlux中使用ReactiveSecurityContextHolder
                // 确保Authorization头被传递给下游服务
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("Authorization", authHeader) // 保留Authorization头
                        .build();
                
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();
                
                return chain.filter(modifiedExchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } catch (ExpiredJwtException e) {
                // 处理过期 Token
                return handleUnauthorized(exchange, "Token expired. Please login again.");
            } catch (MalformedJwtException | SignatureException e) {
                // 处理无效 Token（签名错误/格式错误）
                return handleUnauthorized(exchange, "Invalid token format or signature");
            } catch (Exception e) {
                // 处理其他 token 相关错误
                return handleUnauthorized(exchange, "Invalid token: " + e.getMessage());
            }
        }
        // 未认证
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(Mono.empty());
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(message.getBytes()))
        );
    }

    private boolean isExcludedPath(String path) {
        // 免认证路径
        String[] excludePaths = {
            "/in/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
        };
        for (String excludePath : excludePaths) {
            if (pathMatcher.match(excludePath, path)) {
                return true;
            }
        }
        return false;
    }
}
