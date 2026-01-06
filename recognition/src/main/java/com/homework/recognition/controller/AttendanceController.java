package com.homework.recognition.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.Result;
import com.homework.recognition.domain.entity.AttendanceRecord;
import com.homework.recognition.service.AttendanceService;
import com.homework.recognition.service.FaceDetectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 打卡控制器
 *
 * @author homework
 */
@RestController
@RequestMapping("/recognition/attendance")
@Api(tags = "打卡管理")
public class AttendanceController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);
    
    private final AttendanceService attendanceService;
    private final FaceDetectionService faceDetectionService;
    
    @Autowired
    public AttendanceController(AttendanceService attendanceService, FaceDetectionService faceDetectionService) {
        this.attendanceService = attendanceService;
        this.faceDetectionService = faceDetectionService;
    }
    
    /**
     * 手动打卡
     *
     * @return 打卡结果
     */
    @PostMapping("/punch")
    @ApiOperation("手动打卡")
    public Result punch() {
        try {
            log.info("开始手动打卡");
            
            // 调用人脸检测服务进行打卡
            Map<String, Object> result = faceDetectionService.realTimeFaceDetection();
            
            if (result == null) {
                return Result.error("打卡失败：系统未准备好");
            }
            
            Integer code = (Integer) result.get("code");
            String name = (String) result.get("name");
            
            switch (code) {
                case 1:
                    return Result.success("打卡成功", result);
                case 2:
                    return Result.error("打卡失败：未知人脸");
                case 3:
                    return Result.error("打卡失败：未检测到人脸");
                case 4:
                    return Result.error("打卡失败：系统异常");
                case 5:
                    return Result.error("打卡失败：检测服务异常");
                default:
                    return Result.error("打卡失败：未知错误");
            }
        } catch (Exception e) {
            log.error("手动打卡失败：{}", e.getMessage(), e);
            return Result.error("打卡失败：系统异常");
        }
    }
    
    /**
     * 根据用户ID查询打卡记录
     *
     * @param userId 用户ID
     * @return 打卡记录列表
     */
    @GetMapping("/record/user/{userId}")
    @ApiOperation("根据用户ID查询打卡记录")
    public Result getAttendanceRecordsByUserId(@PathVariable Long userId) {
        try {
            log.info("根据用户ID查询打卡记录：userId={}", userId);
            
            List<AttendanceRecord> records = attendanceService.getAttendanceRecordsByUserId(userId);
            return Result.success(records);
        } catch (Exception e) {
            log.error("根据用户ID查询打卡记录失败：{}", e.getMessage(), e);
            return Result.error("查询失败：系统异常");
        }
    }
    
    /**
     * 根据时间范围查询打卡记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 打卡记录列表
     */
    @GetMapping("/record/range")
    @ApiOperation("根据时间范围查询打卡记录")
    public Result getAttendanceRecordsByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        try {
            log.info("根据时间范围查询打卡记录：startTime={}, endTime={}", startTime, endTime);
            
            List<AttendanceRecord> records = attendanceService.getAttendanceRecordsByTimeRange(startTime, endTime);
            return Result.success(records);
        } catch (Exception e) {
            log.error("根据时间范围查询打卡记录失败：{}", e.getMessage(), e);
            return Result.error("查询失败：系统异常");
        }
    }
    
    /**
     * 获取打卡统计数据
     *
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据
     */
    @GetMapping("/statistics")
    @ApiOperation("获取打卡统计数据")
    public Result getAttendanceStatistics(
            @RequestParam(required = false) Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        try {
            log.info("获取打卡统计数据：userId={}, startTime={}, endTime={}", userId, startTime, endTime);
            
            Map<String, Object> statistics = attendanceService.getAttendanceStatistics(userId, startTime, endTime);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取打卡统计数据失败：{}", e.getMessage(), e);
            return Result.error("查询失败：系统异常");
        }
    }
}