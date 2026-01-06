package com.homework.recognition.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.Result;
import com.homework.recognition.domain.entity.FaceRecognitionLog;
import com.homework.recognition.service.AttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 人脸识别人物识别日志控制器
 *
 * @author homework
 */
@RestController
@RequestMapping("/recognition/log")
@Api(tags = "识别日志管理")
public class RecognitionLogController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(RecognitionLogController.class);
    
    private final AttendanceService attendanceService;
    
    @Autowired
    public RecognitionLogController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    
    /**
     * 获取识别日志列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 识别日志列表
     */
    @GetMapping
    @ApiOperation("获取识别日志列表")
    public Result getRecognitionLogs(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        try {
            log.info("获取识别日志列表：startTime={}, endTime={}", startTime, endTime);
            
            List<FaceRecognitionLog> logs = attendanceService.getRecognitionLogs(startTime, endTime);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("获取识别日志列表失败：{}", e.getMessage(), e);
            return Result.error("获取识别日志列表失败");
        }
    }
    
    /**
     * 根据日志ID获取识别日志
     *
     * @param logId 日志ID
     * @return 识别日志
     */
    @GetMapping("/{logId}")
    @ApiOperation("根据日志ID获取识别日志")
    public Result getRecognitionLogById(@PathVariable Long logId) {
        try {
            log.info("根据日志ID获取识别日志：logId={}", logId);
            
            FaceRecognitionLog log = attendanceService.getRecognitionLogById(logId);
            return Result.success(log);
        } catch (Exception e) {
            log.error("根据日志ID获取识别日志失败：{}", e.getMessage(), e);
            return Result.error("根据日志ID获取识别日志失败");
        }
    }
}