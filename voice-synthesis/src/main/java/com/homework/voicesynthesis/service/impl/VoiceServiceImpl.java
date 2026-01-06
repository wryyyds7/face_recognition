package com.homework.voicesynthesis.service.impl;

import com.homework.voicesynthesis.config.VoiceConfig;
import com.homework.voicesynthesis.service.VoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 语音合成服务实现类
 *
 * @author homework
 */
@Service
public class VoiceServiceImpl implements VoiceService {

    private static final Logger log = LoggerFactory.getLogger(VoiceServiceImpl.class);

    private final VoiceConfig voiceConfig;

    @Autowired
    public VoiceServiceImpl(VoiceConfig voiceConfig) {
        this.voiceConfig = voiceConfig;
    }

    @Override
    public boolean speak(String text) {
        return speak(text, voiceConfig.getDefaultVoiceType());
    }

    @Override
    public boolean speak(String text, String voiceType) {
        return speak(text, voiceType, voiceConfig.getVolume(), voiceConfig.getRate());
    }

    @Override
    public boolean speak(String text, String voiceType, int volume, int rate) {
        try {
            // 转义单引号，避免PowerShell命令执行错误
            String escapedText = text.replace("'", "''");

            // 构建PowerShell命令
            String command = "powershell -Command " +
                    "Add-Type -AssemblyName System.Speech; " +
                    "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                    "$speak.Volume = " + volume + "; " +
                    "$speak.Rate = " + rate + "; " +
                    "if ('" + voiceType + "' -eq 'male') { " +
                    "    $voice = $speak.GetInstalledVoices() | Where-Object { $_.VoiceInfo.Gender -eq 'Male' } | Select-Object -First 1; " +
                    "    if ($voice) { $speak.SelectVoice($voice.VoiceInfo.Name); } " +
                    "} else { " +
                    "    $voice = $speak.GetInstalledVoices() | Where-Object { $_.VoiceInfo.Gender -eq 'Female' } | Select-Object -First 1; " +
                    "    if ($voice) { $speak.SelectVoice($voice.VoiceInfo.Name); } " +
                    "} " +
                    "$speak.Speak('" + escapedText + "');";

            log.info("执行语音合成命令: {}", command);

            // 执行命令
            Runtime.getRuntime().exec(command);
            log.info("语音合成成功: {}", text);
            return true;
        } catch (IOException e) {
            log.error("语音合成失败: {}", e.getMessage(), e);
            return false;
        }
    }
}