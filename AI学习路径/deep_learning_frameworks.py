"""
深度学习框架对比：TensorFlow vs PyTorch
针对Java开发者设计的深度学习入门
"""

import torch
import torch.nn as nn
import torch.optim as optim
import tensorflow as tf
from tensorflow import keras
import numpy as np
import matplotlib.pyplot as plt

class NeuralNetworkBasics:
    """神经网络基础概念"""
    
    def __init__(self):
        self.input_size = 784  # 28x28图像
        self.hidden_size = 128
        self.num_classes = 10
    
    def pytorch_neural_network(self):
        """PyTorch神经网络实现"""
        print("=== PyTorch神经网络 ===")
        
        class SimpleNN(nn.Module):
            def __init__(self, input_size, hidden_size, num_classes):
                super(SimpleNN, self).__init__()
                # 定义网络层（类似Java中的类继承）
                self.fc1 = nn.Linear(input_size, hidden_size)
                self.relu = nn.ReLU()
                self.fc2 = nn.Linear(hidden_size, num_classes)
                
            def forward(self, x):
                # 前向传播
                out = self.fc1(x)
                out = self.relu(out)
                out = self.fc2(out)
                return out
        
        # 创建模型实例
        model = SimpleNN(self.input_size, self.hidden_size, self.num_classes)
        print(f"PyTorch模型结构: {model}")
        
        return model
    
    def tensorflow_neural_network(self):
        """TensorFlow/Keras神经网络实现"""
        print("\n=== TensorFlow/Keras神经网络 ===")
        
        # Sequential API（最简单的方式）
        model = keras.Sequential([
            keras.layers.Dense(self.hidden_size, activation='relu', 
                             input_shape=(self.input_size,)),
            keras.layers.Dense(self.num_classes, activation='softmax')
        ])
        
        # 编译模型
        model.compile(
            optimizer='adam',
            loss='sparse_categorical_crossentropy',
            metrics=['accuracy']
        )
        
        print("TensorFlow模型结构:")
        model.summary()
        
        return model

class FrameworkComparison:
    """框架对比分析"""
    
    def __init__(self):
        self.comparison_points = {
            "PyTorch": {
                "优势": [
                    "动态计算图（更直观）",
                    "Pythonic设计（对Python开发者友好）",
                    "研究首选",
                    "调试更容易",
                    "社区活跃"
                ],
                "适用场景": [
                    "学术研究",
                    "原型开发",
                    "需要动态网络结构",
                    "研究新算法"
                ]
            },
            "TensorFlow": {
                "优势": [
                    "静态计算图（性能更好）",
                    "生产环境部署成熟",
                    "Google支持",
                    "移动端部署完善",
                    "生态系统丰富"
                ],
                "适用场景": [
                    "生产环境部署",
                    "大规模应用",
                    "移动端应用",
                    "企业级解决方案"
                ]
            }
        }
    
    def show_comparison(self):
        """显示框架对比"""
        print("\n=== 深度学习框架对比 ===")
        
        for framework, details in self.comparison_points.items():
            print(f"\n{framework}:")
            print("  优势:")
            for advantage in details["优势"]:
                print(f"    • {advantage}")
            print("  适用场景:")
            for scenario in details["适用场景"]:
                print(f"    • {scenario}")

class PracticalExamples:
    """实用示例代码"""
    
    def image_classification_pytorch(self):
        """PyTorch图像分类示例"""
        print("\n=== PyTorch图像分类示例 ===")
        
        # 简化的CNN网络
        class CNN(nn.Module):
            def __init__(self):
                super(CNN, self).__init__()
                self.conv1 = nn.Conv2d(1, 32, kernel_size=3)
                self.pool = nn.MaxPool2d(2, 2)
                self.conv2 = nn.Conv2d(32, 64, kernel_size=3)
                self.fc1 = nn.Linear(64 * 5 * 5, 128)
                self.fc2 = nn.Linear(128, 10)
                self.relu = nn.ReLU()
                
            def forward(self, x):
                x = self.pool(self.relu(self.conv1(x)))
                x = self.pool(self.relu(self.conv2(x)))
                x = x.view(-1, 64 * 5 * 5)  # 展平
                x = self.relu(self.fc1(x))
                x = self.fc2(x)
                return x
        
        model = CNN()
        print("CNN模型创建完成")
        return model
    
    def text_classification_tensorflow(self):
        """TensorFlow文本分类示例"""
        print("\n=== TensorFlow文本分类示例 ===")
        
        # 文本分类模型
        model = keras.Sequential([
            keras.layers.Embedding(input_dim=10000, output_dim=128),
            keras.layers.LSTM(64, dropout=0.2),
            keras.layers.Dense(32, activation='relu'),
            keras.layers.Dense(1, activation='sigmoid')
        ])
        
        model.compile(
            optimizer='adam',
            loss='binary_crossentropy',
            metrics=['accuracy']
        )
        
        print("文本分类模型创建完成")
        return model

def training_workflow_demo():
    """训练工作流演示"""
    print("\n=== 深度学习训练工作流 ===")
    
    workflow_steps = [
        "1. 数据准备和预处理",
        "2. 模型架构设计",
        "3. 损失函数选择",
        "4. 优化器配置",
        "5. 训练循环",
        "6. 验证和测试",
        "7. 模型保存和部署"
    ]
    
    for step in workflow_steps:
        print(step)

def main():
    """主函数"""
    print("딥층학습 프레임워크 비교 및 실습")
    print("=" * 60)
    
    # 基础神经网络
    nn_basics = NeuralNetworkBasics()
    pytorch_model = nn_basics.pytorch_neural_network()
    tf_model = nn_basics.tensorflow_neural_network()
    
    # 框架对比
    comparison = FrameworkComparison()
    comparison.show_comparison()
    
    # 实用示例
    examples = PracticalExamples()
    cnn_model = examples.image_classification_pytorch()
    text_model = examples.text_classification_tensorflow()
    
    # 训练流程
    training_workflow_demo()
    
    print("\n✅ 深度学习框架学习完成！")

if __name__ == "__main__":
    main()