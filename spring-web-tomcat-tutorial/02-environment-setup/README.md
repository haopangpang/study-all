# 第2章：开发环境搭建

## 🎯 本章学习目标

- 完成Java开发环境配置
- 安装和配置Apache Tomcat服务器
- 搭建Spring Web开发项目
- 配置IDE开发工具
- 验证环境是否正常工作

## 2.1 Java环境配置

### 2.1.1 JDK安装

#### Windows系统安装步骤：

1. **下载JDK**
   - 访问 [Oracle官网](https://www.oracle.com/java/technologies/downloads/) 或 [OpenJDK](https://openjdk.org/)
   - 选择Java 8或更高版本（推荐Java 11或17）

2. **安装JDK**
   ```
   双击下载的exe文件，按照安装向导完成安装
   ```

3. **配置环境变量**
   ```batch
   # 设置JAVA_HOME
   JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
   
   # 添加到PATH
   PATH=%JAVA_HOME%\bin;%PATH%
   ```

4. **验证安装**
   ```bash
   java -version
   javac -version
   ```

#### Linux/Mac系统安装：

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-11-jdk

# CentOS/RHEL
sudo yum install java-11-openjdk-devel

# macOS (使用Homebrew)
brew install openjdk@11
```

### 2.1.2 Gradle安装配置

#### 下载安装：
1. 访问 [Gradle官网](https://gradle.org/releases/)
2. 下载最新稳定版binary-only zip
3. 解压到指定目录

#### 环境变量配置：
```batch
# Windows
GRADLE_HOME=C:\gradle-8.x
PATH=%GRADLE_HOME%\bin;%PATH%

# Linux/Mac
export GRADLE_HOME=/opt/gradle-8.x
export PATH=$GRADLE_HOME/bin:$PATH
```

#### 验证安装：
```bash
gradle -version
```

#### Gradle配置优化（~/.gradle/gradle.properties）：
```properties
# 阿里云镜像加速
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
org.gradle.caching=true

# 镜像配置
systemProp.http.proxyHost=
systemProp.http.proxyPort=
systemProp.https.proxyHost=
systemProp.https.proxyPort=

# 使用阿里云镜像
repositories {
    maven { url 'https://maven.aliyun.com/repository/public' }
    maven { url 'https://maven.aliyun.com/repository/spring' }
    maven { url 'https://maven.aliyun.com/repository/google' }
    mavenCentral()
}
```

## 2.2 Apache Tomcat安装配置

### 2.2.1 Tomcat下载安装

#### 下载地址：
- 官方网站：https://tomcat.apache.org/
- 推荐版本：Tomcat 9.x 或 10.x

#### Windows安装：
```batch
# 1. 下载zip包并解压
# 2. 设置环境变量
CATALINA_HOME=C:\apache-tomcat-9.0.x
PATH=%CATALINA_HOME%\bin;%PATH%
```

#### Linux安装：
```bash
# 1. 下载并解压
wget https://downloads.apache.org/tomcat/tomcat-9/v9.0.xx/bin/apache-tomcat-9.0.xx.tar.gz
tar -xzf apache-tomcat-9.0.xx.tar.gz
sudo mv apache-tomcat-9.0.xx /opt/tomcat

# 2. 设置权限
sudo chown -R $USER:$USER /opt/tomcat
chmod +x /opt/tomcat/bin/*.sh
```

### 2.2.2 Tomcat基本配置

#### 目录结构说明：
```
tomcat/
├── bin/          # 启动脚本
├── conf/         # 配置文件
│   ├── server.xml    # 主配置文件
│   ├── web.xml       # 默认web应用配置
│   └── tomcat-users.xml  # 用户权限配置
├── lib/          # 库文件
├── logs/         # 日志文件
├── temp/         # 临时文件
├── webapps/      # Web应用部署目录
└── work/         # 工作目录
```

#### 重要配置文件修改：

**conf/server.xml** - 端口配置：
```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />

<!-- 如果需要HTTPS -->
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="150" SSLEnabled="true">
    <SSLHostConfig>
        <Certificate certificateKeystoreFile="conf/localhost-rsa.jks"
                     type="RSA" />
    </SSLHostConfig>
</Connector>
```

**conf/tomcat-users.xml** - 管理员账户：
```xml
<tomcat-users>
    <role rolename="manager-gui"/>
    <role rolename="admin-gui"/>
    <user username="admin" password="admin123" 
          roles="manager-gui,admin-gui"/>
</tomcat-users>
```

### 2.2.3 Tomcat启动测试

#### 启动命令：
```bash
# Windows
startup.bat

# Linux/Mac
./startup.sh

# 或者使用 catalina.sh
./catalina.sh start
```

#### 验证启动：
访问 http://localhost:8080
应该能看到Tomcat欢迎页面

#### 常用管理命令：
```bash
# 停止Tomcat
shutdown.bat  # Windows
./shutdown.sh # Linux/Mac

# 查看状态
./catalina.sh version
```

## 2.3 IDE环境配置

### 2.3.1 IntelliJ IDEA配置

#### 插件推荐安装：
- Spring Assistant
- Lombok
- MyBatis Log Plugin
- Rainbow Brackets

#### 项目创建：
1. File → New → Project
2. 选择 Maven 或 Spring Initializr
3. 配置项目基本信息

#### Tomcat集成配置：
1. Run → Edit Configurations
2. 点击"+" → Tomcat Server → Local
3. 配置Server选项卡：
   - Name: Tomcat 9
   - Application server: 选择Tomcat安装路径
4. 配置Deployment选项卡：
   - 点击"+"添加Artifact

### 2.3.2 Eclipse配置

#### 插件安装：
- Spring Tools 4
- Maven Integration

#### 服务器配置：
1. Window → Show View → Servers
2. 右键 → New → Server
3. 选择Apache Tomcat v9.0
4. 选择Tomcat安装目录

## 2.4 创建第一个Spring Web项目

### 2.4.1 使用Gradle创建项目

#### 项目结构：
```
spring-web-demo/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/demo/
│   │   │       ├── controller/
│   │   │       └── config/
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml
│   │       └── index.jsp
│   └── test/
│       └── java/
└── build/
```

#### build.gradle配置：
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
}

dependencies {
    // Spring Web MVC
    implementation "org.springframework:spring-webmvc:${springVersion}"
    
    // Servlet API
    compileOnly "javax.servlet:javax.servlet-api:${servletVersion}"
    providedRuntime "javax.servlet:javax.servlet-api:${servletVersion}"
    
    // JSP支持
    compileOnly 'javax.servlet.jsp:javax.servlet.jsp-api:2.3.3'
    providedRuntime 'javax.servlet.jsp:javax.servlet.jsp-api:2.3.3'
    
    // JSTL标签库
    implementation 'javax.servlet:jstl:1.2'
    
    // 测试依赖
    testImplementation 'org.springframework:spring-test'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

test {
    useJUnitPlatform()
}

// Tomcat插件配置
apply plugin: 'org.springframework.boot'

bootRun {
    mainClass = 'com.example.demo.Application'
}

// 自定义Tomcat任务
task runTomcat(type: JavaExec) {
    mainClass = 'org.apache.catalina.startup.Bootstrap'
    classpath = sourceSets.main.runtimeClasspath
    args = ['start']
    jvmArgs = ['-Dcatalina.base=build/tomcat', '-Dcatalina.home=build/tomcat']
}
```

#### settings.gradle配置：
```gradle
rootProject.name = 'spring-web-demo'
```

#### gradle.properties配置：
```properties
# JVM配置
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m

# 并行构建
org.gradle.parallel=true

# 启用构建缓存
org.gradle.caching=true

# 守护进程
org.gradle.daemon=true
```

### 2.4.2 配置文件设置

#### web.xml配置：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    
    <!-- Spring DispatcherServlet配置 -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/spring-config.xml</param-value>
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
</web-app>
```

#### Spring配置文件（WEB-INF/spring-config.xml）：
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
    
    <!-- 启用注解扫描 -->
    <context:component-scan base-package="com.example.demo"/>
    
    <!-- 启用Spring MVC注解驱动 -->
    <mvc:annotation-driven/>
    
    <!-- 视图解析器配置 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>
```

## 2.5 环境验证测试

### 2.5.1 编写测试代码

#### 控制器代码：
```java
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello Spring Web!");
        model.addAttribute("currentTime", new java.util.Date());
        return "home";
    }
}
```

#### JSP视图文件（WEB-INF/views/home.jsp）：
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Spring Web Demo</title>
</head>
<body>
    <h1>${message}</h1>
    <p>当前时间: ${currentTime}</p>
    <hr>
    <p>环境信息:</p>
    <ul>
        <li>Server Info: ${pageContext.servletContext.serverInfo}</li>
        <li>Session ID: ${pageContext.session.id}</li>
    </ul>
</body>
</html>
```

### 2.5.2 项目构建和部署

#### Gradle命令：
```bash
# 查看可用任务
gradle tasks

# 清理项目
gradle clean

# 编译项目
gradle compileJava

# 打包WAR文件
gradle war

# 运行项目
gradle bootRun

# 运行测试
gradle test

# 构建完整项目
gradle build
```

#### 手动部署到Tomcat：
1. 将生成的WAR文件（位于`build/libs/`目录）复制到Tomcat的webapps目录
2. 启动Tomcat服务器
3. 访问：http://localhost:8080/spring-web-demo/

#### Gradle Wrapper使用（推荐）：
```bash
# 首次使用需要生成wrapper
gradle wrapper

# 使用wrapper执行命令（无需安装Gradle）
./gradlew clean build

# Windows下
gradlew.bat clean build
```

### 2.5.3 验证检查清单

✅ Java环境正常（java -version）
✅ Gradle环境正常（gradle -version）
✅ Tomcat能正常启动和访问
✅ 项目能成功编译打包
✅ 应用能正确部署并运行
✅ 页面能正常显示预期内容
✅ Gradle Wrapper能正常使用

## 🔧 常见问题解决

### Q1: 端口被占用怎么办？
```bash
# Windows查看端口占用
netstat -ano | findstr :8080

# Linux/Mac查看端口占用
lsof -i :8080

# 修改Gradle端口
# 在build.gradle中添加：
bootRun {
    systemProperty 'server.port', '8081'
}

# 或者运行时指定端口
./gradlew bootRun --args='--server.port=8081'
```

### Q2: 中文乱码问题
确保在web.xml中配置了字符编码过滤器，并在JSP页面添加：
```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
```

### Q3: 404错误
检查以下几点：
- URL路径是否正确
- 控制器注解配置是否正确
- 视图文件是否存在且路径正确

## 📚 本章小结

本章我们完成了：
- ✅ Java和Maven环境配置
- ✅ Tomcat服务器安装和基本配置
- ✅ IDE开发环境搭建
- ✅ 第一个Spring Web项目的创建
- ✅ 环境验证测试

## 🚀 下一步

下一章我们将深入学习控制器的编写和路由配置！

---

**💡 练习作业：**
1. 在现有项目基础上，添加一个新的控制器处理用户注册功能
2. 配置多个视图解析器支持不同类型的视图
3. 尝试使用不同的端口启动多个Tomcat实例