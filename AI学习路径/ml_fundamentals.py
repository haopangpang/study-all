"""
机器学习基础概念和算法实现
适合Java开发者理解的ML核心概念
"""

import numpy as np
import matplotlib.pyplot as plt
from sklearn.datasets import make_classification
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, confusion_matrix
import seaborn as sns

class MachineLearningBasics:
    """机器学习基础概念演示"""
    
    def __init__(self):
        self.X_train = None
        self.X_test = None
        self.y_train = None
        self.y_test = None
    
    def generate_sample_data(self):
        """生成示例数据集"""
        # 创建二分类数据集
        X, y = make_classification(
            n_samples=1000,
            n_features=2,
            n_redundant=0,
            n_informative=2,
            n_clusters_per_class=1,
            random_state=42
        )
        
        # 分割训练集和测试集
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(
            X, y, test_size=0.3, random_state=42
        )
        
        return self.X_train, self.y_train
    
    def linear_regression_explanation(self):
        """线性回归原理说明"""
        print("=== 线性回归原理 ===")
        print("目标：找到最佳拟合直线 y = wx + b")
        print("损失函数：均方误差 MSE = Σ(yi - (wx + b))²/n")
        print("优化方法：梯度下降法")
        
        # 简单实现
        class SimpleLinearRegression:
            def __init__(self, learning_rate=0.01, n_iterations=1000):
                self.learning_rate = learning_rate
                self.n_iterations = n_iterations
                self.weight = 0
                self.bias = 0
            
            def fit(self, X, y):
                n_samples = len(X)
                # 梯度下降
                for _ in range(self.n_iterations):
                    y_predicted = self.weight * X + self.bias
                    
                    # 计算梯度
                    dw = (1/n_samples) * np.sum(X * (y_predicted - y))
                    db = (1/n_samples) * np.sum(y_predicted - y)
                    
                    # 更新参数
                    self.weight -= self.learning_rate * dw
                    self.bias -= self.learning_rate * db
            
            def predict(self, X):
                return self.weight * X + self.bias
        
        return SimpleLinearRegression()
    
    def logistic_regression_demo(self):
        """逻辑回归演示（分类算法）"""
        print("\n=== 逻辑回归演示 ===")
        
        # 使用sklearn实现
        model = LogisticRegression(random_state=42)
        model.fit(self.X_train, self.y_train)
        
        # 预测
        y_pred = model.predict(self.X_test)
        accuracy = accuracy_score(self.y_test, y_pred)
        
        print(f"模型准确率: {accuracy:.4f}")
        
        # 可视化决策边界
        self.visualize_decision_boundary(model)
        
        return model, accuracy
    
    def visualize_decision_boundary(self, model):
        """可视化决策边界"""
        plt.figure(figsize=(10, 8))
        
        # 绘制数据点
        scatter = plt.scatter(self.X_test[:, 0], self.X_test[:, 1], 
                            c=self.y_test, cmap='viridis', alpha=0.7)
        
        # 创建网格点
        h = 0.02
        x_min, x_max = self.X_test[:, 0].min() - 1, self.X_test[:, 0].max() + 1
        y_min, y_max = self.X_test[:, 1].min() - 1, self.X_test[:, 1].max() + 1
        xx, yy = np.meshgrid(np.arange(x_min, x_max, h),
                            np.arange(y_min, y_max, h))
        
        # 预测网格点
        Z = model.predict(np.c_[xx.ravel(), yy.ravel()])
        Z = Z.reshape(xx.shape)
        
        # 绘制决策边界
        plt.contour(xx, yy, Z, levels=[0.5], colors='red', linewidths=2)
        plt.colorbar(scatter)
        plt.xlabel('特征1')
        plt.ylabel('特征2')
        plt.title('逻辑回归决策边界')
        plt.savefig('decision_boundary.png', dpi=300, bbox_inches='tight')
        plt.show()
    
    def supervised_vs_unsupervised(self):
        """监督学习vs无监督学习"""
        print("\n=== 学习方式对比 ===")
        
        supervised_methods = {
            "监督学习": [
                "线性回归 - 预测连续值",
                "逻辑回归 - 二分类问题",
                "决策树 - 分类和回归",
                "随机森林 - 集成学习",
                "支持向量机 - 分类和回归",
                "神经网络 - 复杂模式识别"
            ]
        }
        
        unsupervised_methods = {
            "无监督学习": [
                "K-means聚类 - 数据分组",
                "层次聚类 - 树状分组",
                "主成分分析(PCA) - 降维",
                "关联规则 - 购物篮分析",
                "自编码器 - 特征学习"
            ]
        }
        
        print("监督学习应用场景：")
        for method in supervised_methods["监督学习"]:
            print(f"  • {method}")
            
        print("\n无监督学习应用场景：")
        for method in unsupervised_methods["无监督学习"]:
            print(f"  • {method}")

class MLWorkflowDemo:
    """机器学习工作流程演示"""
    
    def __init__(self):
        self.steps = [
            "1. 问题定义和数据收集",
            "2. 数据预处理和清洗",
            "3. 探索性数据分析(EDA)",
            "4. 特征工程",
            "5. 模型选择和训练",
            "6. 模型评估和验证",
            "7. 模型部署和监控"
        ]
    
    def show_workflow(self):
        """展示完整的ML工作流程"""
        print("\n=== 机器学习标准工作流程 ===")
        for step in self.steps:
            print(step)
    
    def preprocessing_example(self):
        """数据预处理示例"""
        print("\n=== 数据预处理示例 ===")
        
        # 模拟原始数据
        raw_data = np.array([
            [1.2, 3.4, np.nan],
            [2.1, np.nan, 5.6],
            [np.nan, 4.5, 6.7],
            [3.2, 5.6, 7.8]
        ])
        
        print("原始数据:")
        print(raw_data)
        
        # 处理缺失值
        from sklearn.impute import SimpleImputer
        imputer = SimpleImputer(strategy='mean')
        clean_data = imputer.fit_transform(raw_data)
        
        print("\n处理后的数据:")
        print(clean_data)
        
        # 特征缩放
        from sklearn.preprocessing import StandardScaler
        scaler = StandardScaler()
        scaled_data = scaler.fit_transform(clean_data)
        
        print("\n标准化后的数据:")
        print(scaled_data)
        
        return scaled_data

def main():
    """主函数演示"""
    print("🤖 机器学习基础概念演示")
    print("=" * 50)
    
    # 初始化
    ml_basics = MachineLearningBasics()
    
    # 生成数据
    print("正在生成示例数据...")
    X_train, y_train = ml_basics.generate_sample_data()
    print(f"训练数据形状: {X_train.shape}")
    
    # 线性回归演示
    lr_model = ml_basics.linear_regression_explanation()
    
    # 逻辑回归演示
    log_model, accuracy = ml_basics.logistic_regression_demo()
    
    # 学习方式对比
    ml_basics.supervised_vs_unsupervised()
    
    # 工作流程演示
    workflow = MLWorkflowDemo()
    workflow.show_workflow()
    workflow.preprocessing_example()
    
    print("\n✅ 机器学习基础概念演示完成！")

if __name__ == "__main__":
    main()