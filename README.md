# 人脸识别系统API文档

## 1. 系统概述

人脸识别系统是一个集人脸识别、语音合成、用户管理、考勤打卡等功能于一体的综合系统。

## 2. 服务模块列表

| 模块名称 | 功能描述 | 服务端口 | 文档地址 |
|---------|---------|---------|---------|
| 登录服务 | 用户认证、登录、注册、登出 | 随机 | http://localhost:{port}/swagger-ui.html |
| 用户服务 | 用户管理、投诉管理 | 随机 | http://localhost:{port}/swagger-ui.html |
| 人脸识别服务 | 人脸检测、识别、考勤打卡 | 随机 | http://localhost:{port}/swagger-ui.html |
| 语音合成服务 | 文本转语音 | 随机 | http://localhost:{port}/swagger-ui.html |
| 公共服务 | 通用工具和功能 | - | - |
| 网关服务 | 服务路由、负载均衡 | 8080 | - |

## 3. 快速开始

### 3.1 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Nacos 2.2.0+

### 3.2 启动步骤

1. 启动Nacos服务
2. 初始化数据库
3. 启动各个服务模块
4. 访问网关地址：http://localhost:8080
5. 访问各模块的Swagger UI地址查看API文档

## 4. API文档使用指南

### 4.1 Swagger UI

每个服务模块都提供了Swagger UI界面，用于查看和测试API接口：

1. 启动服务后，访问对应的Swagger UI地址
2. 在Swagger UI界面中可以查看所有API接口的详细信息
3. 可以直接在界面中测试API接口，填写参数并发送请求
4. 查看API响应结果和状态码

### 4.2 API文档JSON

每个服务模块都提供了API文档的JSON格式，可用于导入到其他API管理工具：

```
http://localhost:{port}/v2/api-docs
```

## 5. 模块详细文档

### 5.1 登录服务

- **功能**：用户认证、登录、注册、登出
- **主要接口**：
  - POST /in/login - 用户登录
  - POST /in/register - 用户注册
  - POST /in/logout - 用户登出
  - POST /in/refreshToken - 刷新Token

### 5.2 用户服务

- **功能**：用户管理、投诉管理
- **主要接口**：
  - 管理类接口（需要ADMIN角色）
  - 投诉类接口（需要USER角色）

### 5.3 人脸识别服务

- **功能**：人脸检测、识别、考勤打卡
- **主要接口**：
  - POST /recognition/detect - 手动触发实时人脸检测
  - POST /recognition/attendance/punch - 手动打卡
  - POST /recognition/detection/start - 开启自动检测
  - POST /recognition/detection/stop - 停止自动检测

### 5.4 语音合成服务

- **功能**：文本转语音
- **主要接口**：
  - POST /voice/speak - 语音合成并播放
  - POST /voice/speak/voice - 语音合成并播放（指定音色）
  - POST /voice/speak/full - 语音合成并播放（指定音量和语速）

## 6. 认证与授权

- 系统使用JWT Token进行认证
- 登录成功后获取Token，后续请求需要在Header中携带Token
- Token格式：`Bearer {token}`
- 不同接口需要不同的角色权限

## 7. 开发与部署

### 7.1 开发环境

1. 克隆代码到本地
2. 配置开发环境
3. 启动各服务模块
4. 访问Swagger UI进行测试

### 7.2 部署环境

1. 打包各服务模块：`mvn clean package`
2. 部署到服务器
3. 配置Nacos服务发现
4. 启动各服务

## 8. 注意事项

- 调用API前请确保已获取有效的Token
- 人脸照片上传时请确保照片质量良好
- 自动检测功能会消耗较多系统资源，请合理使用
- 文本转语音时文本长度不宜过长
- 所有API请求都需要进行参数校验

## 9. 联系方式

- 项目负责人：王荏宇
- 技术支持：wry
- 反馈邮箱：18975333709@163.com
