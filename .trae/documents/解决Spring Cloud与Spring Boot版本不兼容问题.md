## 问题分析

从错误信息中可以看到：
```
java.lang.ClassNotFoundException: org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
```

这个错误发生在处理 `org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration` 类时，说明 Spring Cloud 版本与 Spring Boot 版本不兼容。

## 根本原因

1. **版本不兼容**：Spring Cloud 2023.0.0 对应的 Spring Boot 版本是 3.1.x，而项目中 Spring Boot 版本已经升级到了 3.2.1
2. **缺少依赖**：common 模块中没有显式声明 Spring Boot Web 依赖，而 WebMvcAutoConfiguration 是 Web 模块的一部分
3. **自动配置冲突**：LifecycleMvcEndpointAutoConfiguration 依赖 WebMvcAutoConfiguration，但由于版本不兼容导致无法找到该类

## 解决方案

### 1. 升级 Spring Cloud 版本

将 Spring Cloud 版本从 2023.0.0 升级到与 Spring Boot 3.2.1 兼容的版本（如 2023.0.4）

### 2. 调整 common 模块

- **添加 Spring Boot Web 依赖**：确保 common 模块包含 spring-boot-starter-web 依赖
- **修改启动类**：在 CommonApplication 中排除 LifecycleMvcEndpointAutoConfiguration 自动配置

### 3. 调整 recognition 模块

- **确保版本一致性**：确保 recognition 模块使用的 Spring Cloud 版本与 Spring Boot 3.2.1 兼容
- **优化配置**：确保 recognition 模块的配置正确，特别是数据源配置已被禁用

## 实施步骤

1. **修改父 pom.xml**：升级 Spring Cloud 版本到 2023.0.4
2. **修改 common/pom.xml**：添加 spring-boot-starter-web 依赖
3. **修改 CommonApplication.java**：排除 LifecycleMvcEndpointAutoConfiguration 自动配置
4. **验证 recognition 模块**：确保 recognition 模块的配置正确
5. **测试启动**：分别启动 common 和 recognition 模块，验证问题是否解决

## 预期结果

- common 模块能够成功启动，不再报 ClassNotFoundException
- recognition 模块能够成功启动，不再报数据源配置错误
- 两个模块都能正常运行，实现人脸识别功能