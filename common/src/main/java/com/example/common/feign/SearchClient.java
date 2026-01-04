package com.example.common.feign;

import com.example.common.aop.Log;
import com.example.common.domain.entity.Enterprise;
import com.example.common.domain.enums.BusinessType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(value = "search")
public interface SearchClient {

    //@PreAuthorize("@permittionService.hasPermi('system:enterprise:search')")
    @Log(title = "(feign)利用engine寻找企业", businessType = BusinessType.OTHER)
    @GetMapping("/system/search/engine/enterprise")
    public List<Enterprise> list(@RequestParam String enterpriseName);
}
