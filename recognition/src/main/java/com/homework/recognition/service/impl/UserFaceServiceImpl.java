package com.homework.recognition.service.impl;

import com.homework.common.domain.entity.User;
import com.homework.common.utils.FileUploadUtils;
import com.homework.common.exception.file.*;
import com.homework.common.utils.MimeTypeUtils;
import com.homework.recognition.config.PythonServiceConfig;
import com.homework.recognition.service.FaceFeatureCacheService;
import com.homework.recognition.service.UserFaceService;
import com.homework.users.service.UserService;
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
    private final FaceFeatureCacheService faceFeatureCacheService;
    private final UserService userService;

    @Autowired
    public UserFaceServiceImpl(PythonServiceConfig pythonServiceConfig, FaceFeatureCacheService faceFeatureCacheService, UserService userService) {
        this.pythonServiceConfig = pythonServiceConfig;
        this.faceFeatureCacheService = faceFeatureCacheService;
        this.userService = userService;
    }
    
    /**
     * 获取PythonServiceConfig对象，用于外部访问dbPath
     * 
     * @return PythonServiceConfig对象
     */
    public PythonServiceConfig getPythonServiceConfig() {
        return pythonServiceConfig;
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
            log.info("为用户添加人脸照片，用户名：{}，用户ID：{}", user.getUserName(), user.getUserId());

            // 1. 文件验证：只允许图片类型，大小限制50M
            FileUploadUtils.assertAllowed(faceImg, MimeTypeUtils.IMAGE_EXTENSION);

            // 2. 确保用户人脸照片目录存在，使用用户名_用户ID作为目录名
            Path userFaceDir = getOrCreateUserFaceDir(user);

            // 3. 生成文件名，使用用户名_用户ID作为文件名
            String fileName = generateFileName(user, faceImg.getOriginalFilename());

            // 4. 保存文件
            Path targetLocation = userFaceDir.resolve(fileName);
            Files.copy(faceImg.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 5. 更新用户的avatar字段或faceImages字段
            updateUserFacePath(user, targetLocation.toString());

            // 6. 删除旧的人脸特征缓存，因为新的照片可能会改变特征向量
            faceFeatureCacheService.deleteFaceFeature(user.getUserId());

            // 7. 将更新后的用户对象保存到数据库
            userService.updateUser(user);

            log.info("用户人脸照片添加成功，保存路径：{}", targetLocation.toString());
            return user;
        } catch (FileSizeLimitExceededException e) {
            log.error("文件大小超过限制，用户名：{}，用户ID：{}，错误信息：{}", user.getUserName(), user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("文件大小超过限制，最大50M", e);
        } catch (InvalidExtensionException e) {
            log.error("文件类型不允许，用户名：{}，用户ID：{}，错误信息：{}", user.getUserName(), user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("文件类型不允许，只支持图片格式", e);
        } catch (IOException e) {
            log.error("为用户添加人脸照片失败，用户名：{}，用户ID：{}，错误信息：{}", user.getUserName(), user.getUserId(), e.getMessage(), e);
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
            log.info("批量为用户添加人脸照片，用户名：{}，用户ID：{}，照片数量：{}", user.getUserName(), user.getUserId(), faceImgs.size());

            // 1. 确保用户人脸照片目录存在，使用用户名_用户ID作为目录名
            Path userFaceDir = getOrCreateUserFaceDir(user);

            // 2. 保存所有文件
            for (MultipartFile faceImg : faceImgs) {
                // 文件验证：只允许图片类型，大小限制50M
                FileUploadUtils.assertAllowed(faceImg, MimeTypeUtils.IMAGE_EXTENSION);
                
                String fileName = generateFileName(user, faceImg.getOriginalFilename());
                Path targetLocation = userFaceDir.resolve(fileName);
                Files.copy(faceImg.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("用户人脸照片添加成功，保存路径：{}", targetLocation.toString());
            }

            // 3. 更新用户的faceImages字段
            updateUserFacePaths(user, getUserFaceList(user));

            // 4. 删除旧的人脸特征缓存，因为新的照片可能会改变特征向量
            faceFeatureCacheService.deleteFaceFeature(user.getUserId());

            return user;
        } catch (FileSizeLimitExceededException e) {
            log.error("文件大小超过限制，用户名：{}，用户ID：{}，错误信息：{}", user.getUserName(), user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("文件大小超过限制，最大50M", e);
        } catch (InvalidExtensionException e) {
            log.error("文件类型不允许，用户名：{}，用户ID：{}，错误信息：{}", user.getUserName(), user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("文件类型不允许，只支持图片格式", e);
        } catch (IOException e) {
            log.error("批量为用户添加人脸照片失败，用户名：{}，用户ID：{}，错误信息：{}", user.getUserName(), user.getUserId(), e.getMessage(), e);
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
            User updatedUser = addUserFace(user, faceImg);
            // 3. 返回更新后的用户对象
            return updatedUser;
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
            Path userFaceDir = getOrCreateUserFaceDir(user);
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

            // 4. 删除旧的人脸特征缓存，因为照片变更可能会改变特征向量
            faceFeatureCacheService.deleteFaceFeature(user.getUserId());

            // 5. 将更新后的用户对象保存到数据库
            userService.updateUser(user);

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
        try {
            log.info("获取用户人脸照片列表，用户名：{}", user.getUserName());

            // 1. 构建用户人脸照片目录
            Path userFaceDir = getOrCreateUserFaceDir(user);

            // 2. 遍历目录，获取所有照片文件
            List<String> faceImgPaths = new ArrayList<>();
            Files.list(userFaceDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png"))
                    .forEach(path -> {
                        // 返回文件名而不是完整路径，让前端可以通过API访问
                        faceImgPaths.add(path.getFileName().toString());
                    });

            log.info("获取用户人脸照片列表成功，用户名：{}，照片数量：{}", user.getUserName(), faceImgPaths.size());
            return faceImgPaths;
        } catch (IOException e) {
            log.error("获取用户人脸照片列表失败，用户名：{}，错误信息：{}", user.getUserName(), e.getMessage(), e);
            throw new RuntimeException("获取人脸照片列表失败", e);
        }
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
            log.info("根据用户名获取用户人脸照片列表，用户名：{}", userName);

            // 注意：这个方法是为了兼容旧的调用方式
            // 新的实现应该使用带User对象的getUserFaceList方法
            // 这里我们需要获取所有包含userName的目录，然后返回所有照片
            List<String> faceImgPaths = new ArrayList<>();
            Path dbPath = Paths.get(pythonServiceConfig.getDbPath());
            
            if (Files.exists(dbPath)) {
                Files.list(dbPath)
                        .filter(Files::isDirectory)
                        .filter(dir -> dir.getFileName().toString().startsWith(userName + "_"))
                        .forEach(dir -> {
                            try {
                                Files.list(dir)
                                        .filter(Files::isRegularFile)
                                        .filter(path -> path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png"))
                                        .forEach(path -> {
                                            // 返回文件名而不是完整路径，让前端可以通过API访问
                                            faceImgPaths.add(path.getFileName().toString());
                                        });
                            } catch (IOException e) {
                                log.error("遍历用户人脸照片目录失败，目录：{}，错误信息：{}", dir.toString(), e.getMessage(), e);
                            }
                        });
            }

            log.info("根据用户名获取用户人脸照片列表成功，用户名：{}，照片数量：{}", userName, faceImgPaths.size());
            return faceImgPaths;
        } catch (IOException e) {
            log.error("根据用户名获取用户人脸照片列表失败，用户名：{}，错误信息：{}", userName, e.getMessage(), e);
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
            Path userFaceDir = getOrCreateUserFaceDir(user);

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

            // 4. 删除人脸特征缓存
            faceFeatureCacheService.deleteFaceFeature(user.getUserId());

            // 5. 将更新后的用户对象保存到数据库
            userService.updateUser(user);

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
     * @param user 用户对象
     * @return 用户人脸照片目录路径
     * @throws IOException IO异常
     */
    private Path getOrCreateUserFaceDir(User user) throws IOException {
        // 人脸数据库路径 + 用户名_用户ID
        String dirName = user.getUserName() + "_" + user.getUserId();
        Path userFaceDir = Paths.get(pythonServiceConfig.getDbPath(), dirName);
        if (!Files.exists(userFaceDir)) {
            Files.createDirectories(userFaceDir);
            log.info("创建用户人脸照片目录成功，目录路径：{}", userFaceDir.toString());
        }
        return userFaceDir;
    }

    /**
     * 生成文件名，使用用户名+用户ID作为文件名
     *
     * @param user 用户对象
     * @param originalFilename 原始文件名
     * @return 生成的文件名
     * @throws IOException IO异常
     */
    private String generateFileName(User user, String originalFilename) throws IOException {
        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);
        
        // 生成文件名：用户名_用户ID+扩展名，确保唯一性
        return user.getUserName() + "_" + user.getUserId() + "." + extension;
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
        // 保存照片路径到avatarPath字段，而不是avatar字段
        user.setAvatarPath(facePath);
        // TODO:这里可以根据需要扩展，比如添加faceImages字段保存多张照片路径
    }

    /**
     * 更新用户的人脸照片路径列表
     *
     * @param user       用户对象
     * @param facePaths  人脸照片路径列表
     */
    private void updateUserFacePaths(User user, List<String> facePaths) {
        // 简单实现，如果没有照片，清空avatarPath字段
        if (facePaths.isEmpty()) {
            user.setAvatarPath(null);
        } else {
            // 保存第一张照片路径到avatarPath字段
            user.setAvatarPath(facePaths.get(0));
        }
        // 这里可以根据需要扩展，比如添加faceImages字段保存多张照片路径
    }

    /**
     * 通过照片路径为用户添加人脸照片
     *
     * @param user     用户对象
     * @param photoPath 照片路径
     * @return 更新后的用户对象
     */
    @Override
    public User addUserFaceByPath(User user, String photoPath) {
        try {
            log.info("通过照片路径为用户添加人脸照片，用户名：{}，用户ID：{}，照片路径：{}", 
                    user.getUserName(), user.getUserId(), photoPath);

            // 1. 验证照片文件是否存在
            Path sourcePath = Paths.get(photoPath);
            if (!Files.exists(sourcePath)) {
                throw new RuntimeException("照片文件不存在：" + photoPath);
            }

            // 2. 确保用户人脸照片目录存在，使用用户名_用户ID作为目录名
            Path userFaceDir = getOrCreateUserFaceDir(user);

            // 3. 生成文件名，使用用户名_用户ID作为文件名
            String fileName = generateFileName(user, sourcePath.getFileName().toString());

            // 4. 保存文件（从源路径复制到用户人脸目录）
            Path targetLocation = userFaceDir.resolve(fileName);
            Files.copy(sourcePath, targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 5. 更新用户的avatar字段或faceImages字段
            updateUserFacePath(user, targetLocation.toString());

            // 6. 删除旧的人脸特征缓存，因为新的照片可能会改变特征向量
            faceFeatureCacheService.deleteFaceFeature(user.getUserId());

            // 7. 将更新后的用户对象保存到数据库
            userService.updateUser(user);

            // 8. 清理临时文件
            Files.deleteIfExists(sourcePath);
            log.info("临时照片文件已清理：{}", sourcePath.toString());

            log.info("用户人脸照片添加成功，保存路径：{}", targetLocation.toString());
            return user;
        } catch (IOException e) {
            log.error("通过照片路径为用户添加人脸照片失败，用户名：{}，照片路径：{}，错误信息：{}", 
                    user.getUserName(), photoPath, e.getMessage(), e);
            throw new RuntimeException("添加人脸照片失败：" + e.getMessage(), e);
        }
    }
}