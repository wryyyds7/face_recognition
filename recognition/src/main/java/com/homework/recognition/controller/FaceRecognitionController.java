package com.homework.recognition.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.Result;
import com.homework.common.domain.entity.User;
import com.homework.recognition.service.FaceDetectionService;
import com.homework.recognition.service.UserFaceService;
import com.homework.recognition.service.impl.UserFaceServiceImpl;
import com.homework.users.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
    private final UserService userService;

    @Autowired
    public FaceRecognitionController(FaceDetectionService faceDetectionService, UserFaceService userFaceService, UserService userService) {
        this.faceDetectionService = faceDetectionService;
        this.userFaceService = userFaceService;
        this.userService = userService;
    }

    /**
     * 手动触发实时人脸检测
     *
     * @return 检测结果
     */
    @PostMapping("/detect")
    public Result manualDetect() {
        try {
            log.info("手动触发实时人脸检测");
            Map<String, Object> resultMap = faceDetectionService.singleDetect();
            Integer code = (Integer) resultMap.get("code");
            
            switch (code) {
                case 1:
                    String name = (String) resultMap.get("name");
                    // 创建包含姓名的响应数据
                    Map<String, Object> successData = new HashMap<>();
                    successData.put("name", name);
                    return Result.success(successData);
                case 2:
                    return Result.error("未知人员");
                case 3:
                    return Result.error("没有检测到人脸");
                case 4:
                    return Result.error("未知的验证状态");
                case 5:
                    return Result.error("实时人脸检测失败");
                default:
                    return Result.error("未知的验证状态");
            }
        } catch (Exception e) {
            log.error("手动触发人脸检测失败：{}", e.getMessage(), e);
            return Result.error("人脸检测触发失败：" + e.getMessage());
        }
    }

    /**
     * 人脸验证
     * 检测单张图片是否有人脸
     *
     * @param imgPath 待检测图片路径
     * @return 验证结果
     */
    @PostMapping("/verify")
    public Result verifyFace(@RequestParam("imgPath") String imgPath) {
        try {
            log.info("人脸验证，图片路径：{}", imgPath);
            Map<String, Object> verificationResult = faceDetectionService.verifyFace(imgPath);
            
            // 处理验证结果
            String status = (String) verificationResult.get("status");
            if (status != null) {
                switch (status) {
                    case "recognized":
                        String identity = (String) verificationResult.get("identity");
                        String name = null;
                        if (identity != null) {
                            // 提取文件名（去掉路径）
                            java.io.File identityFile = new java.io.File(identity);
                            String baseName = identityFile.getName();
                            
                            // 去掉后缀
                            int lastDotIndex = baseName.lastIndexOf('.');
                            if (lastDotIndex > 0) {
                                name = baseName.substring(0, lastDotIndex);
                            } else {
                                name = baseName;
                            }
                            
                            // 提取纯用户名，去掉_用户ID部分
                            int underscoreIndex = name.indexOf('_');
                            if (underscoreIndex > 0) {
                                name = name.substring(0, underscoreIndex);
                                log.info("提取纯用户名：从{}提取为{}", baseName, name);
                            }
                        }
                        // 创建包含姓名的响应数据
                        Map<String, Object> successData = new HashMap<>();
                        successData.put("name", name);
                        return Result.success(successData);
                    case "unknown_face":
                        return Result.error("未知人员");
                    case "no_face":
                        return Result.error("没有检测到人脸");
                    default:
                        return Result.error("未知的验证状态");
                }
            } else {
                return Result.error("未知的验证状态");
            }
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
    public Result recognizeFace(@RequestParam("imgPath") String imgPath) {
        try {
            log.info("人脸识别，图片路径：{}", imgPath);
            Map<String, Object> recognitionResult = faceDetectionService.recognizeFace(imgPath);
            
            // 处理识别结果
            String status = (String) recognitionResult.get("status");
            if (status != null) {
                switch (status) {
                    case "recognized":
                        String identity = (String) recognitionResult.get("identity");
                        String name = null;
                        if (identity != null) {
                            // 提取文件名（去掉路径）
                            java.io.File identityFile = new java.io.File(identity);
                            String baseName = identityFile.getName();
                            
                            // 去掉后缀
                            int lastDotIndex = baseName.lastIndexOf('.');
                            if (lastDotIndex > 0) {
                                name = baseName.substring(0, lastDotIndex);
                            } else {
                                name = baseName;
                            }
                            
                            // 提取纯用户名，去掉_用户ID部分
                            int underscoreIndex = name.indexOf('_');
                            if (underscoreIndex > 0) {
                                name = name.substring(0, underscoreIndex);
                                log.info("提取纯用户名：从{}提取为{}", baseName, name);
                            }
                        }
                        // 创建包含姓名的响应数据
                        Map<String, Object> successData = new HashMap<>();
                        successData.put("name", name);
                        return Result.success(successData);
                    case "unknown_face":
                        return Result.error("未知人员");
                    case "no_face":
                        return Result.error("没有检测到人脸");
                    default:
                        return Result.error("未知的验证状态");
                }
            } else {
                return Result.error("未知的验证状态");
            }
        } catch (Exception e) {
            log.error("人脸识别失败：{}", e.getMessage(), e);
            return Result.error("人脸识别失败：" + e.getMessage());
        }
    }

    /**
     * 为用户添加人脸照片
     *
     * @param userId  用户ID
     * @param userName 用户名
     * @param faceImg 人脸照片文件
     * @return 更新后的用户对象
     */
    @PostMapping(value = "/user/face/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result addUserFace(@RequestParam("userId") Long userId, 
                             @RequestParam("userName") String userName,
                             @RequestPart("faceImg") MultipartFile faceImg) {
        try {
            // 构建用户对象
            User user = new User();
            user.setUserId(userId);
            user.setUserName(userName);
            
            log.info("为用户添加人脸照片，用户名：{}，用户ID：{}", userName, userId);
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
    @PostMapping(value = "/user/face/batchAdd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result batchAddUserFaces(@ModelAttribute User user, @RequestPart("faceImgs") List<MultipartFile> faceImgs) {
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
    @PostMapping(value = "/user/face/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateUserFace(@ModelAttribute User user, @RequestPart("faceImg") MultipartFile faceImg, @RequestParam("oldImgName") String oldImgName) {
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
     * @param request 包含user和imgName的请求体
     * @return 更新后的用户对象
     */
    @PostMapping("/user/face/delete")
    public Result deleteUserFace(@RequestBody Map<String, Object> request) {
        try {
            // 从请求体中获取user对象和imgName
            Map<String, Object> userMap = (Map<String, Object>) request.get("user");
            String imgName = (String) request.get("imgName");
            
            // 构建User对象
            User user = new User();
            user.setUserId(Long.parseLong(userMap.get("userId").toString()));
            user.setUserName((String) userMap.get("userName"));
            
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
     * 根据用户昵称获取用户的人脸照片路径
     *
     * @param nickName 用户昵称
     * @return 人脸照片路径列表
     */
    @GetMapping("/user/face/list/{nickName}")
    public Result getUserFaceListByNickName(@PathVariable("nickName") String nickName) {
        try {
            log.info("根据用户昵称获取用户人脸照片列表，用户昵称：{}", nickName);
            List<String> faceList = userFaceService.getUserFaceListByUserName(nickName);
            return Result.success(faceList);
        } catch (Exception e) {
            log.error("根据用户昵称获取用户人脸照片列表失败：{}", e.getMessage(), e);
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

    /**
     * 直接拍照并为用户添加人脸照片
     *
     * @param userId 用户ID
     * @return 更新后的用户对象
     */
    @PostMapping("/user/face/takeAndAdd")
    public Result takeAndAddUserFace(@RequestParam("userId") Long userId) {
        try {
            log.info("直接拍照并为用户添加人脸照片，用户ID：{}", userId);
            
            // 1. 拍照并保存
            String photoPath = faceDetectionService.takePhoto();
            log.info("拍照成功，照片路径：{}", photoPath);
            
            // 2. 根据userId查询用户信息，获取完整的User对象
            System.out.println("userId:"+userId);
            User user = userService.findByUserId(userId);
            System.out.println("user:"+user.toString());
            if (user == null) {
                return Result.error("用户不存在，用户ID：" + userId);
            }
            
            // 3. 为用户添加人脸照片
            User updatedUser = userFaceService.addUserFaceByPath(user, photoPath);
            
            return Result.success(updatedUser);
        } catch (Exception e) {
            log.error("直接拍照并为用户添加人脸照片失败：{}", e.getMessage(), e);
            return Result.error("拍照添加人脸照片失败：" + e.getMessage());
        }
    }

    /**
     * 直接拍照并进行人脸识别打卡
     *
     * @return 打卡结果
     */
    @PostMapping("/punch/takePhoto")
    public Result takePhotoAndPunch() {
        try {
            log.info("直接拍照并进行人脸识别打卡");
            
            // 1. 拍照并保存
            String photoPath = faceDetectionService.takePhoto();
            log.info("拍照成功，照片路径：{}", photoPath);
            
            // 2. 进行人脸识别
            Map<String, Object> recognitionResult = faceDetectionService.recognizeFace(photoPath);
            log.info("人脸识别结果：{}", recognitionResult);
            
            // 3. 处理识别结果
            String status = (String) recognitionResult.get("status");
            if (status != null) {
                switch (status) {
                    case "recognized":
                        String identity = (String) recognitionResult.get("identity");
                        String name = null;
                        if (identity != null) {
                            // 提取文件名（去掉路径）
                            java.io.File identityFile = new java.io.File(identity);
                            String baseName = identityFile.getName();
                            
                            // 去掉后缀
                            int lastDotIndex = baseName.lastIndexOf('.');
                            if (lastDotIndex > 0) {
                                name = baseName.substring(0, lastDotIndex);
                            } else {
                                name = baseName;
                            }
                            
                            // 提取纯用户名，去掉_用户ID部分
                            int underscoreIndex = name.indexOf('_');
                            if (underscoreIndex > 0) {
                                name = name.substring(0, underscoreIndex);
                                log.info("提取纯用户名：从{}提取为{}", baseName, name);
                            }
                        }
                        // 创建包含姓名的响应数据
                        Map<String, Object> successData = new HashMap<>();
                        successData.put("name", name);
                        successData.put("message", "打卡成功");
                        return Result.success(successData);
                    case "unknown_face":
                        return Result.error("未知人员，打卡失败");
                    case "no_face":
                        return Result.error("没有检测到人脸，打卡失败");
                    default:
                        return Result.error("未知的验证状态，打卡失败");
                }
            } else {
                return Result.error("未知的验证状态，打卡失败");
            }
        } catch (Exception e) {
            log.error("直接拍照并进行人脸识别打卡失败：{}", e.getMessage(), e);
            return Result.error("拍照打卡失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取人脸照片的二进制数据，用于前端展示
     *
     * @param fileName 照片文件名
     * @return 照片的二进制数据
     */
    @GetMapping("/user/face/photo/{fileName}")
    public void getFacePhoto(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        try {
            log.info("获取人脸照片，文件名：{}", fileName);
            
            // 1. 根据文件名查找照片文件
            // 遍历所有用户目录，查找包含该文件名的文件
            Path dbPath = Paths.get(((UserFaceServiceImpl)userFaceService).getPythonServiceConfig().getDbPath());
            Path photoPath = null;
            
            if (Files.exists(dbPath)) {
                // 遍历所有目录
                for (Path dir : Files.newDirectoryStream(dbPath)) {
                    if (Files.isDirectory(dir)) {
                        Path targetFile = dir.resolve(fileName);
                        if (Files.exists(targetFile)) {
                            photoPath = targetFile;
                            break;
                        }
                    }
                }
            }
            
            if (photoPath == null || !Files.exists(photoPath)) {
                log.error("人脸照片不存在，文件名：{}", fileName);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 2. 设置响应头
            response.setContentType(Files.probeContentType(photoPath));
            response.setHeader("Content-Disposition", "inline; filename=" + fileName);
            
            // 3. 写入响应
            Files.copy(photoPath, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("获取人脸照片失败：{}", e.getMessage(), e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                log.error("设置响应状态失败：{}", ex.getMessage(), ex);
            }
        }
    }
}
