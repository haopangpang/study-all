# Gradle项目配置文件说明

## 📁 文件列表

### 核心配置文件
- `sample-build.gradle` - 主构建配置文件
- `sample-settings.gradle` - 项目设置文件
- `gradlew` - Unix/Linux/macOS启动脚本
- `gradlew.bat` - Windows启动脚本

## 🚀 使用方法

### 1. 初始化项目
```bash
# 复制配置文件到项目根目录
cp sample-build.gradle build.gradle
cp sample-settings.gradle settings.gradle

# 生成Gradle Wrapper（可选但推荐）
gradle wrapper
```

### 2. 常用Gradle命令

```bash
# 基础命令
./gradlew clean              # 清理构建目录
./gradlew compileJava        # 编译Java代码
./gradlew war                # 打包WAR文件
./gradlew test               # 运行测试
./gradlew build              # 完整构建

# 开发相关
./gradlew bootRun           # 运行Spring Boot应用
./gradlew tasks             # 查看所有可用任务
./gradlew dependencies      # 查看依赖树

# 部署相关
./gradlew copyWarToTomcat   # 部署到Tomcat
```

### 3. Windows环境使用
```cmd
# 使用批处理脚本
gradlew.bat clean build

# 或者直接使用gradle命令（如果已安装）
gradle clean build
```

## ⚙️ 配置说明

### build.gradle主要配置项

```gradle
// 项目基本信息
group = 'com.example'        // 组织标识
version = '1.0.0'            // 版本号
sourceCompatibility = '11'   // Java源码兼容版本
targetCompatibility = '11'   // Java目标版本

// 依赖管理
dependencies {
    implementation 'org.springframework:spring-webmvc:5.3.21'
    compileOnly 'javax.servlet:javax.servlet-api:4.0.1'
    // ... 其他依赖
}

// 自定义任务
task copyWarToTomcat(type: Copy) {
    // 复制WAR到Tomcat目录的任务
}
```

### settings.gradle配置

```gradle
rootProject.name = 'spring-web-demo'  // 项目名称

// 镜像配置
pluginManagement {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
        // ... 其他仓库
    }
}
```

## 🔧 环境变量配置

### 设置Tomcat路径
```bash
# Linux/macOS
export TOMCAT_HOME=/path/to/tomcat

# Windows
set TOMCAT_HOME=C:\path\to\tomcat
```

或者在gradle.properties中配置：
```properties
tomcat.home=/path/to/tomcat
```

## 🎯 最佳实践

### 1. 使用Wrapper
始终使用Gradle Wrapper而不是全局安装的Gradle，确保团队成员使用相同的版本。

### 2. 依赖管理
- 使用阿里云镜像加速下载
- 明确指定依赖版本
- 区分implementation和compileOnly作用域

### 3. 构建优化
```gradle
// 启用并行构建
org.gradle.parallel=true

// 启用构建缓存
org.gradle.caching=true

// 使用守护进程
org.gradle.daemon=true
```

### 4. 测试配置
```gradle
test {
    useJUnitPlatform()
    testLogging {
        events "passed", "skipped", "failed"
    }
}
```

## 📝 注意事项

1. **版本兼容性**：确保Java版本与Gradle版本兼容
2. **网络环境**：国内建议使用阿里云镜像
3. **权限问题**：Linux/macOS下可能需要给gradlew添加执行权限
4. **路径问题**：注意Windows和Unix系统的路径分隔符差异

## 🔍 故障排除

### 常见问题解决

1. **找不到Java命令**
   ```bash
   # 设置JAVA_HOME环境变量
   export JAVA_HOME=/path/to/jdk
   ```

2. **依赖下载失败**
   ```gradle
   // 检查repositories配置
   repositories {
       maven { url 'https://maven.aliyun.com/repository/public' }
       mavenCentral()
   }
   ```

3. **内存不足**
   ```properties
   # gradle.properties中增加内存
   org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
   ```

## 📚 参考资料

- [Gradle官方文档](https://docs.gradle.org/)
- [Spring Boot Gradle插件](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)
- [阿里云Maven镜像](https://developer.aliyun.com/mvn/guide)