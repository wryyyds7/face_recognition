package com.homework.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "enterprise")
public interface EnterpriseClient {

    @GetMapping("/enterprise/getEnterprise")
    public String getEnterprise();
}
