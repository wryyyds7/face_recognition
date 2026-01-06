package com.homework.voicesynthesis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 语音合成服务启动类
 *
 * @author homework
 */
@SpringBootApplication(scanBasePackages = "com.homework")
@EnableFeignClients(basePackages = "com.homework.common.feign")
@ComponentScan(basePackages = {"com.homework.common"})  // 确保包含所有相关组件
@EnableScheduling
@MapperScan("com.homework.*.mapper")
public class VoiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoiceApplication.class, args);
    }

}