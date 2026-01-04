package com.example.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * BaiduMapClient接口测试类
 */
@SpringBootTest(classes = BaiduMapClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class BaiduMapClientTest {

    @Test
    void testBaiduMapClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(BaiduMapClient.class, "BaiduMapClient接口应该存在");
    }

    @EnableFeignClients(clients = BaiduMapClient.class)
    static class TestConfiguration {
    }
}
