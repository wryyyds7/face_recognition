package com.homework.recognition.service;

import com.homework.common.domain.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户人脸照片服务接口
 */
public interface UserFaceService {

    /**
     * 为用户添加人脸照片
     *
     * @param user    用户对象
     * @param faceImg 人脸照片文件
     * @return 更新后的用户对象
     */
    User addUserFace(User user, MultipartFile faceImg);

    /**
     * 批量添加用户人脸照片
     *
     * @param user     用户对象
     * @param faceImgs 人脸照片文件列表
     * @return 更新后的用户对象
     */
    User batchAddUserFaces(User user, List<MultipartFile> faceImgs);

    /**
     * 更新用户人脸照片
     *
     * @param user    用户对象
     * @param faceImg 新的人脸照片文件
     * @param oldImgName 旧的人脸照片文件名
     * @return 更新后的用户对象
     */
    User updateUserFace(User user, MultipartFile faceImg, String oldImgName);

    /**
     * 删除用户人脸照片
     *
     * @param user     用户对象
     * @param imgName  要删除的人脸照片文件名
     * @return 更新后的用户对象
     */
    User deleteUserFace(User user, String imgName);

    /**
     * 获取用户的人脸照片列表
     *
     * @param user 用户对象
     * @return 人脸照片文件列表
     */
    List<String> getUserFaceList(User user);

    /**
     * 根据用户名获取用户的人脸照片路径
     *
     * @param userName 用户名
     * @return 人脸照片路径列表
     */
    List<String> getUserFaceListByUserName(String userName);

    /**
     * 清空用户的所有人脸照片
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User clearUserFaces(User user);
}