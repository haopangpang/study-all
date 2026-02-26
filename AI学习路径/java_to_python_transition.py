"""
Java开发者Python学习对照表
展示相同概念在两种语言中的实现差异
"""

# 1. 基本语法对比
class JavaToPythonComparison:
    def __init__(self):
        self.concepts = {}
    
    # 对象创建
    def object_creation(self):
        """对象创建对比"""
        # Java风格
        # Person person = new Person("张三", 25);
        
        # Python风格
        class Person:
            def __init__(self, name, age):
                self.name = name
                self.age = age
        
        person = Person("张三", 25)
        return person
    
    # 集合操作
    def collection_operations(self):
        """集合操作对比"""
        # Java: List<String> list = new ArrayList<>();
        # list.add("item1"); list.add("item2");
        
        # Python: 列表操作
        my_list = ["item1", "item2"]
        my_list.append("item3")
        
        # Java: Map<String, Integer> map = new HashMap<>();
        # map.put("key1", 100);
        
        # Python: 字典操作
        my_dict = {"key1": 100, "key2": 200}
        my_dict["key3"] = 300
        
        return my_list, my_dict
    
    # 异常处理
    def exception_handling(self):
        """异常处理对比"""
        try:
            # 可能出错的代码
            result = 10 / 0
        except ZeroDivisionError as e:
            print(f"除零错误: {e}")
        except Exception as e:
            print(f"其他错误: {e}")
        finally:
            print("清理资源")

# 2. AI相关库导入示例
def ai_libraries_demo():
    """AI常用库导入示例"""
    # 数据处理
    import numpy as np
    import pandas as pd
    
    # 机器学习
    from sklearn.model_selection import train_test_split
    from sklearn.linear_model import LinearRegression
    from sklearn.metrics import accuracy_score
    
    # 深度学习
    import tensorflow as tf
    import torch
    import torch.nn as nn
    
    # 自然语言处理
    import nltk
    from transformers import pipeline
    
    # 计算机视觉
    import cv2
    from PIL import Image
    
    print("AI库导入成功！")

# 3. Java开发者友好的Python特性
def python_features_for_java_devs():
    """Python对Java开发者友好的特性"""
    
    # 列表推导式（类似Java Stream API但更简洁）
    numbers = [1, 2, 3, 4, 5]
    squares = [x**2 for x in numbers if x % 2 == 0]
    
    # 装饰器（类似Java注解）
    def timer_decorator(func):
        import time
        def wrapper(*args, **kwargs):
            start = time.time()
            result = func(*args, **kwargs)
            end = time.time()
            print(f"{func.__name__} 执行时间: {end - start:.4f}秒")
            return result
        return wrapper
    
    @timer_decorator
    def slow_function():
        import time
        time.sleep(1)
        return "完成"
    
    # 上下文管理器（类似try-with-resources）
    def file_operation():
        with open('data.txt', 'w') as f:
            f.write('Hello AI World')
        # 文件自动关闭，无需手动close()

if __name__ == "__main__":
    print("=== Java开发者Python学习指南 ===")
    
    # 创建对比实例
    comparison = JavaToPythonComparison()
    
    # 测试各种操作
    person = comparison.object_creation()
    print(f"创建对象: {person.name}, {person.age}")
    
    list_data, dict_data = comparison.collection_operations()
    print(f"列表操作: {list_data}")
    print(f"字典操作: {dict_data}")
    
    # 运行AI库演示
    ai_libraries_demo()
    
    # 测试装饰器功能
    result = python_features_for_java_devs().__globals__['slow_function']()
    print(result)