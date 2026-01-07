# 登录服务API文档

## 1. 概述

登录服务提供用户认证、登录、注册、登出等功能。

## 2. API地址

- Swagger UI地址：http://localhost:{port}/swagger-ui.html
- API文档JSON：http://localhost:{port}/v2/api-docs

## 3. 主要功能模块

### 3.1 登录认证

- 用户登录
- 用户注册
- 用户登出
- 刷新Token

## 4. 使用说明

1. 启动登录服务
2. 访问Swagger UI地址查看完整的API文档
3. 使用API测试工具（如Postman）调用API接口
4. 查看控制台日志了解API调用情况

## 5. 注意事项

- 用户登录成功后会返回Token，请妥善保存
- Token有效期为24小时，过期后需要刷新或重新登录
- 登出操作会将Token加入黑名单，使其失效
- 注册时请确保用户名唯一
