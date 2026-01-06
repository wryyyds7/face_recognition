package com.homework.gateway.filter;

import com.homework.gateway.config.AuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthGatewayFilter extends AbstractGatewayFilterFactory<AuthGatewayFilter.Config> {
    private final List<String> excludePaths;

    @Autowired
    public AuthGatewayFilter(AuthProperties authProperties) {
        super(Config.class);
        this.excludePaths = authProperties.getExcludePaths();
    }

    @Override
    public GatewayFilter apply(Config config) {
        System.out.println("进入过滤器AuthGatewayFilter");
        return (exchange, chain) -> {
            // 检查当前请求路径是否在排除列表中
            String path = exchange.getRequest().getPath().toString();
            if (isExcludePath(path)) {
                return chain.filter(exchange); // 直接放行，不验证
            }
            // 1. 检查认证服务设置的 userId 头
            String userId = exchange.getRequest().getHeaders().getFirst("userId");

            // 2. 验证失败 → 返回 401
            if (userId == null) {
                return handleUnauthorized(exchange, "Missing userId");
            }

            // 3. 将 userId 传递给微服务（用 X-User-Id 头，避免冲突）
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId) // 网关传递给微服务
                    .build();

            // 4. 继续请求链
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(message.getBytes()))
        );
    }
    private boolean isExcludePath(String path) {
        System.out.println("enter AuthGatewayFilter isExcludePath:" + excludePaths.stream().anyMatch(p -> path.startsWith(p)));
        return excludePaths.stream().anyMatch(p -> path.startsWith(p));
    }
    public static class Config {
        // 配置属性（这里不需要）
    }
}