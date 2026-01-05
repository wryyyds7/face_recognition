# 实现人脸识别服务模块

## 1. 需求分析

* 对接 `python_port` 模块的人脸识别服务

* 实现实时人脸检测功能

* 实现用户照片的增删改查功能

* 定期清空检测文件夹

* 代码风格与 `login` 模块一致

## 2. 实现步骤

### 2.1 创建Feign客户端

* 在 `common/src/main/java/com/homework/common/feign/` 目录下创建 `PythonPortClient.java`

* 定义 `verify` 和 `find` 接口方法，对接Python服务的 `/api/verify` 和 `/api/find` 接口

* 支持文件上传和路径调用两种方式

### 2.2 创建人脸服务模块

* 在项目根目录下创建 `recognition` 模块

* 配置模块依赖，包括 `common` 模块和Feign相关依赖

* 创建服务启动类 `RecognitionApplication.java`

### 2.3 实现实时人脸检测功能

* 创建 `FaceDetectionService.java` 接口和实现类

* 实现定时拍照逻辑，使用 `@Scheduled` 注解

* 调用Python服务进行人脸检测和识别

* 处理检测结果，存储相关信息

### 2.4 实现用户照片管理功能

* 创建 `UserFaceService.java` 接口和实现类

* 实现照片的增删改查功能

* 照片命名使用用户名（userName字段）

* 存储绝对路径到User对象的avatar字段或新增的faceImages字段

* 支持批量操作

### 2.5 实现定期清理功能

* 创建 `FaceDataCleanupService.java` 接口和实现类

* 使用 `@Scheduled` 注解实现定期清理

* 清理Python服务的临时文件和检测文件夹

* 确保不影响正在进行的检测和识别过程

### 2.6 创建控制器

* 创建 `FaceRecognitionController.java`

* 定义REST接口，供其他模块调用

* 实现权限控制

### 2.7 配置文件

* 创建 `application.yml`，配置服务端口、Feign客户端等

* 配置定时任务的时间间隔

* 配置照片存储路径和临时文件路径

## 3. 代码风格要求

* 遵循 `login` 模块的代码风格

* 使用构造函数注入依赖

* 添加详细的JavaDoc注释

* 方法命名清晰，参数类型明确

* 异常处理机制完善

* 日志记录完整

## 4. 注意事项

* 不使用模拟数据，直接调用真实的Python服务

* 确保Feign客户端配置正确，能够访问Python服务

* 处理好文件上传和路径调用两种方式

* 确保返回值类型与Python服务的响应格式匹配

* 定期清理功能要考虑Python服务的运行状态，避免影响正在进行的操作

* 照片存储路径使用绝对路径，便于管理

## 5. 预期结果

* 创建符合项目风格的人脸识别服务模块

* 能够成功调用Python服务的人脸验证和人脸识别接口

* 实现实时人脸检测功能

* 实现用户照片的增删改查功能

* 实现定期清理功能，防止存储空间过大

* 代码风格与现有代码一致，易于维护和扩展

