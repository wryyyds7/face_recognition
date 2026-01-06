package com.homework.gateway.config;

import com.homework.gateway.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // 关键：禁用 CSRF 保护
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/in/**")
                                .permitAll() // 免认证路径
                        .anyExchange().authenticated() // 其他路径需要认证
                )
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION); // 添加认证过滤
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
