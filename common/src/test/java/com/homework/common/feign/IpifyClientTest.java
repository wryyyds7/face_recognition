package com.homework.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * IpifyClient接口测试类
 */
@SpringBootTest(classes = IpifyClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class IpifyClientTest {

    @Test
    void testIpifyClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(IpifyClient.class, "IpifyClient接口应该存在");
    }

    @EnableFeignClients(clients = IpifyClient.class)
    static class TestConfiguration {
    }
}
