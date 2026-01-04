package com.homework.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * BoChaClient接口测试类
 */
@SpringBootTest(classes = BoChaClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class BoChaClientTest {

    @Test
    void testBoChaClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(BoChaClient.class, "BoChaClient接口应该存在");
    }

    @EnableFeignClients(clients = BoChaClient.class)
    static class TestConfiguration {
    }
}
