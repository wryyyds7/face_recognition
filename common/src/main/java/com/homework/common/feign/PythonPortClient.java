package com.homework.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * PythonPort服务Feign客户端
 * 用于调用Python服务的人脸识别接口
 */
@FeignClient(value = "python-port")
public interface PythonPortClient {

    /**
     * 人脸验证接口 - 通过路径调用
     * 检测单张图片是否有人脸
     *
     * @param request 请求参数，包含img_path
     * @return 验证结果
     */
    @PostMapping(value = "/api/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> verifyByPath(@RequestBody Map<String, String> request);

    /**
     * 人脸验证接口 - 通过文件上传调用
     * 检测单张图片是否有人脸
     *
     * @param img 待检测图片
     * @return 验证结果
     */
    @PostMapping(value = "/api/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> verifyByFile(@RequestPart("img") MultipartFile img);

    /**
     * 人脸识别接口 - 通过路径调用
     *
     * @param request 请求参数，包含img_path和db_path
     * @return 识别结果
     */
    @PostMapping(value = "/api/find", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> findByPath(@RequestBody Map<String, String> request);

    /**
     * 人脸识别接口 - 通过文件上传调用
     *
     * @param img 待识别图片
     * @param dbPath 数据库路径
     * @return 识别结果
     */
    @PostMapping(value = "/api/find", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> findByFile(@RequestPart("img") MultipartFile img, @RequestParam("db_path") String dbPath);

    /**
     * 默认方法，简化人脸验证调用（通过路径）
     * 检测单张图片是否有人脸
     *
     * @param imgPath 待检测图片路径
     * @return 验证结果
     */
    default Map<String, Object> verify(String imgPath) {
        Map<String, String> request = new java.util.HashMap<>();
        request.put("img_path", imgPath);
        return verifyByPath(request);
    }

    /**
     * 默认方法，简化人脸识别调用（通过路径）
     *
     * @param imgPath 待识别图片路径
     * @param dbPath 数据库路径
     * @return 识别结果
     */
    default Map<String, Object> find(String imgPath, String dbPath) {
        Map<String, String> request = new java.util.HashMap<>();
        request.put("img_path", imgPath);
        request.put("db_path", dbPath);
        return findByPath(request);
    }
}