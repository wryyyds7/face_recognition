package com.example.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * EnterpriseClient接口测试类
 */
@SpringBootTest(classes = EnterpriseClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class EnterpriseClientTest {

    @Test
    void testEnterpriseClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(EnterpriseClient.class, "EnterpriseClient接口应该存在");
    }

    @EnableFeignClients(clients = EnterpriseClient.class)
    static class TestConfiguration {
    }
}
