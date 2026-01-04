package com.example.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Ipify API Feign客户端
 * 用于调用ident.me API获取公网IP地址
 */
@FeignClient(name = "ipify", url = "https://ident.me")
public interface IpifyClient {

    /**
     * 通过ident.me API获取公网IP地址
     *
     * @return 公网IP地址
     */
    @GetMapping("/")
    String getPublicIp();
}
