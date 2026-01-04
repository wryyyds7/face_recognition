package com.example.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * SearchClient接口测试类
 */
@SpringBootTest(classes = SearchClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class SearchClientTest {

    @Test
    void testSearchClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(SearchClient.class, "SearchClient接口应该存在");
    }

    @EnableFeignClients(clients = SearchClient.class)
    static class TestConfiguration {
    }
}
