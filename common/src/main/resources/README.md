# 公共服务API文档

## 1. 概述

公共服务提供通用的工具和功能，如文件上传等。

## 2. API地址

- Swagger UI地址：http://localhost:{port}/swagger-ui.html
- API文档JSON：http://localhost:{port}/v2/api-docs

## 3. 主要功能模块

### 3.1 文件上传

- 通用上传请求（单个）
- 通用上传请求（多个）

## 4. 使用说明

1. 启动服务
2. 访问Swagger UI地址查看完整的API文档
3. 使用API测试工具（如Postman）调用API接口
4. 查看控制台日志了解API调用情况

## 5. 注意事项

- 调用API前请确保已获取有效的Token
- 上传文件大小有限制，请根据配置调整
- 支持的文件格式请参考服务配置
- 上传成功后会返回文件访问URL
