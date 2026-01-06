package com.homework.common.feign;

import com.homework.common.domain.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 语音合成Feign客户端
 *
 * @author homework
 */
@FeignClient(name = "voice-synthesis", path = "/voice")
public interface VoiceSynthesisClient {

    /**
     * 语音合成并播放
     *
     * @param text 要合成的文本
     * @return 执行结果
     */
    @PostMapping("/speak")
    Result speak(@RequestParam("text") String text);

    /**
     * 语音合成并播放（指定音色）
     *
     * @param text 要合成的文本
     * @param voiceType 音色类型（female/male）
     * @return 执行结果
     */
    @PostMapping("/speak/voice")
    Result speakWithVoice(@RequestParam("text") String text, @RequestParam("voiceType") String voiceType);

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
    Result speakFull(@RequestParam("text") String text,
                    @RequestParam("voiceType") String voiceType,
                    @RequestParam("volume") Integer volume,
                    @RequestParam("rate") Integer rate);
}