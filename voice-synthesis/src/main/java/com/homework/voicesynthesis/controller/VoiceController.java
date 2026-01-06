package com.homework.voicesynthesis.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.Result;
import com.homework.voicesynthesis.config.VoiceConfig;
import com.homework.voicesynthesis.service.VoiceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 语音合成控制器
 *
 * @author homework
 */
@RestController
@RequestMapping("/voice")
@Api(tags = "语音合成管理")
public class VoiceController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(VoiceController.class);

    private final VoiceService voiceService;
    private final VoiceConfig voiceConfig;

    @Autowired
    public VoiceController(VoiceService voiceService, VoiceConfig voiceConfig) {
        this.voiceService = voiceService;
        this.voiceConfig = voiceConfig;
    }

    /**
     * 语音合成并播放
     *
     * @param text 要合成的文本
     * @return 执行结果
     */
    @PostMapping("/speak")
    @ApiOperation("语音合成并播放")
    public Result speak(@RequestParam String text) {
        try {
            log.info("语音合成请求: {}", text);
            boolean result = voiceService.speak(text);
            if (result) {
                return Result.success("语音合成成功");
            } else {
                return Result.error("语音合成失败");
            }
        } catch (Exception e) {
            log.error("语音合成异常: {}", e.getMessage(), e);
            return Result.error("语音合成异常");
        }
    }

    /**
     * 语音合成并播放（指定音色）
     *
     * @param text 要合成的文本
     * @param voiceType 音色类型（female/male）
     * @return 执行结果
     */
    @PostMapping("/speak/voice")
    @ApiOperation("语音合成并播放（指定音色）")
    public Result speakWithVoice(@RequestParam String text, @RequestParam String voiceType) {
        try {
            log.info("语音合成请求: {}, 音色: {}", text, voiceType);
            boolean result = voiceService.speak(text, voiceType);
            if (result) {
                return Result.success("语音合成成功");
            } else {
                return Result.error("语音合成失败");
            }
        } catch (Exception e) {
            log.error("语音合成异常: {}", e.getMessage(), e);
            return Result.error("语音合成异常");
        }
    }

    /**
     * 语音合成并播放（指定音量和语速）
     *
     * @param text 要合成的文本
     * @param voiceType 音色类型（female/male）
     * @param volume 音量（0-100）
     * @param rate 语速（-10到+10）
     * @return 执行结果
     */
    @PostMapping("/speak/full")
    @ApiOperation("语音合成并播放（指定音量和语速）")
    public Result speakFull(@RequestParam String text, @RequestParam(required = false) String voiceType,
                           @RequestParam(required = false) Integer volume, @RequestParam(required = false) Integer rate) {
        try {
            // 使用默认值
            String finalVoiceType = voiceType != null ? voiceType : voiceConfig.getDefaultVoiceType();
            int finalVolume = volume != null ? volume : voiceConfig.getVolume();
            int finalRate = rate != null ? rate : voiceConfig.getRate();

            log.info("语音合成请求: {}, 音色: {}, 音量: {}, 语速: {}", text, finalVoiceType, finalVolume, finalRate);
            boolean result = voiceService.speak(text, finalVoiceType, finalVolume, finalRate);
            if (result) {
                return Result.success("语音合成成功");
            } else {
                return Result.error("语音合成失败");
            }
        } catch (Exception e) {
            log.error("语音合成异常: {}", e.getMessage(), e);
            return Result.error("语音合成异常");
        }
    }

    /**
     * 获取语音合成配置
     *
     * @return 配置信息
     */
    @GetMapping("/config")
    @ApiOperation("获取语音合成配置")
    public Result getConfig() {
        try {
            return Result.success(voiceConfig);
        } catch (Exception e) {
            log.error("获取配置异常: {}", e.getMessage(), e);
            return Result.error("获取配置异常");
        }
    }
}