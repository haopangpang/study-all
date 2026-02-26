"""
Python AI服务API
为Java应用提供AI能力接口
"""

from flask import Flask, request, jsonify
from transformers import pipeline
import torch
import logging

app = Flask(__name__)

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class AIServiceManager:
    """AI服务管理器"""
    
    def __init__(self):
        self.services = {}
        self.initialize_services()
    
    def initialize_services(self):
        """初始化AI服务"""
        try:
            # 文本分类服务
            self.services['text_classifier'] = pipeline(
                "sentiment-analysis",
                model="uer/roberta-base-finetuned-chinanews-chinese"
            )
            logger.info("文本分类服务初始化成功")
            
            # 聊天机器人服务
            self.services['chatbot'] = SimpleChatbot()
            logger.info("聊天机器人服务初始化成功")
            
        except Exception as e:
            logger.error(f"服务初始化失败: {e}")

class SimpleChatbot:
    """简单聊天机器人"""
    
    def __init__(self):
        self.responses = {
            "你好": "你好！我是AI助手，有什么可以帮助你的吗？",
            "天气": "我无法获取实时天气信息，建议查看天气预报应用。",
            "时间": "我是一个AI助手，没有实时时间功能。",
            "默认": "我理解你的问题，但我需要更多上下文来给出准确回答。"
        }
    
    def get_response(self, message):
        """获取回复"""
        # 关键词匹配
        for keyword, response in self.responses.items():
            if keyword in message:
                return response, 0.8
        
        # 默认回复
        return self.responses["默认"], 0.3

# 全局服务实例
ai_manager = AIServiceManager()

@app.route('/api/health', methods=['GET'])
def health_check():
    """健康检查接口"""
    return jsonify({
        "status": "healthy",
        "services": list(ai_manager.services.keys())
    })

@app.route('/api/classify', methods=['POST'])
def text_classification():
    """文本分类API"""
    try:
        data = request.get_json()
        text = data.get('text', '')
        
        if not text:
            return jsonify({"error": "缺少文本内容"}), 400
        
        # 使用预训练模型进行分类
        classifier = ai_manager.services.get('text_classifier')
        if classifier:
            result = classifier(text)[0]
            classification = result['label']
            confidence = result['score']
        else:
            # 降级处理
            classification, confidence = fallback_classification(text)
        
        logger.info(f"文本分类: '{text}' -> {classification} ({confidence:.4f})")
        
        return jsonify({
            "classification": classification,
            "confidence": confidence,
            "text": text
        })
        
    except Exception as e:
        logger.error(f"分类错误: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/chat', methods=['POST'])
def chat_endpoint():
    """聊天API"""
    try:
        data = request.get_json()
        message = data.get('message', '')
        
        if not message:
            return jsonify({"error": "缺少消息内容"}), 400
        
        # 获取聊天机器人回复
        chatbot = ai_manager.services.get('chatbot')
        reply, confidence = chatbot.get_response(message)
        
        logger.info(f"聊天交互: '{message}' -> '{reply}'")
        
        return jsonify({
            "reply": reply,
            "confidence": confidence,
            "original_message": message
        })
        
    except Exception as e:
        logger.error(f"聊天错误: {e}")
        return jsonify({"error": str(e)}), 500

def fallback_classification(text):
    """降级分类方法"""
    positive_keywords = ['好', '棒', '喜欢', '优秀', '满意']
    negative_keywords = ['坏', '差', '讨厌', '糟糕', '不满']
    
    for keyword in positive_keywords:
        if keyword in text:
            return 'positive', 0.7
    
    for keyword in negative_keywords:
        if keyword in text:
            return 'negative', 0.7
    
    return 'neutral', 0.5

@app.route('/api/image/process', methods=['POST'])
def image_processing():
    """图像处理API"""
    try:
        # 这里应该是实际的图像处理逻辑
        # 为了演示，返回模拟结果
        return jsonify({
            "objects_detected": 3,
            "processing_time": 150,
            "success": True
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

def start_server():
    """启动服务器"""
    print("🚀 启动Python AI服务...")
    print("服务地址: http://localhost:5000")
    print("可用接口:")
    print("  GET  /api/health     - 健康检查")
    print("  POST /api/classify   - 文本分类")
    print("  POST /api/chat       - 聊天机器人")
    print("  POST /api/image/process - 图像处理")
    
    app.run(host='0.0.0.0', port=5000, debug=False)

if __name__ == '__main__':
    start_server()