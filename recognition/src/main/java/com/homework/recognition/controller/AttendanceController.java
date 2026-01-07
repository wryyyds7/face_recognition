package com.homework.recognition.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.Result;
import com.homework.recognition.config.AttendanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 考勤管理控制器
 *
 * @author homework
 */
@RestController
@RequestMapping("/attendance")
public class AttendanceController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceConfig attendanceConfig;

    @Autowired
    public AttendanceController(AttendanceConfig attendanceConfig) {
        this.attendanceConfig = attendanceConfig;
    }

    /**
     * 获取当前打卡配置
     *
     * @return 当前打卡配置
     */
    @GetMapping("/config")
    public Result getAttendanceConfig() {
        try {
            log.info("获取打卡配置，当前打卡间隔：{}小时", attendanceConfig.getPunchInterval());
            Map<String, Object> config = new HashMap<>();
            config.put("punchInterval", attendanceConfig.getPunchInterval());
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取打卡配置失败：{}", e.getMessage(), e);
            return Result.error("获取打卡配置失败");
        }
    }

    /**
     * 修改打卡时间间隔
     *
     * @param config 包含punchInterval字段的配置对象
     * @return 修改结果
     */
    @PutMapping("/config")
    public Result updateAttendanceConfig(@RequestBody Map<String, Integer> config) {
        try {
            Integer punchInterval = config.get("punchInterval");
            if (punchInterval == null || punchInterval <= 0) {
                return Result.error("打卡时间间隔必须大于0");
            }
            
            attendanceConfig.setPunchInterval(punchInterval);
            log.info("修改打卡配置，新打卡间隔：{}小时", punchInterval);
            return Result.success("打卡配置修改成功");
        } catch (Exception e) {
            log.error("修改打卡配置失败：{}", e.getMessage(), e);
            return Result.error("修改打卡配置失败");
        }
    }
}
