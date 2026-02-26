"""
计算机视觉基础教程
面向Java开发者的CV入门指南
"""

import cv2
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import requests
from io import BytesIO

class ComputerVisionBasics:
    """计算机视觉基础概念"""
    
    def __init__(self):
        self.sample_image_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Vd-Orig.png/256px-Vd-Orig.png"
    
    def image_processing_fundamentals(self):
        """图像处理基础"""
        print("=== 图像处理基础 ===")
        
        # 创建示例图像
        img = np.zeros((300, 300, 3), dtype=np.uint8)
        img[100:200, 100:200] = [255, 0, 0]  # 红色矩形
        img[150:250, 150:250] = [0, 255, 0]  # 绿色矩形
        
        # 显示图像信息
        print(f"图像形状: {img.shape}")
        print(f"图像数据类型: {img.dtype}")
        print(f"像素值范围: {img.min()} - {img.max()}")
        
        return img
    
    def image_filters_demo(self, img):
        """图像滤波器演示"""
        print("\n=== 图像滤波器 ===")
        
        # 1. 高斯模糊
        blurred = cv2.GaussianBlur(img, (15, 15), 0)
        
        # 2. 边缘检测
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        edges = cv2.Canny(gray, 50, 150)
        
        # 3. 形态学操作
        kernel = np.ones((5,5), np.uint8)
        dilated = cv2.dilate(edges, kernel, iterations=1)
        
        return blurred, edges, dilated
    
    def feature_detection(self, img):
        """特征检测"""
        print("\n=== 特征检测 ===")
        
        # 转换为灰度图
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        
        # Harris角点检测
        gray_float = np.float32(gray)
        corners = cv2.cornerHarris(gray_float, 2, 3, 0.04)
        corners = cv2.dilate(corners, None)
        
        # 标记角点
        img_corners = img.copy()
        img_corners[corners > 0.01 * corners.max()] = [0, 0, 255]
        
        return img_corners

class ObjectDetectionBasics:
    """目标检测基础"""
    
    def simple_object_detection(self):
        """简单目标检测示例"""
        print("\n=== 简单目标检测 ===")
        
        # 创建包含多个对象的图像
        img = np.zeros((400, 400, 3), dtype=np.uint8)
        
        # 添加不同颜色的圆形（模拟不同对象）
        cv2.circle(img, (100, 100), 30, (255, 0, 0), -1)    # 蓝色圆
        cv2.circle(img, (250, 150), 40, (0, 255, 0), -1)    # 绿色圆
        cv2.circle(img, (180, 300), 35, (0, 0, 255), -1)    # 红色圆
        
        # 转换为HSV色彩空间进行颜色分割
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        
        # 定义蓝色范围
        lower_blue = np.array([100, 50, 50])
        upper_blue = np.array([130, 255, 255])
        blue_mask = cv2.inRange(hsv, lower_blue, upper_blue)
        
        # 查找轮廓
        contours, _ = cv2.findContours(blue_mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        # 在原图上绘制检测结果
        result_img = img.copy()
        for contour in contours:
            # 计算轮廓的边界框
            x, y, w, h = cv2.boundingRect(contour)
            cv2.rectangle(result_img, (x, y), (x+w, y+h), (255, 255, 255), 2)
            cv2.putText(result_img, "Object", (x, y-10), 
                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
        
        print(f"检测到 {len(contours)} 个蓝色对象")
        return result_img

class DeepLearningCV:
    """深度学习计算机视觉"""
    
    def pretrained_model_demo(self):
        """预训练模型演示"""
        print("\n=== 预训练模型演示 ===")
        
        try:
            # 使用OpenCV的DNN模块加载预训练模型
            # 这里使用简化版本，实际应用中可以使用YOLO、ResNet等
            
            print("预训练模型类别:")
            classes = ['person', 'bicycle', 'car', 'motorcycle', 'airplane', 'bus']
            for i, cls in enumerate(classes[:3]):
                print(f"  {i}: {cls}")
            
            # 模拟检测结果
            detections = [
                {"class": "person", "confidence": 0.95, "bbox": [50, 50, 150, 200]},
                {"class": "car", "confidence": 0.87, "bbox": [200, 100, 300, 180]}
            ]
            
            print("\n检测结果:")
            for det in detections:
                print(f"  类别: {det['class']}, 置信度: {det['confidence']:.2f}")
                print(f"  边界框: {det['bbox']}")
                
        except Exception as e:
            print(f"模型加载失败: {e}")
            print("请安装: pip install opencv-python")

class CVApplications:
    """计算机视觉应用"""
    
    def face_detection_demo(self):
        """人脸检测演示"""
        print("\n=== 人脸检测演示 ===")
        
        # 创建示例人脸图像
        face_img = np.zeros((200, 200, 3), dtype=np.uint8)
        # 绘制简化的脸部特征
        cv2.circle(face_img, (100, 80), 60, (135, 206, 235), -1)  # 脸部
        cv2.circle(face_img, (70, 70), 10, (0, 0, 0), -1)         # 左眼
        cv2.circle(face_img, (130, 70), 10, (0, 0, 0), -1)        # 右眼
        cv2.ellipse(face_img, (100, 120), (25, 15), 0, 0, 180, (0, 0, 0), 2)  # 嘴巴
        
        print("人脸检测基本原理:")
        print("1. Haar特征检测")
        print("2. LBP局部二值模式")
        print("3. 深度学习方法(CNN)")
        
        return face_img
    
    def image_classification_pipeline(self):
        """图像分类流水线"""
        print("\n=== 图像分类流水线 ===")
        
        pipeline_steps = [
            "1. 图像采集",
            "2. 预处理(缩放、归一化)",
            "3. 数据增强",
            "4. 特征提取",
            "5. 模型训练",
            "6. 验证和测试",
            "7. 部署推理"
        ]
        
        for step in pipeline_steps:
            print(step)

def display_images(images_dict):
    """显示图像结果"""
    fig, axes = plt.subplots(2, 3, figsize=(15, 10))
    axes = axes.ravel()
    
    for idx, (title, img) in enumerate(images_dict.items()):
        if idx < len(axes):
            # 转换BGR到RGB用于matplotlib显示
            if len(img.shape) == 3:
                img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
            else:
                img_rgb = img
            axes[idx].imshow(img_rgb, cmap='gray' if len(img.shape) == 2 else None)
            axes[idx].set_title(title)
            axes[idx].axis('off')
    
    plt.tight_layout()
    plt.savefig('cv_demonstration.png', dpi=300, bbox_inches='tight')
    plt.show()

def main():
    """主函数"""
    print("👁️ 计算机视觉基础教程")
    print("=" * 50)
    
    # 基础CV操作
    cv_basic = ComputerVisionBasics()
    original_img = cv_basic.image_processing_fundamentals()
    
    # 图像滤波
    blurred, edges, dilated = cv_basic.image_filters_demo(original_img)
    
    # 特征检测
    corner_img = cv_basic.feature_detection(original_img)
    
    # 目标检测
    obj_detection = ObjectDetectionBasics()
    detection_result = obj_detection.simple_object_detection()
    
    # 深度学习CV
    dl_cv = DeepLearningCV()
    dl_cv.pretrained_model_demo()
    
    # CV应用
    applications = CVApplications()
    face_img = applications.face_detection_demo()
    applications.image_classification_pipeline()
    
    # 显示结果（如果环境支持）
    try:
        images_to_show = {
            "原始图像": original_img,
            "高斯模糊": blurred,
            "边缘检测": edges,
            "角点检测": corner_img,
            "目标检测": detection_result,
            "人脸示例": face_img
        }
        display_images(images_to_show)
    except:
        print("图像显示需要matplotlib支持")
    
    print("\n✅ 计算机视觉基础学习完成！")

if __name__ == "__main__":
    main()