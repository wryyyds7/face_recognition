from flask import Flask, request, jsonify
from Enter import deepface_model_find, deepface_model_extract
import os
import time
import requests
import pandas as pd
from threading import Thread
from config_reader import config

app = Flask(__name__)

# 从共享配置文件读取配置
UPLOAD_FOLDER = config.get('database.temp-path', 'uploads')
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# Nacos配置
NACOS_SERVER = '127.0.0.1:8848'  # Nacos服务器地址
SERVICE_NAME = 'python-port'  # 服务名称
GROUP_NAME = 'DEFAULT_GROUP'  # 服务分组
CLUSTER_NAME = 'DEFAULT'  # 集群名称

# 获取Python服务配置
PYTHON_CONFIG_PORT = config.get('python.port', 5000)  # 从配置读取端口，0表示随机端口
PYTHON_HOST = config.get('python.host', 'localhost')

# Nacos API URL
NACOS_API_URL = f'http://{NACOS_SERVER}/nacos/v1/ns'

# 实际使用的端口（初始化为配置值，启动后会更新为实际端口）
actual_port = PYTHON_CONFIG_PORT


# 服务注册函数
def register_service(real_port):
    """使用HTTP API将Python服务注册到Nacos"""
    global actual_port
    actual_port = real_port  # 更新实际使用的端口

    try:
        # 准备注册参数
        register_url = f'{NACOS_API_URL}/instance'
        params = {
            'serviceName': SERVICE_NAME,
            'groupName': GROUP_NAME,
            'ip': PYTHON_HOST,
            'port': actual_port,
            'clusterName': CLUSTER_NAME,
            'weight': 1.0,
            'enable': True,
            'healthy': True
        }

        # 发送注册请求
        response = requests.post(register_url, params=params)
        if response.status_code == 200 and response.text == 'ok':
            print(f"✅ 成功将服务 {SERVICE_NAME} 注册到Nacos: {PYTHON_HOST}:{actual_port}")

            # 启动心跳检测
            def heartbeat():
                heartbeat_url = f'{NACOS_API_URL}/instance/beat'
                while True:
                    try:
                        beat_params = {
                            'serviceName': SERVICE_NAME,
                            'groupName': GROUP_NAME,
                            'ip': PYTHON_HOST,
                            'port': actual_port,
                            'clusterName': CLUSTER_NAME
                        }
                        beat_response = requests.put(heartbeat_url, params=beat_params)
                        if beat_response.status_code == 200:
                            pass  # 心跳发送成功，不需要打印
                        time.sleep(5)  # 每5秒发送一次心跳
                    except Exception as e:
                        print(f"❤️ 心跳发送失败: {e}")
                        time.sleep(1)  # 失败后1秒重试

            heartbeat_thread = Thread(target=heartbeat, daemon=True)
            heartbeat_thread.start()
            print(f"❤️ 心跳检测已启动")
        else:
            print(f"❌ 服务注册失败: {response.status_code} - {response.text}")

    except Exception as e:
        print(f"❌ 服务注册失败: {e}")


# 服务注销函数
def deregister_service():
    """使用HTTP API从Nacos注销Python服务"""
    try:
        deregister_url = f'{NACOS_API_URL}/instance'
        params = {
            'serviceName': SERVICE_NAME,
            'groupName': GROUP_NAME,
            'ip': PYTHON_HOST,
            'port': actual_port,
            'clusterName': CLUSTER_NAME
        }
        response = requests.delete(deregister_url, params=params)
        if response.status_code == 200 and response.text == 'ok':
            print(f"✅ 成功从Nacos注销服务 {SERVICE_NAME}: {PYTHON_HOST}:{actual_port}")
        else:
            print(f"❌ 服务注销失败: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"❌ 服务注销失败: {e}")


@app.route('/api/verify', methods=['POST'])
def verify():
    """人脸验证接口 - 检测单张图片是否有人脸，并自动进行人脸识别"""
    print("进入人脸验证接口")
    try:
        # 检查请求类型
        if 'img' in request.files:
            # 处理上传图片
            img = request.files['img']
            # 获取数据库路径，默认使用配置的路径
            db_path = request.form.get('db_path', config.get('database.root-path',
                                                             'd:/bianchenglianxi/java/project/face_recognition/python_port/img'))

            # 保存图片到临时目录
            img_path = os.path.join(app.config['UPLOAD_FOLDER'], img.filename)
            img.save(img_path)
            print("报存照片成功")
            try:
                # 1. 检测图片是否包含人脸
                face_objs = deepface_model_extract(img_path)
                print("search if the photo has people")
                if not face_objs or len(face_objs) == 0:
                    # 没有检测到人脸，删除临时文件
                    os.remove(img_path)
                    return jsonify({"status": "no_face", "message": "No face detected in the image"}), 400
                print("search if the photo has people——检测成功")
                # 2. 检测到人脸，自动进行人脸识别
                result = deepface_model_find(img_path, db_path)

                # 添加检查代码：查看db_path目录下的所有文件
                import glob

                all_files = glob.glob(os.path.join(db_path, "**", "*.*"), recursive=True)
                print(f"🔍 扫描数据库目录: {db_path}")
                print(f"📁 共扫描到 {len(all_files)} 个文件")
                # 过滤出图像文件（可选，只查看实际的图像文件）
                image_extensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp']
                image_files = [f for f in all_files if os.path.splitext(f)[1].lower() in image_extensions]
                print(f"🖼️  图像文件数量: {len(image_files)}")
                print(f"📄 文件: {all_files[:]}")

                result = deepface_model_find(img_path, db_path)
                # 3. 处理识别结果
                if result and len(result) > 0:
                    # 检查是否有匹配的结果
                    match_found = False
                    matched_identity = None

                    for df in result:
                        if not df.empty:
                            # 验证DataFrame结构
                            if all(col in df.columns for col in ['distance', 'threshold', 'identity']):
                                # 确保数据结构正确
                                if len(df) > 0:
                                    # 使用更安全的方式获取最小距离的结果
                                    min_distance_idx = df['distance'].idxmin()
                                    if pd.notna(min_distance_idx):
                                        best_match = df.loc[min_distance_idx]
                                        if best_match['distance'] <= best_match['threshold']:
                                            match_found = True
                                            matched_identity = best_match['identity']
                                            break

                    if match_found:
                        # 识别成功：返回匹配的文件名
                        return jsonify({
                            "status": "recognized",
                            "message": "Face recognized",
                            "identity": matched_identity,
                            "face_count": len(face_objs)
                        })
                    else:
                        # 存在人脸但不在名单中
                        return jsonify({
                            "status": "unknown_face",
                            "message": "Face detected but not in database",
                            "face_count": len(face_objs)
                        })
                else:
                    # 识别失败：存在人脸但不在名单中
                    return jsonify({
                        "status": "unknown_face",
                        "message": "Face detected but not in database",
                        "face_count": len(face_objs)
                    })

            finally:
                # 删除临时文件
                os.remove(img_path)
        else:
            print("没有上传图片")
            # 处理图片路径
            data = request.get_json()
            img_path = data.get('img_path')
            # img_path = "D:\\bianchenglianxi\java\project\\face_recognition\python_port\photos\success\\20260106201914079_wry.jpg"
            # 获取数据库路径，默认使用配置的路径
            db_path = data.get('db_path', config.get('database.root-path',
                                                     'd:/bianchenglianxi/java/project/face_recognition/python_port/img'))
            print("处理图片路径")
            if not img_path:
                return jsonify({"error": "Missing required parameters"}), 400
            print("处理图片路径——成功")
            # 1. 检测图片是否包含人脸
            face_objs = deepface_model_extract(img_path)
            print("search if the photo has people——检测成功")
            if not face_objs or len(face_objs) == 0:
                print("没有检测到人脸")
                return jsonify({"status": "no_face", "message": "No face detected in the image"}), 400

            # 2. 检测到人脸，自动进行人脸识别
            try:
                import glob

                all_files = glob.glob(os.path.join(db_path, "**", "*.*"), recursive=True)
                print(f"🔍 扫描数据库目录: {db_path}")
                print(f"📁 共扫描到 {len(all_files)} 个文件")
                # 过滤出图像文件（可选，只查看实际的图像文件）
                image_extensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp']
                image_files = [f for f in all_files if os.path.splitext(f)[1].lower() in image_extensions]
                print(f"🖼️  图像文件数量: {len(image_files)}")
                print(f"📄 文件: {all_files[:]}")
                result = deepface_model_find(img_path, db_path)

                #检测的 之后删除
                print("🔍 DeepFace.find result:", result)
                for i, df in enumerate(result):
                    print(f"  DF {i}: shape={df.shape}, columns={list(df.columns) if df is not None else 'None'}")
                    if df is not None and not df.empty:
                        print(f"  Sample row:\n{df.iloc[0]}")
                # 3. 处理识别结果
                if result and len(result) > 0:
                    # 检查是否有匹配的结果
                    match_found = False
                    matched_identity = None

                    for df in result:
                        if not df.empty:
                            # 验证DataFrame结构
                            if all(col in df.columns for col in ['distance', 'threshold', 'identity']):
                                # 确保数据结构正确
                                if len(df) > 0:
                                    # 使用更安全的方式获取最小距离的结果
                                    min_distance_idx = df['distance'].idxmin()
                                    if pd.notna(min_distance_idx):
                                        best_match = df.loc[min_distance_idx]
                                        if best_match['distance'] <= best_match['threshold']:
                                            match_found = True
                                            matched_identity = best_match['identity']
                                            break

                    if match_found:
                        # 识别成功：返回匹配的文件名
                        return jsonify({
                            "status": "recognized",
                            "message": "Face recognized",
                            "identity": matched_identity,
                            "face_count": len(face_objs)
                        })
                    else:
                        # 存在人脸但不在名单中
                        return jsonify({
                            "status": "unknown_face",
                            "message": "Face detected but not in database",
                            "face_count": len(face_objs)
                        })
                else:
                    # 识别失败：存在人脸但不在名单中
                    return jsonify({
                        "status": "unknown_face",
                        "message": "Face detected but not in database",
                        "face_count": len(face_objs)
                    })
            except Exception as e:
                print(f"❌ 人脸识别失败: {e}")
                import traceback
                traceback.print_exc()
                # 识别失败时返回友好的错误信息，而不是500错误
                return jsonify({
                    "status": "unknown_face",
                    "message": "Face detected but recognition failed",
                    "face_count": len(face_objs)
                })


    except Exception as e:
        print(f"❌ 人脸识别失败: {e}")
        import traceback
        traceback.print_exc()
        return jsonify({"error": str(e)}), 500


@app.route('/api/find', methods=['POST'])
def find():
    """人脸识别接口"""
    try:
        # 检查请求类型
        if 'img' in request.files:
            # 处理上传图片
            img = request.files['img']
            db_path = request.form.get('db_path')

            if not db_path:
                return jsonify({"error": "Missing db_path parameter"}), 400

            # 保存图片到临时目录
            img_path = os.path.join(app.config['UPLOAD_FOLDER'], img.filename)
            img.save(img_path)

            try:
                # 检测图片是否包含人脸
                face_objs = deepface_model_extract(img_path)

                if not face_objs or len(face_objs) == 0:
                    # 没有检测到人脸，删除临时文件
                    return jsonify({"error": "No face detected in the image"}), 400

                # 调用人脸识别函数
                result = deepface_model_find(img_path, db_path)

                # 格式化结果
                formatted_result = {
                    "results": []
                }
                for df in result:
                    if not df.empty and all(col in df.columns for col in ['distance', 'threshold', 'identity']):
                        for _, row in df.iterrows():
                            formatted_result["results"].append({
                                "identity": row['identity'],
                                "distance": row['distance'],
                                "threshold": row['threshold']
                            })

                return jsonify(formatted_result)
            finally:
                # 删除临时文件
                os.remove(img_path)
        else:
            # 处理图片路径
            data = request.get_json()
            img_path = data.get('img_path')
            db_path = data.get('db_path')

            if not img_path or not db_path:
                return jsonify({"error": "Missing required parameters"}), 400

            # 检测图片是否包含人脸
            face_objs = deepface_model_extract(img_path)

            if not face_objs or len(face_objs) == 0:
                return jsonify({"error": "No face detected in the image"}), 400

            # 调用人脸识别函数
            result = deepface_model_find(img_path, db_path)

        # 格式化结果
        formatted_result = {
            "results": []
        }
        for df in result:
            if not df.empty and all(col in df.columns for col in ['distance', 'threshold', 'identity']):
                for _, row in df.iterrows():
                    formatted_result["results"].append({
                        "identity": row['identity'],
                        "distance": row['distance'],
                        "threshold": row['threshold']
                    })

        return jsonify(formatted_result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    # 使用Flask的默认方式启动服务器，并通过装饰器获取实际端口
    def get_real_port():
        """获取Flask应用的实际端口"""
        import socket

        # 创建一个临时socket来获取可用端口
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((PYTHON_HOST, 0))  # 绑定到0端口，会随机分配
        _, real_port = s.getsockname()
        s.close()
        return real_port


    try:
        # 获取实际使用的端口（如果配置为0，会随机分配）
        real_port = PYTHON_CONFIG_PORT if PYTHON_CONFIG_PORT != 0 else get_real_port()

        # 注册服务到Nacos（使用实际端口）
        register_service(real_port)

        # 启动Flask应用
        print(f"🚀 Flask应用启动中... {PYTHON_HOST}:{real_port}")

        # 配置Flask，关闭自动重载或调整监听目录
        # 方案1：关闭调试模式（生产环境推荐）
        app.run(debug=False, host=PYTHON_HOST, port=real_port)

        # 方案2：开启调试模式但禁用自动重载（开发环境推荐）
        # app.run(debug=True, host=PYTHON_HOST, port=real_port, use_reloader=False)

        # 方案3：使用自定义的文件监视规则
        # from werkzeug.debug import DebuggedApplication
        # from werkzeug.serving import run_simple
        # run_simple(
        #     PYTHON_HOST, real_port,
        #     DebuggedApplication(app, True),
        #     use_reloader=True,
        #     reloader_options={
        #         'interval': 3,
        #         'exclude_patterns': ['*.pyc', '__pycache__', '*.pyo', '.git', 'env', 'venv', 'uploads']
        #     }
        # )
    except KeyboardInterrupt:
        print("\n👋 应用正在关闭...")
    finally:
        # 从Nacos注销服务
        deregister_service()
