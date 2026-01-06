package com.homework.voicesynthesis.service;

/**
 * 语音合成服务接口
 *
 * @author homework
 */
public interface VoiceService {

    /**
     * 语音合成并播放
     *
     * @param text 要合成的文本
     * @return 执行结果
     */
    boolean speak(String text);

    /**
     * 语音合成并播放（指定音色）
     *
     * @param text 要合成的文本
     * @param voiceType 音色类型（female/male）
     * @return 执行结果
     */
    boolean speak(String text, String voiceType);

    /**
     * 语音合成并播放（指定音量和语速）
     *
     * @param text 要合成的文本
     * @param voiceType 音色类型（female/male）
     * @param volume 音量（0-100）
     * @param rate 语速（-10到+10）
     * @return 执行结果
     */
    boolean speak(String text, String voiceType, int volume, int rate);
}