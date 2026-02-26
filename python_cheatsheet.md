# 🐍 Python速查表 - Java开发者专用

## 基础语法对比

### 1. Hello World
```python
# Python
print("Hello, World!")

# Java
# public class HelloWorld {
#     public static void main(String[] args) {
#         System.out.println("Hello, World!");
#     }
# }
```

### 2. 变量声明
```python
# Python - 动态类型，无需声明
name = "张三"
age = 25
height = 175.5
is_student = True

# Java - 静态类型，需声明
# String name = "张三";
# int age = 25;
# double height = 175.5;
# boolean isStudent = true;
```

### 3. 数据类型映射

| Java类型 | Python类型 | 示例 |
|---------|-----------|------|
| int | int | `age = 25` |
| double | float | `price = 99.99` |
| String | str | `name = "Python"` |
| boolean | bool | `flag = True` |
| List<T> | list | `items = [1, 2, 3]` |
| Map<K,V> | dict | `person = {"name": "张三"}` |
| Set<T> | set | `unique_items = {1, 2, 3}` |

### 4. 控制结构

#### 条件语句
```python
# Python
if age >= 18:
    print("成年人")
elif age >= 12:
    print("青少年")
else:
    print("儿童")

# Java
# if (age >= 18) {
#     System.out.println("成年人");
# } else if (age >= 12) {
#     System.out.println("青少年");
# } else {
#     System.out.println("儿童");
# }
```

#### 循环语句
```python
# Python for循环
for i in range(5):
    print(i)

# Python while循环
count = 0
while count < 3:
    print(count)
    count += 1

# Java对应
# for (int i = 0; i < 5; i++) {
#     System.out.println(i);
# }
# 
# int count = 0;
# while (count < 3) {
#     System.out.println(count);
#     count++;
# }
```

### 5. 函数定义
```python
# Python函数
def calculate_area(length, width):
    """计算矩形面积"""
    return length * width

# Java方法
# public static double calculateArea(double length, double width) {
#     return length * width;
# }
```

### 6. 集合操作对比

#### 列表(List) - 类似ArrayList
```python
# Python
fruits = ["苹果", "香蕉", "橙子"]
fruits.append("葡萄")        # 添加元素
first_fruit = fruits[0]      # 访问元素
fruits_length = len(fruits)  # 获取长度

# Java
# List<String> fruits = new ArrayList<>();
# fruits.add("葡萄");
# String firstFruit = fruits.get(0);
# int size = fruits.size();
```

#### 字典(Dict) - 类似HashMap
```python
# Python
student = {
    "name": "张三",
    "age": 20,
    "grade": 85.5
}
student["city"] = "北京"     # 添加键值对
name = student["name"]       # 获取值
keys = list(student.keys())  # 获取所有键

# Java
# Map<String, Object> student = new HashMap<>();
# student.put("city", "北京");
# String name = (String) student.get("name");
# Set<String> keys = student.keySet();
```

### 7. 类和对象

#### Python类定义
```python
class Student:
    def __init__(self, name, age):
        self.name = name
        self.age = age
    
    def introduce(self):
        return f"我是{self.name}，今年{self.age}岁"
    
    @staticmethod
    def get_school():
        return "北京大学"

# 使用
student = Student("李四", 18)
print(student.introduce())
print(Student.get_school())
```

#### Java类对比
```java
// public class Student {
//     private String name;
//     private int age;
//     
//     public Student(String name, int age) {
//         this.name = name;
//         this.age = age;
//     }
//     
//     public String introduce() {
//         return "我是" + name + "，今年" + age + "岁";
//     }
//     
//     public static String getSchool() {
//         return "北京大学";
//     }
// }
```

### 8. 异常处理
```python
# Python
try:
    result = 10 / 0
except ZeroDivisionError:
    print("除零错误")
except Exception as e:
    print(f"其他错误: {e}")
finally:
    print("清理资源")

# Java
# try {
#     int result = 10 / 0;
# } catch (ArithmeticException e) {
#     System.out.println("除零错误");
# } catch (Exception e) {
#     System.out.println("其他错误: " + e.getMessage());
# } finally {
#     System.out.println("清理资源");
# }
```

### 9. 常用内置函数

| 函数 | 用途 | 示例 |
|------|------|------|
| `len()` | 获取长度 | `len([1,2,3])` → 3 |
| `str()` | 转字符串 | `str(123)` → "123" |
| `int()` | 转整数 | `int("123")` → 123 |
| `range()` | 数字序列 | `range(5)` → 0,1,2,3,4 |
| `enumerate()` | 带索引遍历 | `enumerate(['a','b'])` |
| `zip()` | 并行遍历 | `zip([1,2], ['a','b'])` |

### 10. 实用技巧

#### 列表推导式
```python
# 传统方式
squares = []
for x in range(10):
    if x % 2 == 0:
        squares.append(x**2)

# 列表推导式 - 更简洁
squares = [x**2 for x in range(10) if x % 2 == 0]
```

#### 字符串格式化
```python
name = "张三"
age = 25

# f-string (推荐)
message = f"我是{name}，今年{age}岁"

# format方法
message = "我是{}，今年{}岁".format(name, age)

# %格式化
message = "我是%s，今年%d岁" % (name, age)
```

#### 多变量赋值
```python
# 交换变量
a, b = 10, 20
a, b = b, a  # a=20, b=10

# 解包赋值
coordinates = [100, 200]
x, y = coordinates
```

## 学习资源推荐

1. **官方文档**: https://docs.python.org/zh-cn/3/
2. **在线练习**: https://www.w3schools.com/python/
3. **交互式学习**: https://replit.com/languages/python3

## 快速开始步骤

1. 安装Python 3.8+
2. 运行 `python --version` 验证
3. 创建 `.py` 文件开始编码
4. 使用 `python filename.py` 运行程序

记住：Python之禅 - "优雅胜于丑陋，简洁胜于复杂"