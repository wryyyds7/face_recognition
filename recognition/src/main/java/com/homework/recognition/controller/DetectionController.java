package com.homework.recognition.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.Result;
import com.homework.recognition.service.impl.FaceDetectionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 自动检测控制控制器
 *
 * @author homework
 */
@RestController
@RequestMapping("/recognition/detection")
@Api(tags = "自动检测管理")
public class DetectionController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(DetectionController.class);
    
    private final FaceDetectionServiceImpl faceDetectionService;
    
    @Autowired
    public DetectionController(FaceDetectionServiceImpl faceDetectionService) {
        this.faceDetectionService = faceDetectionService;
    }
    
    /**
     * 开启自动检测
     *
     * @return 操作结果
     */
    @PostMapping("/start")
    @ApiOperation("开启自动检测")
    public Result startDetection() {
        try {
            log.info("开启自动检测");
            faceDetectionService.startDetection();
            return Result.success("自动检测已开启");
        } catch (Exception e) {
            log.error("开启自动检测失败：{}", e.getMessage(), e);
            return Result.error("开启自动检测失败");
        }
    }
    
    /**
     * 停止自动检测
     *
     * @return 操作结果
     */
    @PostMapping("/stop")
    @ApiOperation("停止自动检测")
    public Result stopDetection() {
        try {
            log.info("停止自动检测");
            faceDetectionService.stopDetection();
            return Result.success("自动检测已停止");
        } catch (Exception e) {
            log.error("停止自动检测失败：{}", e.getMessage(), e);
            return Result.error("停止自动检测失败");
        }
    }
    
    /**
     * 获取自动检测状态
     *
     * @return 检测状态
     */
    @GetMapping("/status")
    @ApiOperation("获取自动检测状态")
    public Result getDetectionStatus() {
        try {
            boolean status = faceDetectionService.getDetectionStatus();
            Map<String, Object> result = new HashMap<>();
            result.put("enabled", status);
            result.put("message", status ? "自动检测已开启" : "自动检测已停止");
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取自动检测状态失败：{}", e.getMessage(), e);
            return Result.error("获取自动检测状态失败");
        }
    }
    
    /**
     * 开启识别结果保存
     *
     * @return 操作结果
     */
    @PostMapping("/save/enable")
    @ApiOperation("开启识别结果保存")
    public Result enableSaveResult() {
        try {
            log.info("开启识别结果保存");
            faceDetectionService.enableSaveResult();
            return Result.success("识别结果保存已开启");
        } catch (Exception e) {
            log.error("开启识别结果保存失败：{}", e.getMessage(), e);
            return Result.error("开启识别结果保存失败");
        }
    }
    
    /**
     * 关闭识别结果保存
     *
     * @return 操作结果
     */
    @PostMapping("/save/disable")
    @ApiOperation("关闭识别结果保存")
    public Result disableSaveResult() {
        try {
            log.info("关闭识别结果保存");
            faceDetectionService.disableSaveResult();
            return Result.success("识别结果保存已关闭");
        } catch (Exception e) {
            log.error("关闭识别结果保存失败：{}", e.getMessage(), e);
            return Result.error("关闭识别结果保存失败");
        }
    }
}