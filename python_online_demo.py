#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Python基础语法演示 - Java开发者视角
在线运行版本，无需本地安装
"""

def java_developer_python_guide():
    """
    为Java开发者准备的Python入门指南
    """
    print("=" * 50)
    print("🐍 Python基础语法快速入门 - Java开发者版")
    print("=" * 50)
    
    # 1. 基本语法对比
    print("\n1. 基本语法对比")
    print("-" * 30)
    
    print("Java风格:")
    print("  public class HelloWorld {")
    print("      public static void main(String[] args) {")
    print("          System.out.println(\"Hello World\");")
    print("      }")
    print("  }")
    
    print("\nPython风格:")
    print("  print('Hello World')")
    print("  # 不需要类定义，直接执行")
    
    # 2. 变量声明对比
    print("\n2. 变量声明对比")
    print("-" * 30)
    
    print("Java: int age = 25; String name = \"张三\";")
    print("Python: age = 25  # 动态类型，无需声明类型")
    print("        name = '张三'  # 单引号双引号都可以")
    
    # 3. 数据类型演示
    print("\n3. 主要数据类型演示")
    print("-" * 30)
    
    # 数字类型
    integer_var = 42
    float_var = 3.14159
    boolean_var = True
    
    print(f"整数: {integer_var} (类型: {type(integer_var)})")
    print(f"浮点数: {float_var} (类型: {type(float_var)})")
    print(f"布尔值: {boolean_var} (类型: {type(boolean_var)})")
    
    # 字符串操作
    text = "Hello Python"
    print(f"字符串: {text}")
    print(f"字符串长度: {len(text)}")
    print(f"切片操作: {text[0:5]}")
    print(f"格式化: {text.upper()}")
    
    # 4. 控制结构对比
    print("\n4. 控制结构对比")
    print("-" * 30)
    
    print("Java的if语句:")
    print("  if (age >= 18) {")
    print("      System.out.println(\"成年人\");")
    print("  } else {")
    print("      System.out.println(\"未成年人\");")
    print("  }")
    
    print("\nPython的if语句:")
    age = 25
    if age >= 18:
        print("成年人")
    else:
        print("未成年人")
    
    # 5. 循环对比
    print("\n5. 循环结构对比")
    print("-" * 30)
    
    print("Java的for循环:")
    print("  for (int i = 0; i < 5; i++) {")
    print("      System.out.println(i);")
    print("  }")
    
    print("\nPython的for循环:")
    for i in range(5):
        print(f"  {i}")
    
    print("\nPython的while循环:")
    count = 0
    while count < 3:
        print(f"  计数: {count}")
        count += 1
    
    # 6. 函数定义对比
    print("\n6. 函数定义对比")
    print("-" * 30)
    
    print("Java方法:")
    print("  public static int add(int a, int b) {")
    print("      return a + b;")
    print("  }")
    
    print("\nPython函数:")
    def add(a, b):
        """两数相加函数"""
        return a + b
    
    result = add(10, 20)
    print(f"  add(10, 20) = {result}")
    
    # 7. 集合类型对比
    print("\n7. 集合类型对比")
    print("-" * 30)
    
    # 列表(List) - 类似ArrayList
    java_list_note = "// Java: List<String> list = new ArrayList<>();"
    python_list = ["apple", "banana", "orange"]
    print(java_list_note)
    print(f"Python列表: {python_list}")
    print(f"  长度: {len(python_list)}")
    print(f"  第一个元素: {python_list[0]}")
    python_list.append("grape")
    print(f"  添加元素后: {python_list}")
    
    # 字典(Dictionary) - 类似HashMap
    java_map_note = "// Java: Map<String, Integer> map = new HashMap<>();"
    python_dict = {"apple": 5, "banana": 3, "orange": 8}
    print(java_map_note)
    print(f"Python字典: {python_dict}")
    print(f"  apple的数量: {python_dict['apple']}")
    python_dict["grape"] = 12
    print(f"  添加元素后: {python_dict}")
    
    # 8. 异常处理对比
    print("\n8. 异常处理对比")
    print("-" * 30)
    
    print("Java异常处理:")
    print("  try {")
    print("      int result = 10 / 0;")
    print("  } catch (ArithmeticException e) {")
    print("      System.out.println(\"除零错误\");")
    print("  }")
    
    print("\nPython异常处理:")
    try:
        result = 10 / 0
    except ZeroDivisionError as e:
        print(f"  除零错误: {e}")
    except Exception as e:
        print(f"  其他错误: {e}")
    finally:
        print("  清理资源")
    
    # 9. 文件操作对比
    print("\n9. 文件操作对比")
    print("-" * 30)
    
    print("Java文件读取:")
    print("  BufferedReader reader = new BufferedReader(new FileReader(\"file.txt\"));")
    print("  String line = reader.readLine();")
    
    print("\nPython文件读取:")
    print("  with open('file.txt', 'r') as f:")
    print("      content = f.read()")
    
    # 10. 实用技巧
    print("\n10. Python特色功能")
    print("-" * 30)
    
    # 列表推导式
    numbers = [1, 2, 3, 4, 5]
    squares = [x**2 for x in numbers if x % 2 == 0]
    print(f"列表推导式 - 偶数的平方: {squares}")
    
    # 多变量赋值
    a, b = 10, 20
    print(f"多变量赋值: a={a}, b={b}")
    
    # 交换变量
    a, b = b, a
    print(f"交换后: a={a}, b={b}")
    
    print("\n" + "=" * 50)
    print("🎯 Python基础语法演示完成！")
    print("💡 记住：Python注重简洁和可读性")
    print("=" * 50)

def interactive_practice():
    """
    交互式练习区域
    """
    print("\n🎮 互动练习区")
    print("-" * 30)
    
    # 练习1：温度转换
    def celsius_to_fahrenheit(celsius):
        """摄氏度转华氏度"""
        return celsius * 9/5 + 32
    
    temp_c = 25
    temp_f = celsius_to_fahrenheit(temp_c)
    print(f"练习1 - 温度转换:")
    print(f"  {temp_c}°C = {temp_f}°F")
    
    # 练习2：简单计算器
    def calculator(a, b, operator):
        """简单计算器"""
        operations = {
            '+': a + b,
            '-': a - b,
            '*': a * b,
            '/': a / b if b != 0 else "除数不能为零"
        }
        return operations.get(operator, "不支持的运算符")
    
    calc_result = calculator(15, 3, '/')
    print(f"练习2 - 计算器:")
    print(f"  15 / 3 = {calc_result}")
    
    # 练习3：字符串处理
    def process_text(text):
        """文本处理示例"""
        words = text.split()
        word_count = len(words)
        unique_words = len(set(words))
        return {
            '单词总数': word_count,
            '不重复单词数': unique_words,
            '首字母大写': text.title()
        }
    
    sample_text = "hello world python programming"
    text_stats = process_text(sample_text)
    print(f"练习3 - 文本处理:")
    for key, value in text_stats.items():
        print(f"  {key}: {value}")

if __name__ == "__main__":
    # 运行演示
    java_developer_python_guide()
    interactive_practice()
    
    print("\n📚 学习建议:")
    print("1. 先熟悉基本语法差异")
    print("2. 多练习列表和字典操作")
    print("3. 理解Python的缩进规则")
    print("4. 学习常用的内置函数")
    print("5. 实践项目驱动学习")