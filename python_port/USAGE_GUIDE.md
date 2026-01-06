 DeepFace 人脸识别项目 - 从零开始使用指南

## 项目介绍

DeepFace 是一个基于 Python 的开源人脸识别项目，提供了多种人脸识别功能，包括人脸验证、人脸识别、人脸属性分析、人脸检测和对齐、人脸特征提取以及人脸实时分析等。

## 目录结构

```
deepface-master/
├── Enter.py          # 主程序文件，包含所有功能函数
├── README.md         # 详细的项目说明文档
├── res/              # 存放结果图片
├── video/            # 存放视频文件
└── USAGE_GUIDE.md    # 本使用指南
```

## 准备工作

### 1. 安装 Python

项目对 Python 版本有要求：`3.8 < version < 3.13`，推荐使用 Python 3.11。

- 下载地址：[Python 3.11.8](https://www.python.org/ftp/python/3.11.8/python-3.11.8-amd64.exe)
- 安装时请勾选 "Add Python to PATH"

### 2. 安装依赖库

打开命令行工具（CMD 或 PowerShell），执行以下命令安装所需依赖：

```bash
# 安装 deepface 库（使用清华镜像加速）
pip install deepface -i https://pypi.tuna.tsinghua.edu.cn/simple

# 安装辅助库
pip install matplotlib pillow opencv-python numpy -i https://pypi.tuna.tsinghua.edu.cn/simple
```

### 3. 准备测试数据

- 准备一些人脸图片（用于测试各种功能）
- 准备一个视频文件（用于测试实时分析功能）

建议创建一个 `img` 文件夹，存放测试图片。

## 功能使用

### 1. 基本功能介绍

项目提供了以下核心功能：

| 功能 | 描述 | 函数名 |
|------|------|--------|
| 人脸验证 | 比较两张图片是否为同一个人 | `deepface_model_verify` |
| 人脸识别 | 在数据库中查找与输入图片相似的人脸 | `deepface_model_find` |
| 人脸属性分析 | 分析年龄、性别、表情、种族 | `deepface_model_analyze` |
| 人脸检测和对齐 | 检测图片中的人脸并进行对齐 | `deepface_model_extract` |
| 人脸特征提取 | 提取人脸的特征向量 | `deepface_model_represent` |
| 人脸实时分析 | 对视频流进行实时人脸识别和分析 | `deepface_model_stream` |

### 2. 运行方式

#### 方式一：直接修改 Enter.py 运行

1. 打开 `Enter.py` 文件
2. 找到 `if __name__ == '__main__':` 部分
3. 取消注释需要测试的功能代码，并修改参数
4. 保存文件并运行：

```bash
python Enter.py
```

#### 方式二：创建新的 Python 脚本调用功能

创建一个新的 Python 文件（例如 `test.py`），导入 Enter.py 中的功能函数并调用：

```python
from Enter import *

# 示例：人脸验证
result = deepface_model_verify('img/1-1.jpg', 'img/1-2.jpg')
printf(result, "人脸验证结果")
```

## 功能详细说明

### 1. 人脸验证

**功能**：比较两张图片是否为同一个人

**函数**：`deepface_model_verify(img1, img2, model_name=models_name[1])`

**参数**：
- `img1`：第一张图片路径
- `img2`：第二张图片路径
- `model_name`：使用的模型名称（默认使用 Facenet）

**返回值**：字典，包含验证结果、距离、阈值等信息

**示例**：

```python
result = deepface_model_verify('img/1-1.jpg', 'img/1-2.jpg')
printf(result, "人脸验证结果")
```

### 2. 人脸识别

**功能**：在数据库中查找与输入图片相似的人脸

**函数**：`deepface_model_find(img, db, model_name=models_name[1])`

**参数**：
- `img`：输入图片路径
- `db`：数据库文件夹路径
- `model_name`：使用的模型名称（默认使用 Facenet）

**返回值**：列表，包含找到的相似人脸信息

**示例**：

```python
result = deepface_model_find('img/1-1.jpg', 'img')
printf(result, "人脸识别结果")
```

### 3. 人脸属性分析

**功能**：分析人脸的年龄、性别、表情、种族

**函数**：`deepface_model_analyze(img)`

**参数**：
- `img`：输入图片路径

**返回值**：列表，包含人脸属性分析结果

**示例**：

```python
result = deepface_model_analyze('img/3-3.jpg')
printf(result, "人脸属性分析结果")
picture_frame('img/3-3.jpg', result)  # 显示带框的人脸和表情
```

### 4. 人脸检测和对齐

**功能**：检测图片中的人脸并进行对齐

**函数**：`deepface_model_extract(img)`

**参数**：
- `img`：输入图片路径

**返回值**：列表，包含检测到的人脸信息和对齐后的人脸图像

**示例**：

```python
result = deepface_model_extract('img/2-2.jpg')
printf(result, "人脸检测结果")
```

### 5. 人脸特征提取

**功能**：提取人脸的特征向量

**函数**：`deepface_model_represent(img)`

**参数**：
- `img`：输入图片路径

**返回值**：列表，包含人脸特征向量和人脸区域信息

**示例**：

```python
result = deepface_model_represent('img/2-3.jpg')
printf(result, "人脸特征提取结果")
```

### 6. 人脸实时分析

**功能**：对视频流进行实时人脸识别和分析

**函数**：`deepface_model_stream(db, video)`

**参数**：
- `db`：数据库文件夹路径
- `video`：视频文件路径或摄像头索引（0 表示默认摄像头）

**返回值**：无

**示例**：

```python
# 使用视频文件
deepface_model_stream('img', 'video/3.mp4')

# 使用摄像头
deepface_model_stream('img', 0)
```

## 模型说明

项目支持多种人脸识别模型，包括：

1. VGG-Face
2. Facenet
3. Facenet512
4. OpenFace
5. DeepFace
6. DeepID
7. ArcFace
8. Dlib
9. SFace
10. Ensemble

根据项目 README 中的测试结果，推荐使用以下模型：
- Facenet512（实测分数：98.4%）
- Facenet（实测分数：97.4%）
- Dlib（实测分数：96.8%）
- VGG-Face（实测分数：96.7%）
- ArcFace（实测分数：96.7%）

## 常见问题

### 1. 模型下载慢怎么办？

- 可以手动下载模型文件：[Releases](https://github.com/serengil/deepface/releases)
- 下载后将模型文件放入 `C:\Users\你的用户文件夹\.deepface\weights` 中
- 或修改 `folder_utils.py` 的 `get_deepface_home` 函数自定义路径

### 2. 运行时出现人脸检测错误？

- 确保图片中有人脸
- 确保图片分辨率足够高
- 可以尝试调整 `detector_backend` 参数（可选值：'opencv'、'retinaface'、'mtcnn' 等）

### 3. 实时分析功能无法打开摄像头？

- 确保摄像头已连接且未被其他程序占用
- 尝试修改 `source` 参数为其他摄像头索引（如 1、2 等）

## 示例代码

以下是一个完整的示例脚本，演示了如何使用项目中的各种功能：

```python
from Enter import *

# 初始化
init()

# 1. 人脸验证
print("=== 人脸验证 ===")
verify_result = deepface_model_verify('img/1-1.jpg', 'img/1-2.jpg')
printf(verify_result, "人脸验证结果")

# 2. 人脸识别
print("=== 人脸识别 ===")
find_result = deepface_model_find('img/1-1.jpg', 'img')
printf(find_result, "人脸识别结果")

# 3. 人脸属性分析
print("=== 人脸属性分析 ===")
analyze_result = deepface_model_analyze('img/3-3.jpg')
printf(analyze_result, "人脸属性分析结果")
picture_frame('img/3-3.jpg', analyze_result)

# 4. 人脸检测和对齐
print("=== 人脸检测和对齐 ===")
extract_result = deepface_model_extract('img/2-2.jpg')
printf(extract_result, "人脸检测结果")

# 5. 人脸特征提取
print("=== 人脸特征提取 ===")
represent_result = deepface_model_represent('img/2-3.jpg')
printf(represent_result, "人脸特征提取结果")

# 6. 人脸实时分析（注释掉，需要视频文件或摄像头）
# print("=== 人脸实时分析 ===")
# deepface_model_stream('img', 'video/3.mp4')
```

## 总结

本项目提供了全面的人脸识别功能，使用简单，支持多种模型和功能。通过本指南，您可以从零开始快速上手使用 DeepFace 项目。

如果您在使用过程中遇到问题，可以参考项目的 [GitHub 仓库](https://github.com/serengil/deepface) 或查看 `README.md` 文件获取更多信息。
