package com.homework.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * LearningPathRecommendationClient接口测试类
 */
@SpringBootTest(classes = LearningPathRecommendationClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class LearningPathRecommendationClientTest {

    @Test
    void testLearningPathRecommendationClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(LearningPathRecommendationClient.class, "LearningPathRecommendationClient接口应该存在");
    }

    @EnableFeignClients(clients = LearningPathRecommendationClient.class)
    static class TestConfiguration {
    }
}
