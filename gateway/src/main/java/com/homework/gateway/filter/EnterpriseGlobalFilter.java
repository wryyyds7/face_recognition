package com.homework.gateway.filter;
import com.homework.common.utils.CurrentHolder;
import com.homework.common.utils.JwtUtils;
import com.homework.gateway.config.AuthProperties;
import com.homework.gateway.config.JwtPreperties;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 11.1 wry
 * 这个类是一个网管的拦截器类，废弃原有默认，更改为现有的抽露出的逻辑
 *
 * 作用为抽离出token，解析并判断
 */
@Component
public class EnterpriseGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public EnterpriseGlobalFilter(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        Object userIdObj = null;
        System.out.println("当前请求路径：" + request.getPath().value());
        // 2.
        if(isExcludePath(request.getPath().value())){
            System.out.println("被排除成功");
            return chain.filter(exchange);
        }
        System.out.println("非排除路径，解析ing");
        String token = null;
        // 3. 获取token
        List<String> headers = request.getHeaders().get("Authorization");
        if(headers != null && headers.size() > 0){
            token = headers.get(0);
        }
        try{
            // 4. 解析token
            Claims claims = JwtUtils.parseToken(token);
            System.out.println("进入解析claims:" + claims);
            // 解析出来的前提是有id这个属性 TODO: 这里存疑---id是放到哪里？
            userIdObj = claims.get("userId");
            System.out.println("当前用户id：" + userIdObj);
            if (userIdObj != null) {
                CurrentHolder.setCurrentId(Integer.valueOf(userIdObj.toString()));
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.empty());
            }
            String finalUserIdObj = userIdObj.toString();
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("Authorization", "Bearer " + token)
                    .build();
            // 备用
            ServerWebExchange swe = exchange.mutate()
                    .request(builder -> {
                        builder.header("userId", finalUserIdObj);
                    })
                    .build();

            exchange = exchange.mutate().request(modifiedRequest).build();
            CurrentHolder.remove();

            System.out.println("完成解析：swe为"+ swe);
            return chain.filter(exchange);
        } catch (Exception e) {
            // 5. 解析失败
            e.printStackTrace();
            return response.setComplete();
        }
        // 这个方法是改变请求的，作用为改变请求头，具体是增加了userId这个
//        String finalUserIdObj = userIdObj.toString();
//        ServerWebExchange swe = exchange.mutate()
//                .request(builder -> {
//                    builder.header("userId", finalUserIdObj);
//                })
//                .build();
//        CurrentHolder.remove();
//        return chain.filter(swe);

    }

    private boolean isExcludePath(String path) {
        System.out.println("enter");
        for (String excludePath : authProperties.getExcludePaths()) {
            System.out.println("进入过滤器，当前路径为:"+excludePath);
            if(excludePath.startsWith("/system") ){
                continue;
            }
            if (antPathMatcher.match(excludePath, path)) {
                System.out.println(excludePath);
                System.out.println("请求成功");
                return true; // 匹配成功，需要排除
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        // 这里是责任链的优先级，越小越优
        return -100000;
    }
}
