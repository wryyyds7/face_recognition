package com.homework.recognition.service.impl;

import com.homework.common.domain.entity.User;
import com.homework.recognition.config.PythonServiceConfig;
import com.homework.recognition.service.UserFaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用户人脸照片服务实现类
 */
@Service
public class UserFaceServiceImpl implements UserFaceService {

    private static final Logger log = LoggerFactory.getLogger(UserFaceServiceImpl.class);

    private final PythonServiceConfig pythonServiceConfig;

    @Autowired
    public UserFaceServiceImpl(PythonServiceConfig pythonServiceConfig) {
        this.pythonServiceConfig = pythonServiceConfig;
    }

    /**
     * 为用户添加人脸照片
     *
     * @param user    用户对象
     * @param faceImg 人脸照片文件
     * @return 更新后的用户对象
     */
    @Override
    public User addUserFace(User user, MultipartFile faceImg) {
        try {
            log.info("为用户添加人脸照片，用户名：{}", user.getUserName());

            // 1. 确保用户人脸照片目录存在
            Path userFaceDir = getOrCreateUserFaceDir(user.getUserName());

            // 2. 生成文件名，使用用户名+序号
            String fileName = generateFileName(user.getUserName(), faceImg.getOriginalFilename());

            // 3. 保存文件
            Path targetLocation = userFaceDir.resolve(fileName);
            Files.copy(faceImg.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 4. 更新用户的avatar字段或faceImages字段
            updateUserFacePath(user, targetLocation.toString());

            log.info("用户人脸照片添加成功，保存路径：{}", targetLocation.toString());
            return user;
        } catch (IOException e) {
            log.error("为用户添加人脸照片失败，用户名：{}，错误信息：{}", user.getUserName(), e.getMessage(), e);
            throw new RuntimeException("添加人脸照片失败", e);
        }
    }

    /**
     * 批量添加用户人脸照片
     *
     * @param user     用户对象
     * @param faceImgs 人脸照片文件列表
     * @return 更新后的用户对象
     */
    @Override
    public User batchAddUserFaces(User user, List<MultipartFile> faceImgs) {
        try {
            log.info("批量为用户添加人脸照片，用户名：{}，照片数量：{}", user.getUserName(), faceImgs.size());

            // 1. 确保用户人脸照片目录存在
            Path userFaceDir = getOrCreateUserFaceDir(user.getUserName());

            // 2. 保存所有文件
            for (MultipartFile faceImg : faceImgs) {
                String fileName = generateFileName(user.getUserName(), faceImg.getOriginalFilename());
                Path targetLocation = userFaceDir.resolve(fileName);
                Files.copy(faceImg.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("用户人脸照片添加成功，保存路径：{}", targetLocation.toString());
            }

            // 3. 更新用户的faceImages字段
            updateUserFacePaths(user, getUserFaceList(user));

            return user;
        } catch (IOException e) {
            log.error("批量为用户添加人脸照片失败，用户名：{}，错误信息：{}", user.getUserName(), e.getMessage(), e);
            throw new RuntimeException("批量添加人脸照片失败", e);
        }
    }

    /**
     * 更新用户人脸照片
     *
     * @param user    用户对象
     * @param faceImg 新的人脸照片文件
     * @param oldImgName 旧的人脸照片文件名
     * @return 更新后的用户对象
     */
    @Override
    public User updateUserFace(User user, MultipartFile faceImg, String oldImgName) {
        try {
            log.info("更新用户人脸照片，用户名：{}，旧照片文件名：{}", user.getUserName(), oldImgName);

            // 1. 删除旧照片
            deleteUserFace(user, oldImgName);

            // 2. 添加新照片
            return addUserFace(user, faceImg);
        } catch (Exception e) {
            log.error("更新用户人脸照片失败，用户名：{}，错误信息：{}", user.getUserName(), e.getMessage(), e);
            throw new RuntimeException("更新人脸照片失败", e);
        }
    }

    /**
     * 删除用户人脸照片
     *
     * @param user     用户对象
     * @param imgName  要删除的人脸照片文件名
     * @return 更新后的用户对象
     */
    @Override
    public User deleteUserFace(User user, String imgName) {
        try {
            log.info("删除用户人脸照片，用户名：{}，照片文件名：{}", user.getUserName(), imgName);

            // 1. 构建文件路径
            Path userFaceDir = getOrCreateUserFaceDir(user.getUserName());
            Path imgPath = userFaceDir.resolve(imgName);

            // 2. 删除文件
            if (Files.exists(imgPath)) {
                Files.delete(imgPath);
                log.info("用户人脸照片删除成功，文件路径：{}", imgPath.toString());
            } else {
                log.warn("用户人脸照片不存在，无法删除，文件路径：{}", imgPath.toString());
            }

            // 3. 更新用户的faceImages字段
            updateUserFacePaths(user, getUserFaceList(user));

            return user;
        } catch (IOException e) {
            log.error("删除用户人脸照片失败，用户名：{}，错误信息：{}", user.getUserName(), e.getMessage(), e);
            throw new RuntimeException("删除人脸照片失败", e);
        }
    }

    /**
     * 获取用户的人脸照片列表
     *
     * @param user 用户对象
     * @return 人脸照片文件列表
     */
    @Override
    public List<String> getUserFaceList(User user) {
        return getUserFaceListByUserName(user.getUserName());
    }

    /**
     * 根据用户名获取用户的人脸照片路径
     *
     * @param userName 用户名
     * @return 人脸照片路径列表
     */
    @Override
    public List<String> getUserFaceListByUserName(String userName) {
        try {
            log.info("获取用户人脸照片列表，用户名：{}", userName);

            // 1. 构建用户人脸照片目录
            Path userFaceDir = getOrCreateUserFaceDir(userName);

            // 2. 遍历目录，获取所有照片文件
            List<String> faceImgPaths = new ArrayList<>();
            Files.list(userFaceDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png"))
                    .forEach(path -> faceImgPaths.add(path.toString()));

            log.info("获取用户人脸照片列表成功，用户名：{}，照片数量：{}", userName, faceImgPaths.size());
            return faceImgPaths;
        } catch (IOException e) {
            log.error("获取用户人脸照片列表失败，用户名：{}，错误信息：{}", userName, e.getMessage(), e);
            throw new RuntimeException("获取人脸照片列表失败", e);
        }
    }

    /**
     * 清空用户的所有人脸照片
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    @Override
    public User clearUserFaces(User user) {
        try {
            log.info("清空用户所有人脸照片，用户名：{}", user.getUserName());

            // 1. 获取用户人脸照片目录
            Path userFaceDir = getOrCreateUserFaceDir(user.getUserName());

            // 2. 遍历目录，删除所有照片文件
            Files.list(userFaceDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.info("删除用户人脸照片成功，文件路径：{}", path.toString());
                        } catch (IOException e) {
                            log.error("删除用户人脸照片失败，文件路径：{}，错误信息：{}", path.toString(), e.getMessage(), e);
                        }
                    });

            // 3. 更新用户的faceImages字段
            updateUserFacePaths(user, new ArrayList<>());

            log.info("清空用户所有人脸照片成功，用户名：{}", user.getUserName());
            return user;
        } catch (IOException e) {
            log.error("清空用户所有人脸照片失败，用户名：{}，错误信息：{}", user.getUserName(), e.getMessage(), e);
            throw new RuntimeException("清空人脸照片失败", e);
        }
    }

    /**
     * 获取或创建用户人脸照片目录
     *
     * @param userName 用户名
     * @return 用户人脸照片目录路径
     * @throws IOException IO异常
     */
    private Path getOrCreateUserFaceDir(String userName) throws IOException {
        // 人脸数据库路径 + 用户名
        Path userFaceDir = Paths.get(pythonServiceConfig.getDbPath(), userName);
        if (!Files.exists(userFaceDir)) {
            Files.createDirectories(userFaceDir);
            log.info("创建用户人脸照片目录成功，目录路径：{}", userFaceDir.toString());
        }
        return userFaceDir;
    }

    /**
     * 生成文件名，使用用户名+序号
     *
     * @param userName 用户名
     * @param originalFilename 原始文件名
     * @return 生成的文件名
     * @throws IOException IO异常
     */
    private String generateFileName(String userName, String originalFilename) throws IOException {
        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);

        // 获取用户人脸照片目录
        Path userFaceDir = getOrCreateUserFaceDir(userName);

        // 遍历目录，获取当前序号
        int maxSeq = 0;
        for (String imgPath : getUserFaceListByUserName(userName)) {
            String filename = Paths.get(imgPath).getFileName().toString();
            if (filename.startsWith(userName)) {
                try {
                    String seqStr = filename.substring(userName.length(), filename.indexOf("."));
                    int seq = Integer.parseInt(seqStr);
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException e) {
                    // 忽略非数字序号的文件
                    log.warn("文件名格式不符合要求，忽略：{}", filename);
                }
            }
        }

        // 生成新文件名：用户名+序号+扩展名
        return userName + (maxSeq + 1) + "." + extension;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg"; // 默认使用jpg格式
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 更新用户的人脸照片路径
     *
     * @param user      用户对象
     * @param facePath  人脸照片路径
     */
    private void updateUserFacePath(User user, String facePath) {
        // 简单实现，只保存第一张照片路径到avatar字段
        if (user.getAvatar() == null) {
            user.setAvatar(facePath);
        }
        // 这里可以根据需要扩展，比如添加faceImages字段保存多张照片路径
    }

    /**
     * 更新用户的人脸照片路径列表
     *
     * @param user       用户对象
     * @param facePaths  人脸照片路径列表
     */
    private void updateUserFacePaths(User user, List<String> facePaths) {
        // 简单实现，如果没有照片，清空avatar字段
        if (facePaths.isEmpty()) {
            user.setAvatar(null);
        } else {
            // 保存第一张照片路径到avatar字段
            user.setAvatar(facePaths.get(0));
        }
        // 这里可以根据需要扩展，比如添加faceImages字段保存多张照片路径
    }
}