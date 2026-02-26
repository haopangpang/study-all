# 🐍 Python安装指南 - Windows系统

## 第一步：下载Python

1. 访问Python官方网站：https://www.python.org/downloads/
2. 点击 "Download Python 3.x.x" (建议选择最新稳定版本，如3.11或3.12)
3. 下载Windows安装包 (Windows installer (64-bit))

## 第二步：安装Python

1. **运行安装程序**
   - 双击下载的exe文件
   - ⚠️ **重要**：勾选 "Add Python to PATH" 选项
   - 选择 "Install Now" 或 "Customize installation"

2. **安装选项说明**
   ```
   ✓ Add Python to PATH (必须勾选)
   ✓ Install for all users (可选)
   ✓ Associate files with Python (可选)
   ✓ Create shortcuts (可选)
   ```

## 第三步：验证安装

打开新的命令提示符窗口，输入以下命令：

```cmd
python --version
pip --version
```

应该能看到类似输出：
```
Python 3.11.5
pip 23.2.1 from C:\Python311\lib\site-packages\pip (python 3.11)
```

## 第四步：配置开发环境

### 推荐的IDE选择：

1. **Visual Studio Code** (推荐)
   - 免费且轻量级
   - 安装Python扩展包
   - 支持调试和智能提示

2. **PyCharm Community Edition**
   - 专为Python设计
   - 功能强大但稍重

3. **Jupyter Notebook**
   - 适合数据分析和机器学习
   - 浏览器中运行

## 第五步：创建第一个Python程序

创建文件 `hello.py`：
```python
print("Hello, Python World!")
print("我是从Java转来的开发者！")
```

运行程序：
```cmd
python hello.py
```

## 常见问题解决

### 问题1：'python' 不是内部或外部命令
**解决方法**：重新安装时确保勾选"Add Python to PATH"

### 问题2：权限被拒绝
**解决方法**：以管理员身份运行命令提示符

### 问题3：版本冲突
**解决方法**：卸载旧版本，重新安装最新版本

## 下一步学习准备

安装完成后，我们将开始学习：
1. Python基础语法对比
2. 数据结构差异
3. 面向对象编程
4. 实际项目应用

预计学习时间：2-3小时完成基础入门