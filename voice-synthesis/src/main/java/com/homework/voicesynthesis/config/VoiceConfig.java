package com.homework.voicesynthesis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 语音合成配置类
 *
 * @author homework
 */
@Component
@ConfigurationProperties(prefix = "voice.synthesis")
public class VoiceConfig {

    /**
     * 默认音量（0-100）
     */
    private int volume;

    /**
     * 默认语速（-10到+10）
     */
    private int rate;

    /**
     * 默认音色类型（female/male）
     */
    private String defaultVoiceType;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getDefaultVoiceType() {
        return defaultVoiceType;
    }

    public void setDefaultVoiceType(String defaultVoiceType) {
        this.defaultVoiceType = defaultVoiceType;
    }
}