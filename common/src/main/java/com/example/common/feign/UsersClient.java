package com.example.common.feign;

import com.example.common.domain.entity.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "users")
public interface UsersClient {

    @GetMapping("/user/searchUser")
    @ApiOperation("用户搜索服务")
    User getUserById(@RequestBody Long userId);
}
