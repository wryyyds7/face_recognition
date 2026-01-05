from flask import Flask, request, jsonify
from Enter import deepface_model_verify, deepface_model_find, deepface_model_extract
import os
from config_reader import config

app = Flask(__name__)

# 从共享配置文件读取配置
UPLOAD_FOLDER = config.get('database.temp-path', 'uploads')
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route('/api/verify', methods=['POST'])
def verify():
    """人脸验证接口 - 检测单张图片是否有人脸，并自动进行人脸识别"""
    try:
        # 检查请求类型
        if 'img' in request.files:
            # 处理上传图片
            img = request.files['img']
            # 获取数据库路径，默认使用配置的路径
            db_path = request.form.get('db_path', 'd:/bianchenglianxi/java/project/face_recognition/python_port/img')
            
            # 保存图片到临时目录
            img_path = os.path.join(app.config['UPLOAD_FOLDER'], img.filename)
            img.save(img_path)
            
            try:
                # 1. 检测图片是否包含人脸
                face_objs = deepface_model_extract(img_path)
                
                if not face_objs or len(face_objs) == 0:
                    # 没有检测到人脸，删除临时文件
                    os.remove(img_path)
                    return jsonify({"status": "no_face", "message": "No face detected in the image"}), 400
                
                # 2. 检测到人脸，自动进行人脸识别
                result = deepface_model_find(img_path, db_path)
                
                # 3. 处理识别结果
                if result and len(result) > 0:
                    # 检查是否有匹配的结果
                    match_found = False
                    matched_identity = None
                    
                    for df in result:
                        if not df.empty:
                            # 获取匹配度最高的结果（distance最小）
                            best_match = df.loc[df['distance'].idxmin()]
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
            # 处理图片路径
            data = request.get_json()
            img_path = data.get('img_path')
            # 获取数据库路径，默认使用配置的路径
            db_path = data.get('db_path', 'd:/bianchenglianxi/java/project/face_recognition/python_port/img')
            
            if not img_path:
                return jsonify({"error": "Missing required parameters"}), 400
            
            # 1. 检测图片是否包含人脸
            face_objs = deepface_model_extract(img_path)
            
            if not face_objs or len(face_objs) == 0:
                return jsonify({"status": "no_face", "message": "No face detected in the image"}), 400
            
            # 2. 检测到人脸，自动进行人脸识别
            result = deepface_model_find(img_path, db_path)
            
            # 3. 处理识别结果
            if result and len(result) > 0:
                # 检查是否有匹配的结果
                match_found = False
                matched_identity = None
                
                for df in result:
                    if not df.empty:
                        # 获取匹配度最高的结果（distance最小）
                        best_match = df.loc[df['distance'].idxmin()]
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
    port = config.get('python.port', 5000)
    host = config.get('python.host', '0.0.0.0')
    app.run(debug=True, host=host, port=port)
