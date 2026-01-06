package com.homework.recognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 人脸识别服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.homework", exclude = {
    LifecycleMvcEndpointAutoConfiguration.class,
    org.springframework.cloud.autoconfigure.RefreshAutoConfiguration.class
})
@EnableFeignClients(basePackages = "com.homework.common.feign")
@EnableScheduling
public class RecognitionApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecognitionApplication.class, args);
    }

}