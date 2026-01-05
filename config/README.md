# 人脸识别系统共享配置

## 概述

本目录包含人脸识别系统的共享配置文件，用于统一管理Java和Python服务的配置，实现"一处修改，处处生效"的效果。

## 配置文件结构

```
config/
├── config.yml          # 主配置文件
└── README.md           # 配置说明文档
```

## 配置项说明

### 1. Python服务配置

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| python.port | Python服务端口 | 5000 |
| python.host | Python服务主机 | localhost |
| python.url | Python服务完整URL | http://localhost:5000 |

### 2. 人脸数据库配置

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| database.root-path | 人脸数据库根路径 | d:/bianchenglianxi/java/project/face_recognition/python_port/img |
| database.temp-path | 临时文件路径 | d:/bianchenglianxi/java/project/face_recognition/python_port/uploads |
| database.photo-path | 拍照保存路径 | d:/bianchenglianxi/java/project/face_recognition/python_port/photos |

### 3. 人脸检测配置

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| detection.interval | 检测间隔（毫秒） | 5000 |
| detection.cleanup-interval | 清理间隔（毫秒） | 3600000 |

### 4. 上传文件配置

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| upload.max-file-size | 最大文件大小 | 10MB |
| upload.max-request-size | 最大请求大小 | 10MB |

## Java服务配置读取

Java服务通过`application.yml`文件读取配置，配置项与共享配置文件保持一致。在`application.yml`中，每个配置项都添加了注释，说明其来源。

### 配置示例

```yaml
# Python服务配置
python:
  port:
    # 从共享配置文件读取：config/config.yml -> python.url
    url: http://localhost:5000
    # 人脸数据库路径 - 从共享配置文件读取：config/config.yml -> database.root-path
    db-path: d:/bianchenglianxi/java/project/face_recognition/python_port/img
```

### 配置类

Java服务使用`@ConfigurationProperties`注解将配置映射到Java类中，例如：

```java
@Component
@ConfigurationProperties(prefix = "python.port")
public class PythonServiceConfig {
    private String url;
    private String dbPath;
    private String tempPath;
    private long cleanupInterval;
    // getter和setter方法
}
```

## Python服务配置读取

Python服务通过`config_reader.py`模块读取共享配置文件，该模块提供了一个全局的`config`实例，方便在其他Python文件中使用。

### 使用示例

```python
from config_reader import config

# 读取配置项
port = config.get('python.port', 5000)
host = config.get('python.host', '0.0.0.0')

# 读取嵌套配置项
upload_folder = config.get('database.temp-path', 'uploads')

# 重新加载配置
config.reload()
```

### 配置文件加载流程

1. Python服务启动时，`config_reader.py`会自动加载`config/config.yml`文件
2. 创建全局`config`实例，方便在其他Python文件中使用
3. 可以通过`config.get()`方法读取配置项，支持点号分隔的嵌套路径
4. 可以通过`config.reload()`方法重新加载配置文件

## 配置更新流程

1. 修改`config/config.yml`文件中的配置项
2. 重启Java服务，或者重新加载Spring配置
3. 对于Python服务，可以重启服务，或者调用`config.reload()`方法重新加载配置

## 注意事项

1. 配置文件使用YAML格式，注意缩进和语法
2. 所有路径使用正斜杠`/`，避免使用反斜杠`\`
3. 修改配置文件后，需要重启服务才能生效（或调用`config.reload()`重新加载）
4. 共享配置文件中的配置项优先级高于服务内部的默认配置
5. 确保服务有读取配置文件的权限
