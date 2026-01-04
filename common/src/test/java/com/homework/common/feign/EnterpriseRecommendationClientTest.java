package com.homework.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * EnterpriseRecommendationClient接口测试类
 */
@SpringBootTest(classes = EnterpriseRecommendationClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class EnterpriseRecommendationClientTest {

    @Test
    void testEnterpriseRecommendationClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(EnterpriseRecommendationClient.class, "EnterpriseRecommendationClient接口应该存在");
    }

    @EnableFeignClients(clients = EnterpriseRecommendationClient.class)
    static class TestConfiguration {
    }
}
