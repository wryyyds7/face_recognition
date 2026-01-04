from flask import Flask, request, jsonify
from Enter import deepface_model_verify, deepface_model_find
import os

app = Flask(__name__)

# 配置上传文件夹
UPLOAD_FOLDER = 'uploads'
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route('/api/verify', methods=['POST'])
def verify():
    """人脸验证接口"""
    try:
        # 检查请求类型
        if 'img1' in request.files and 'img2' in request.files:
            # 处理上传图片
            img1 = request.files['img1']
            img2 = request.files['img2']
            
            # 保存图片到临时目录
            img1_path = os.path.join(app.config['UPLOAD_FOLDER'], img1.filename)
            img2_path = os.path.join(app.config['UPLOAD_FOLDER'], img2.filename)
            img1.save(img1_path)
            img2.save(img2_path)
            
            # 调用人脸验证函数
            result = deepface_model_verify(img1_path, img2_path)
            
            # 删除临时文件
            os.remove(img1_path)
            os.remove(img2_path)
        else:
            # 处理图片路径
            data = request.get_json()
            img1_path = data.get('img1_path')
            img2_path = data.get('img2_path')
            
            if not img1_path or not img2_path:
                return jsonify({"error": "Missing required parameters"}), 400
            
            # 调用人脸验证函数
            result = deepface_model_verify(img1_path, img2_path)
        
        return jsonify(result)
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
            
            # 调用人脸识别函数
            result = deepface_model_find(img_path, db_path)
            
            # 删除临时文件
            os.remove(img_path)
        else:
            # 处理图片路径
            data = request.get_json()
            img_path = data.get('img_path')
            db_path = data.get('db_path')
            
            if not img_path or not db_path:
                return jsonify({"error": "Missing required parameters"}), 400
            
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
    app.run(debug=True, host='0.0.0.0', port=5000)
