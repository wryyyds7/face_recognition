package com.homework.recognition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 打卡配置类
 * 
 * @author homework
 */
@Component
@ConfigurationProperties(prefix = "attendance")
public class AttendanceConfig {

    /**
     * 打卡时间间隔（小时），默认12小时
     */
    private int punchInterval = 12;

    /**
     * 获取打卡时间间隔
     * 
     * @return 打卡时间间隔（小时）
     */
    public int getPunchInterval() {
        return punchInterval;
    }

    /**
     * 设置打卡时间间隔
     * 
     * @param punchInterval 打卡时间间隔（小时）
     */
    public void setPunchInterval(int punchInterval) {
        this.punchInterval = punchInterval;
    }
}
