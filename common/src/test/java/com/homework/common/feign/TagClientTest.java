package com.homework.common.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TagClient接口测试类
 */
@SpringBootTest(classes = TagClientTest.TestConfiguration.class)
@ActiveProfiles("test")
public class TagClientTest {

    @Test
    void testTagClientInterface() {
        // 测试接口是否可以被实例化
        assertNotNull(TagClient.class, "TagClient接口应该存在");
    }

    @Test
    void testEntityTagDTO() {
        // 测试内部类是否可以被实例化
        TagClient.EntityTagDTO entityTagDTO = new TagClient.EntityTagDTO("enterprise", 1L, 1L);
        assertNotNull(entityTagDTO, "EntityTagDTO应该可以被实例化");
        assertNotNull(entityTagDTO.getEntityType(), "EntityType应该不为空");
        assertNotNull(entityTagDTO.getEntityId(), "EntityId应该不为空");
        assertNotNull(entityTagDTO.getTagId(), "TagId应该不为空");
    }

    @EnableFeignClients(clients = TagClient.class)
    static class TestConfiguration {
    }
}
