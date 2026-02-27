# 第3章：第一个Hello World应用

## 🎯 本章学习目标

- 创建完整的Spring Web Hello World应用
- 理解Spring Boot vs 传统Spring配置的区别
- 掌握控制器的基本编写方法
- 学会使用不同的视图技术
- 实现RESTful API接口

## 3.1 传统Spring Web项目Hello World

### 3.1.1 项目结构回顾

```
hello-world-traditional/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/helloworld/
│       │       ├── config/
│       │       │   └── WebConfig.java
│       │       └── controller/
│       │           └── HelloController.java
│       └── webapp/
│           ├── WEB-INF/
│           │   ├── views/
│           │   │   ├── hello.jsp
│           │   │   └── welcome.jsp
│           │   └── web.xml
│           └── static/
│               ├── css/
│               │   └── style.css
│               └── js/
│                   └── app.js
└── build/
```

### 3.1.2 完整代码实现

#### Gradle配置文件（build.gradle）：
```gradle
plugins {
    id 'java'
    id 'war'
    id 'org.springframework.boot' version '2.7.0' apply false
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
}

group = 'com.example'
version = '1.0.0'
sourceCompatibility = '11'
targetCompatibility = '11'

repositories {
    maven { url 'https://maven.aliyun.com/repository/public' }
    maven { url 'https://maven.aliyun.com/repository/spring' }
    mavenCentral()
}

ext {
    springVersion = '5.3.21'
    servletVersion = '4.0.1'
    jacksonVersion = '2.13.3'
}

dependencies {
    // Spring Web MVC
    implementation "org.springframework:spring-webmvc:${springVersion}"
    implementation "org.springframework:spring-context:${springVersion}"
    
    // Servlet API
    compileOnly "javax.servlet:javax.servlet-api:${servletVersion}"
    providedRuntime "javax.servlet:javax.servlet-api:${servletVersion}"
    
    // JSP支持
    compileOnly 'javax.servlet.jsp:javax.servlet.jsp-api:2.3.3'
    providedRuntime 'javax.servlet.jsp:javax.servlet.jsp-api:2.3.3'
    
    // JSTL标签库
    implementation 'javax.servlet:jstl:1.2'
    
    // Jackson JSON处理
    implementation "com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}"
    implementation "com.fasterxml.jackson.core:jackson-core:${jacksonVersion}"
    
    // 日志框架
    implementation 'ch.qos.logback:logback-classic:1.2.11'
    implementation 'org.slf4j:slf4j-api:1.7.36'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
    options.compilerArgs << '-Xlint:unchecked' << '-Xlint:deprecation'
}

// WAR打包配置
war {
    archiveFileName = "hello-world-${version}.war"
}

// 自定义Tomcat运行任务
task runTomcat(type: JavaExec) {
    description = '运行内嵌Tomcat服务器'
    group = 'application'
    
    mainClass = 'org.springframework.boot.loader.PropertiesLauncher'
    classpath = sourceSets.main.runtimeClasspath
    jvmArgs = [
        '-Dserver.port=8080',
        '-Dserver.servlet.context-path=/hello'
    ]
    
    doFirst {
        println "启动Tomcat服务器..."
        println "访问地址: http://localhost:8080/hello"
    }
}

// 便捷任务别名
task start(dependsOn: runTomcat) {
    description = '启动应用'
    group = 'application'
}
```

#### Web配置文件（web.xml）：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    
    <display-name>Hello World Application</display-name>
    
    <!-- Spring DispatcherServlet -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring-config.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    
    <!-- 字符编码过滤器 -->
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    
    <!-- 静态资源处理 -->
    <servlet-mapping>
        <servlet-name>default</servlet-name>
        <url-pattern>*.css</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>default</servlet-name>
        <url-pattern>*.js</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>default</servlet-name>
        <url-pattern>*.png</url-pattern>
    </servlet-mapping>
</web-app>
```

#### Spring配置文件（resources/spring-config.xml）：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context.xsd
           http://www.springframework.org/schema/mvc
           http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    
    <!-- 组件扫描 -->
    <context:component-scan base-package="com.example.helloworld"/>
    
    <!-- 注解驱动 -->
    <mvc:annotation-driven/>
    
    <!-- 静态资源处理 -->
    <mvc:resources mapping="/static/**" location="/static/"/>
    
    <!-- 视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>
```

#### 控制器实现：
```java
package com.example.helloworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {
    
    /**
     * 首页跳转
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/welcome";
    }
    
    /**
     * 欢迎页面
     */
    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("appName", "Hello World Application");
        model.addAttribute("currentTime", LocalDateTime.now());
        return "welcome";
    }
    
    /**
     * 传统的Hello World页面
     */
    @GetMapping("/hello")
    public String hello(
            @RequestParam(value = "name", defaultValue = "World") String name,
            Model model) {
        model.addAttribute("name", name);
        model.addAttribute("message", "Hello " + name + "!");
        model.addAttribute("timestamp", System.currentTimeMillis());
        return "hello";
    }
    
    /**
     * RESTful API - 返回JSON数据
     */
    @GetMapping("/api/hello")
    @ResponseBody
    public Map<String, Object> helloApi(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello " + name + "!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("success", true);
        return response;
    }
    
    /**
     * POST请求示例
     */
    @PostMapping("/api/greet")
    @ResponseBody
    public Map<String, Object> greet(@RequestBody Map<String, String> requestData) {
        String name = requestData.getOrDefault("name", "Anonymous");
        Map<String, Object> response = new HashMap<>();
        response.put("greeting", "Nice to meet you, " + name + "!");
        response.put("receivedAt", LocalDateTime.now().toString());
        return response;
    }
}
```

#### JSP视图文件：

**welcome.jsp**：
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${appName}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>${appName}</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">首页</a>
                <a href="${pageContext.request.contextPath}/hello">Hello页面</a>
                <a href="${pageContext.request.contextPath}/api/hello">API测试</a>
            </nav>
        </header>
        
        <main>
            <section class="welcome-section">
                <h2>欢迎来到Spring Web世界！</h2>
                <p>这是一个完整的Hello World示例应用。</p>
                <p>当前时间：<strong>${currentTime}</strong></p>
                
                <div class="features">
                    <h3>功能演示：</h3>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/hello?name=张三">带参数的Hello页面</a></li>
                        <li><a href="${pageContext.request.contextPath}/api/hello?name=李四">JSON API接口</a></li>
                        <li><a href="#" onclick="testPostApi()">POST API测试</a></li>
                    </ul>
                </div>
            </section>
        </main>
        
        <footer>
            <p>&copy; 2024 Spring Web学习示例</p>
        </footer>
    </div>
    
    <script src="${pageContext.request.contextPath}/static/js/app.js"></script>
</body>
</html>
```

**hello.jsp**：
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hello Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Hello World Application</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/welcome">返回首页</a>
            </nav>
        </header>
        
        <main>
            <div class="hello-message">
                <h2>${message}</h2>
                <p>欢迎你，<strong>${name}</strong>！</p>
                <p>页面加载时间戳：${timestamp}</p>
                
                <form action="${pageContext.request.contextPath}/hello" method="get">
                    <label for="name">输入你的名字：</label>
                    <input type="text" id="name" name="name" value="${name}">
                    <button type="submit">重新问候</button>
                </form>
            </div>
        </main>
    </div>
</body>
</html>
```

#### 静态资源文件：

**CSS样式（static/css/style.css）**：
```css
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Arial', sans-serif;
    line-height: 1.6;
    color: #333;
    background-color: #f4f4f4;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 1rem;
    border-radius: 10px;
    margin-bottom: 2rem;
}

header h1 {
    margin-bottom: 1rem;
}

nav a {
    color: white;
    text-decoration: none;
    margin-right: 20px;
    padding: 5px 10px;
    border-radius: 5px;
    transition: background-color 0.3s;
}

nav a:hover {
    background-color: rgba(255, 255, 255, 0.2);
}

.welcome-section {
    background: white;
    padding: 2rem;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.features ul {
    list-style-type: none;
    margin-top: 1rem;
}

.features li {
    margin: 10px 0;
    padding: 10px;
    background: #f8f9fa;
    border-left: 4px solid #667eea;
    border-radius: 5px;
}

.features a {
    color: #667eea;
    text-decoration: none;
    font-weight: bold;
}

.features a:hover {
    text-decoration: underline;
}

.hello-message {
    background: white;
    padding: 2rem;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    text-align: center;
}

.hello-message h2 {
    color: #667eea;
    margin-bottom: 1rem;
}

form {
    margin-top: 2rem;
    display: flex;
    justify-content: center;
    gap: 10px;
    flex-wrap: wrap;
}

input[type="text"] {
    padding: 10px;
    border: 2px solid #ddd;
    border-radius: 5px;
    font-size: 16px;
}

button {
    padding: 10px 20px;
    background: #667eea;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
    transition: background-color 0.3s;
}

button:hover {
    background: #5a6fd8;
}

footer {
    text-align: center;
    margin-top: 2rem;
    padding: 1rem;
    color: #666;
}
```

**JavaScript文件（static/js/app.js）**：
```javascript
// POST API测试函数
function testPostApi() {
    const name = prompt('请输入你的名字：') || '访客';
    
    fetch('/hello/api/greet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name: name })
    })
    .then(response => response.json())
    .then(data => {
        alert(`收到回复：${data.greeting}\n时间：${data.receivedAt}`);
    })
    .catch(error => {
        console.error('Error:', error);
        alert('请求失败：' + error.message);
    });
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    console.log('Hello World应用已加载');
    
    // 添加一些交互效果
    const links = document.querySelectorAll('a[href^="/"]');
    links.forEach(link => {
        link.addEventListener('click', function(e) {
            // 可以在这里添加页面切换动画
            console.log('导航到：', this.href);
        });
    });
});
```

## 3.2 Spring Boot版本Hello World

### 3.2.1 项目结构

```
hello-world-boot/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/boot/
│       │       ├── HelloWorldApplication.java
│       │       └── controller/
│       │           └── HelloController.java
│       └── resources/
│           ├── application.yml
│           ├── static/
│           │   └── index.html
│           └── templates/
│               └── hello.html
└── target/
```

### 3.2.2 完整代码实现

#### Maven配置（pom.xml）：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>hello-world-boot</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>11</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Thymeleaf模板引擎 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        
        <!-- 开发工具 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        
        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 主启动类：
```java
package com.example.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloWorldApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
}
```

#### 控制器代码：
```java
package com.example.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/welcome";
    }
    
    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("appName", "Spring Boot Hello World");
        model.addAttribute("currentTime", LocalDateTime.now());
        return "welcome";
    }
    
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("message", "Hello " + name + "!");
        return "hello";
    }
    
    @GetMapping("/api/hello")
    @ResponseBody
    public Map<String, Object> helloApi(@RequestParam(defaultValue = "World") String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello " + name + "!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("success", true);
        return response;
    }
}
```

#### 配置文件（application.yml）：
```yaml
server:
  port: 8080
  servlet:
    context-path: /boot-hello

spring:
  application:
    name: hello-world-boot
  
  thymeleaf:
    cache: false  # 开发环境下关闭缓存
    
logging:
  level:
    com.example.boot: DEBUG
    org.springframework.web: DEBUG
```

#### Thymeleaf模板：

**templates/welcome.html**：
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title th:text="${appName}">Welcome</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 800px; margin: 0 auto; }
        .header { background: #667eea; color: white; padding: 20px; border-radius: 10px; }
        .content { background: white; padding: 30px; margin: 20px 0; border-radius: 10px; }
        a { color: #667eea; text-decoration: none; margin-right: 20px; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1 th:text="${appName}">Welcome Application</h1>
            <nav>
                <a href="/">首页</a>
                <a href="/hello">Hello页面</a>
                <a href="/api/hello">API接口</a>
            </nav>
        </div>
        
        <div class="content">
            <h2>欢迎来到Spring Boot世界！</h2>
            <p>这是使用Spring Boot创建的Hello World应用。</p>
            <p>当前时间：<strong th:text="${currentTime}">时间</strong></p>
            
            <h3>功能演示：</h3>
            <ul>
                <li><a href="/hello?name=张三">带参数的Hello页面</a></li>
                <li><a href="/api/hello?name=李四">JSON API接口</a></li>
            </ul>
        </div>
    </div>
</body>
</html>
```

## 3.3 两种方式对比

| 特性 | 传统Spring | Spring Boot |
|------|------------|-------------|
| 配置复杂度 | 高（XML配置多） | 低（约定优于配置） |
| 启动速度 | 较慢 | 快速 |
| 依赖管理 | 手动管理 | 自动管理 |
| 部署方式 | WAR包部署 | 可打包为JAR直接运行 |
| 学习曲线 | 陡峭 | 平缓 |
| 适用场景 | 企业级复杂应用 | 快速原型开发 |

## 3.4 运行和测试

### 3.4.1 传统Spring项目运行：

```bash
# 1. 编译打包
./gradlew clean war

# 2. 部署到Tomcat
# 将build/libs/hello-world-1.0.0.war复制到Tomcat的webapps目录

# 3. 启动Tomcat
./startup.sh

# 4. 访问应用
http://localhost:8080/hello/
```

### 3.4.2 Spring Boot项目运行：

```bash
# 方式一：使用Gradle运行
./gradlew bootRun

# 方式二：打包后运行
./gradlew clean build
java -jar build/libs/hello-world-boot-1.0.0.jar

# 访问应用
http://localhost:8080/boot-hello/
```

### 3.4.3 API测试：

```bash
# 测试GET请求
curl "http://localhost:8080/hello/api/hello?name=测试用户"

# 测试POST请求
curl -X POST "http://localhost:8080/hello/api/greet" \
  -H "Content-Type: application/json" \
  -d '{"name":"测试用户"}'

# 使用Gradle测试任务
./gradlew test
```

### 3.4.4 常用Gradle命令：

```bash
# 查看所有任务
./gradlew tasks

# 清理构建目录
./gradlew clean

# 编译Java代码
./gradlew compileJava

# 运行测试
./gradlew test

# 打包WAR文件
./gradlew war

# 构建完整项目
./gradlew build

# 生成依赖报告
./gradlew dependencies

# 运行内嵌Tomcat
./gradlew runTomcat
```

## 🔧 本章小结

本章我们学习了：
- ✅ 传统Spring Web项目的完整实现
- ✅ Spring Boot简化版本的开发
- ✅ 控制器、视图、静态资源的整合
- ✅ RESTful API的设计和实现
- ✅ 两种开发方式的对比分析

## 🚀 下一步

下一章我们将深入学习控制器的各种特性和路由配置技巧！

---

**💡 练习作业：**
1. 为Hello World应用添加用户列表功能
2. 实现表单提交和数据验证
3. 添加错误页面处理（404、500等）
4. 尝试集成其他模板引擎（如Freemarker）