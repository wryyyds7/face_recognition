package com.homework.common;

import com.homework.common.domain.entity.UserContext;
import com.homework.common.service.PermittionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.homework.common.constant.RoleConstants.ADMIN;
import static com.homework.common.constant.RoleConstants.USER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
public class testHasRole {
    @Autowired
    private PermittionService permittionService;

    @Test
    public void testHasRole1() {
        // 模拟登录
        UserContext.setUser(1L);
        UserContext.setRoles(List.of(ADMIN, USER));

        // 测试
        assertTrue(permittionService.hasRole(ADMIN));
        assertTrue(permittionService.hasRole(USER));
        assertFalse(permittionService.hasRole("guest"));
    }
}
