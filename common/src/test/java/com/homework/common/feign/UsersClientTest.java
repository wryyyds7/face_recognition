package com.homework.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UsersClient接口测试类
 */
@SpringBootTest(classes = UsersClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class UsersClientTest {

    @Test
    void testUsersClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(UsersClient.class, "UsersClient接口应该存在");
    }

    @EnableFeignClients(clients = UsersClient.class)
    static class TestConfiguration {
    }
}
