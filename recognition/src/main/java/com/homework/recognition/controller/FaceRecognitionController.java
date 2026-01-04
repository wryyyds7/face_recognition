package com.homework.recognition.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.Result;
import com.homework.common.domain.entity.User;
import com.homework.recognition.service.FaceDetectionService;
import com.homework.recognition.service.UserFaceService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 人脸识别控制器
 */
@RestController
@RequestMapping("/recognition")
public class FaceRecognitionController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(FaceRecognitionController.class);

    private final FaceDetectionService faceDetectionService;
    private final UserFaceService userFaceService;

    @Autowired
    public FaceRecognitionController(FaceDetectionService faceDetectionService, UserFaceService userFaceService) {
        this.faceDetectionService = faceDetectionService;
        this.userFaceService = userFaceService;
    }

    /**
     * 手动触发实时人脸检测
     *
     * @return 检测结果
     */
    @PostMapping("/detect")
    @ApiOperation("手动触发实时人脸检测")
    public Result manualDetect() {
        try {
            log.info("手动触发实时人脸检测");
            faceDetectionService.realTimeFaceDetection();
            return Result.success("人脸检测已触发");
        } catch (Exception e) {
            log.error("手动触发人脸检测失败：{}", e.getMessage(), e);
            return Result.error("人脸检测触发失败：" + e.getMessage());
        }
    }

    /**
     * 人脸验证
     *
     * @param img1Path 第一张图片路径
     * @param img2Path 第二张图片路径
     * @return 验证结果
     */
    @PostMapping("/verify")
    @ApiOperation("人脸验证")
    public Result verifyFace(@RequestParam("img1Path") String img1Path, @RequestParam("img2Path") String img2Path) {
        try {
            log.info("人脸验证，图片1：{}，图片2：{}", img1Path, img2Path);
            Map<String, Object> result = faceDetectionService.verifyFace(img1Path, img2Path);
            return Result.success(result);
        } catch (Exception e) {
            log.error("人脸验证失败：{}", e.getMessage(), e);
            return Result.error("人脸验证失败：" + e.getMessage());
        }
    }

    /**
     * 人脸识别
     *
     * @param imgPath 待识别图片路径
     * @return 识别结果
     */
    @PostMapping("/recognize")
    @ApiOperation("人脸识别")
    public Result recognizeFace(@RequestParam("imgPath") String imgPath) {
        try {
            log.info("人脸识别，图片路径：{}", imgPath);
            Map<String, Object> result = faceDetectionService.recognizeFace(imgPath);
            return Result.success(result);
        } catch (Exception e) {
            log.error("人脸识别失败：{}", e.getMessage(), e);
            return Result.error("人脸识别失败：" + e.getMessage());
        }
    }

    /**
     * 为用户添加人脸照片
     *
     * @param user    用户对象
     * @param faceImg 人脸照片文件
     * @return 更新后的用户对象
     */
    @PostMapping("/user/face/add")
    @ApiOperation("为用户添加人脸照片")
    public Result addUserFace(@RequestBody User user, @RequestPart("faceImg") MultipartFile faceImg) {
        try {
            log.info("为用户添加人脸照片，用户名：{}", user.getUserName());
            User updatedUser = userFaceService.addUserFace(user, faceImg);
            return Result.success(updatedUser);
        } catch (Exception e) {
            log.error("为用户添加人脸照片失败：{}", e.getMessage(), e);
            return Result.error("添加人脸照片失败：" + e.getMessage());
        }
    }

    /**
     * 批量为用户添加人脸照片
     *
     * @param user     用户对象
     * @param faceImgs 人脸照片文件列表
     * @return 更新后的用户对象
     */
    @PostMapping("/user/face/batchAdd")
    @ApiOperation("批量为用户添加人脸照片")
    public Result batchAddUserFaces(@RequestBody User user, @RequestPart("faceImgs") List<MultipartFile> faceImgs) {
        try {
            log.info("批量为用户添加人脸照片，用户名：{}，照片数量：{}", user.getUserName(), faceImgs.size());
            User updatedUser = userFaceService.batchAddUserFaces(user, faceImgs);
            return Result.success(updatedUser);
        } catch (Exception e) {
            log.error("批量为用户添加人脸照片失败：{}", e.getMessage(), e);
            return Result.error("批量添加人脸照片失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户人脸照片
     *
     * @param user       用户对象
     * @param faceImg    新的人脸照片文件
     * @param oldImgName 旧的人脸照片文件名
     * @return 更新后的用户对象
     */
    @PostMapping("/user/face/update")
    @ApiOperation("更新用户人脸照片")
    public Result updateUserFace(@RequestBody User user, @RequestPart("faceImg") MultipartFile faceImg, @RequestParam("oldImgName") String oldImgName) {
        try {
            log.info("更新用户人脸照片，用户名：{}，旧照片文件名：{}", user.getUserName(), oldImgName);
            User updatedUser = userFaceService.updateUserFace(user, faceImg, oldImgName);
            return Result.success(updatedUser);
        } catch (Exception e) {
            log.error("更新用户人脸照片失败：{}", e.getMessage(), e);
            return Result.error("更新人脸照片失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户人脸照片
     *
     * @param user     用户对象
     * @param imgName  要删除的人脸照片文件名
     * @return 更新后的用户对象
     */
    @PostMapping("/user/face/delete")
    @ApiOperation("删除用户人脸照片")
    public Result deleteUserFace(@RequestBody User user, @RequestParam("imgName") String imgName) {
        try {
            log.info("删除用户人脸照片，用户名：{}，照片文件名：{}", user.getUserName(), imgName);
            User updatedUser = userFaceService.deleteUserFace(user, imgName);
            return Result.success(updatedUser);
        } catch (Exception e) {
            log.error("删除用户人脸照片失败：{}", e.getMessage(), e);
            return Result.error("删除人脸照片失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的人脸照片列表
     *
     * @param user 用户对象
     * @return 人脸照片文件列表
     */
    @PostMapping("/user/face/list")
    @ApiOperation("获取用户的人脸照片列表")
    public Result getUserFaceList(@RequestBody User user) {
        try {
            log.info("获取用户人脸照片列表，用户名：{}", user.getUserName());
            List<String> faceList = userFaceService.getUserFaceList(user);
            return Result.success(faceList);
        } catch (Exception e) {
            log.error("获取用户人脸照片列表失败：{}", e.getMessage(), e);
            return Result.error("获取人脸照片列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户的人脸照片路径
     *
     * @param userName 用户名
     * @return 人脸照片路径列表
     */
    @GetMapping("/user/face/list/{userName}")
    @ApiOperation("根据用户名获取用户的人脸照片列表")
    public Result getUserFaceListByUserName(@PathVariable("userName") String userName) {
        try {
            log.info("根据用户名获取用户人脸照片列表，用户名：{}", userName);
            List<String> faceList = userFaceService.getUserFaceListByUserName(userName);
            return Result.success(faceList);
        } catch (Exception e) {
            log.error("根据用户名获取用户人脸照片列表失败：{}", e.getMessage(), e);
            return Result.error("获取人脸照片列表失败：" + e.getMessage());
        }
    }

    /**
     * 清空用户的所有人脸照片
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    @PostMapping("/user/face/clear")
    @ApiOperation("清空用户的所有人脸照片")
    public Result clearUserFaces(@RequestBody User user) {
        try {
            log.info("清空用户所有人脸照片，用户名：{}", user.getUserName());
            User updatedUser = userFaceService.clearUserFaces(user);
            return Result.success(updatedUser);
        } catch (Exception e) {
            log.error("清空用户所有人脸照片失败：{}", e.getMessage(), e);
            return Result.error("清空人脸照片失败：" + e.getMessage());
        }
    }
}
