# 第5章：Tomcat集成和部署指南

## 🎯 本章学习目标

- 掌握Spring Boot内嵌Tomcat的配置
- 学会外部Tomcat服务器的部署方法
- 理解WAR包和JAR包的区别及使用场景
- 掌握Tomcat性能调优技巧
- 学会监控和调试部署的应用

## 5.1 Tomcat基础回顾

### 5.1.1 Tomcat架构组件

```
Server (服务器)
└── Service (服务)
    ├── Connector (连接器)
    │   ├── HTTP Connector
    │   ├── AJP Connector
    │   └── HTTPS Connector
    └── Engine (引擎)
        └── Host (虚拟主机)
            └── Context (应用上下文)
                ├── Servlet
                ├── Filter
                └── Listener
```

### 5.1.2 核心配置文件

#### server.xml - 主配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Server port="8005" shutdown="SHUTDOWN">
    <Service name="Catalina">
        <!-- HTTP连接器 -->
        <Connector port="8080" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="8443"
                   maxThreads="200"
                   minSpareThreads="10"/>
        
        <!-- HTTPS连接器 -->
        <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
                   maxThreads="150" SSLEnabled="true">
            <SSLHostConfig>
                <Certificate certificateKeystoreFile="conf/localhost-rsa.jks"
                             type="RSA" />
            </SSLHostConfig>
        </Connector>
        
        <Engine name="Catalina" defaultHost="localhost">
            <Host name="localhost" appBase="webapps"
                  unpackWARs="true" autoDeploy="true">
                <!-- 访问日志配置 -->
                <Valve className="org.apache.catalina.valves.AccessLogValve"
                       directory="logs"
                       prefix="localhost_access_log"
                       suffix=".txt"
                       pattern="%h %l %u %t &quot;%r&quot; %s %b"/>
            </Host>
        </Engine>
    </Service>
</Server>
```

#### context.xml - 上下文配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <!-- 数据源配置 -->
    <Resource name="jdbc/mydb"
              auth="Container"
              type="javax.sql.DataSource"
              maxTotal="20"
              maxIdle="10"
              maxWaitMillis="10000"
              username="dbuser"
              password="dbpass"
              driverClassName="com.mysql.cj.jdbc.Driver"
              url="jdbc:mysql://localhost:3306/mydb"/>
    
    <!-- Session配置 -->
    <Manager pathname="" />
    
    <!-- 热部署配置 -->
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
</Context>
```

## 5.2 Spring Boot与内嵌Tomcat

### 5.2.1 内嵌Tomcat配置

#### application.yml配置：
```yaml
server:
  # 端口配置
  port: 8080
  
  # 上下文路径
  servlet:
    context-path: /myapp
    
  # Tomcat特定配置
  tomcat:
    # 连接器配置
    max-connections: 8192
    max-threads: 200
    min-spare-threads: 10
    connection-timeout: 20s
    
    # 访问日志
    accesslog:
      enabled: true
      directory: logs
      prefix: access_log
      suffix: .txt
      pattern: "%h %l %u %t \"%r\" %s %b %D"
    
    # SSL配置
    ssl:
      enabled: false
      key-store: classpath:keystore.p12
      key-store-password: password
      key-store-type: PKCS12
      key-alias: tomcat
      
    # 其他配置
    uri-encoding: UTF-8
    basedir: /tmp/tomcat
    remoteip:
      protocol-header: x-forwarded-proto
      remote-ip-header: x-forwarded-for
```

#### Java配置方式：
```java
@Configuration
public class TomcatConfig {
    
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                // 设置最大连接数
                connector.setMaxConnections(10000);
                
                // 设置超时时间
                connector.setProperty("connectionTimeout", "30000");
                
                // 设置线程池
                ProtocolHandler handler = connector.getProtocolHandler();
                if (handler instanceof AbstractProtocol) {
                    AbstractProtocol<?> protocol = (AbstractProtocol<?>) handler;
                    protocol.setMaxThreads(300);
                    protocol.setMinSpareThreads(25);
                }
            });
        };
    }
}
```

### 5.2.2 内嵌Tomcat高级配置

#### 自定义Tomcat工厂：
```java
@Component
public class CustomTomcatWebServerFactory 
    extends TomcatServletWebServerFactory 
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    
    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        Tomcat tomcat = new Tomcat();
        
        // 自定义配置
        tomcat.setBaseDir("/tmp/tomcat");
        
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8080);
        connector.setProperty("maxThreads", "400");
        connector.setProperty("acceptCount", "100");
        
        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);
        
        // 添加额外的Valve
        StandardHost host = (StandardHost) tomcat.getEngine().findChild(tomcat.getEngine().getDefaultHost());
        host.getPipeline().addValve(new CustomAccessLogValve());
        
        prepareContext(tomcat.getHost(), initializers);
        return getTomcatWebServer(tomcat);
    }
    
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // 额外的自定义配置
    }
}
```

## 5.3 外部Tomcat部署

### 5.3.1 WAR包部署方式

#### Gradle配置修改：
```gradle
// build.gradle
apply plugin: 'war'

configurations {
    providedRuntime
}

dependencies {
    // 移除内嵌Tomcat依赖
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
    
    // 其他依赖保持不变
    implementation 'org.springframework.boot:spring-boot-starter-web'
}

war {
    archiveFileName = 'myapp.war'
    enabled = true
}

// 如果使用Spring Boot插件
if (project.hasProperty('springBoot')) {
    apply plugin: 'org.springframework.boot'
    
    bootWar {
        archiveFileName = 'myapp.war'
        enabled = true
    }
}
```

#### 主应用类修改：
```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 部署步骤：
```bash
# 1. 构建WAR包
./gradlew clean war

# 2. 部署到Tomcat
cp build/libs/myapp.war $CATALINA_HOME/webapps/

# 3. 启动Tomcat
$CATALINA_HOME/bin/startup.sh

# 4. 访问应用
http://localhost:8080/myapp/

# 或者使用自定义部署任务
./gradlew copyWarToTomcat
```

#### 自定义Gradle部署任务：
```gradle
// 在build.gradle中添加
task copyWarToTomcat(type: Copy) {
    description = '将WAR文件复制到Tomcat webapps目录'
    group = 'deployment'
    
    from war
    into System.getProperty('tomcat.home', '${System.getenv('CATALINA_HOME') ?: '/opt/tomcat'}/webapps')
    
    doLast {
        println "WAR文件已部署到Tomcat"
    }
}

task startTomcat(type: Exec) {
    description = '启动Tomcat服务器'
    group = 'application'
    
    workingDir System.getProperty('tomcat.home', System.getenv('CATALINA_HOME') ?: '/opt/tomcat')
    commandLine './bin/startup.sh'
    
    doLast {
        println "Tomcat服务器启动中..."
    }
}

task deploy(dependsOn: ['clean', 'war', 'copyWarToTomcat']) {
    description = '完整部署流程'
    group = 'deployment'
}
```

### 5.3.2 手动WAR包配置

#### web.xml配置：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         version="4.0">
    
    <display-name>My Spring Application</display-name>
    
    <!-- Spring上下文配置 -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
    </context-param>
    
    <!-- ContextLoaderListener -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    
    <!-- DispatcherServlet -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value></param-value>
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

## 5.4 部署环境配置

### 5.4.1 多环境配置

#### application.yml多环境配置：
```yaml
# 公共配置
spring:
  application:
    name: myapp

# 开发环境
---
spring:
  profiles: dev
server:
  port: 8080
logging:
  level:
    com.mycompany: DEBUG

# 测试环境
---
spring:
  profiles: test
server:
  port: 8081
logging:
  level:
    com.mycompany: INFO

# 生产环境
---
spring:
  profiles: prod
server:
  port: 8080
logging:
  level:
    com.mycompany: WARN
  file:
    name: /var/log/myapp/application.log
```

#### 激活不同环境：
```bash
# 方式1：命令行参数
java -jar myapp.jar --spring.profiles.active=prod

# 方式2：环境变量
export SPRING_PROFILES_ACTIVE=prod
java -jar myapp.jar

# 方式3：JVM参数
java -Dspring.profiles.active=prod -jar myapp.jar
```

### 5.4.2 外部化配置

#### 配置文件优先级：
```
1. 命令行参数
2. SPRING_APPLICATION_JSON中的属性
3. ServletConfig初始化参数
4. ServletContext初始化参数
5. JNDI属性
6. Java系统属性(System.getProperties())
7. 操作系统环境变量
8. RandomValuePropertySource配置的random.*属性
9. jar包外的application-{profile}.properties
10. jar包内的application-{profile}.properties
11. jar包外的application.properties
12. jar包内的application.properties
```

#### 外部配置示例：
```bash
# 创建外部配置目录
mkdir -p /etc/myapp/config

# 复制配置文件
cp application-prod.properties /etc/myapp/config/

# 启动时指定配置位置
java -jar myapp.jar --spring.config.location=/etc/myapp/config/
```

## 5.5 Tomcat性能调优

### 5.5.1 JVM参数调优

```bash
#!/bin/bash
# Tomcat启动脚本优化

export JAVA_OPTS="$JAVA_OPTS -server"
export JAVA_OPTS="$JAVA_OPTS -Xms2g -Xmx4g"  # 堆内存设置
export JAVA_OPTS="$JAVA_OPTS -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"  # 元空间
export JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"  # 使用G1垃圾收集器
export JAVA_OPTS="$JAVA_OPTS -XX:MaxGCPauseMillis=200"  # GC暂停时间目标
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"  # OOM时生成堆转储
export JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=/var/log/tomcat/heapdump.hprof"
export JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"  # 无头模式
export JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"  # 字符编码
```

### 5.5.2 Tomcat连接器优化

```xml
<!-- server.xml 连接器优化配置 -->
<Connector port="8080" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
           maxThreads="400"
           minSpareThreads="50"
           maxConnections="10000"
           acceptCount="300"
           connectionTimeout="20000"
           maxKeepAliveRequests="100"
           keepAliveTimeout="20000"
           compression="on"
           compressionMinSize="2048"
           compressableMimeType="text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json">
    
    <!-- 线程池配置 -->
    <Executor name="tomcatThreadPool" 
              namePrefix="catalina-exec-"
              maxThreads="400" 
              minSpareThreads="50"
              maxIdleTime="60000"/>
</Connector>
```

### 5.5.3 数据库连接池优化

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // 连接池配置
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setLeakDetectionThreshold(60000);
        
        return dataSource;
    }
}
```

## 5.6 监控和调试

### 5.6.1 应用监控配置

#### Actuator配置：
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,loggers
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

#### 自定义健康检查：
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // 检查数据库连接
            boolean dbStatus = checkDatabase();
            // 检查外部服务
            boolean externalServiceStatus = checkExternalService();
            
            if (dbStatus && externalServiceStatus) {
                return Health.up()
                    .withDetail("database", "Available")
                    .withDetail("externalService", "Connected")
                    .build();
            } else {
                return Health.down()
                    .withDetail("database", dbStatus ? "Available" : "Unavailable")
                    .withDetail("externalService", externalServiceStatus ? "Connected" : "Disconnected")
                    .build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
    
    private boolean checkDatabase() {
        // 数据库检查逻辑
        return true;
    }
    
    private boolean checkExternalService() {
        // 外部服务检查逻辑
        return true;
    }
}
```

### 5.6.2 日志配置

#### logback-spring.xml配置：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/myapp/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/myapp/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>100MB</maxFileSize>
                <maxHistory>30</maxHistory>
                <totalSizeCap>3GB</totalSizeCap>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

### 5.6.3 远程调试配置

#### 启用远程调试：
```bash
# 在catalina.sh中添加
export JPDA_ADDRESS="5005"
export JPDA_TRANSPORT="dt_socket"

# 启动调试模式
./catalina.sh jpda start

# 或者使用JVM参数
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar myapp.jar
```

## 5.7 部署最佳实践

### 5.7.1 安全配置

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        
        // 禁用服务器版本信息
        tomcat.addConnectorCustomizers(connector -> {
            connector.setProperty("server", "MyServer");
        });
        
        return tomcat;
    }
    
    // 隐藏敏感信息
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/health", "/info");
    }
}
```

### 5.7.2 自动化部署脚本

```bash
#!/bin/bash
# deploy.sh - 自动化部署脚本

APP_NAME="myapp"
APP_VERSION="1.0.0"
TOMCAT_HOME="/opt/tomcat"
BACKUP_DIR="/opt/backups"
LOG_FILE="/var/log/deploy.log"

echo "$(date): Starting deployment..." >> $LOG_FILE

# 1. 备份当前版本
if [ -f "$TOMCAT_HOME/webapps/$APP_NAME.war" ]; then
    cp $TOMCAT_HOME/webapps/$APP_NAME.war $BACKUP_DIR/${APP_NAME}_$(date +%Y%m%d_%H%M%S).war
    echo "$(date): Backup created" >> $LOG_FILE
fi

# 2. 停止Tomcat
$TOMCAT_HOME/bin/shutdown.sh
sleep 10

# 3. 清理旧文件
rm -rf $TOMCAT_HOME/webapps/$APP_NAME*
rm -rf $TOMCAT_HOME/work/Catalina/localhost/$APP_NAME

# 4. 部署新版本
cp target/$APP_NAME-$APP_VERSION.war $TOMCAT_HOME/webapps/$APP_NAME.war

# 5. 启动Tomcat
$TOMCAT_HOME/bin/startup.sh

# 6. 等待应用启动
sleep 30

# 7. 检查部署状态
curl -f http://localhost:8080/$APP_NAME/health > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "$(date): Deployment successful" >> $LOG_FILE
else
    echo "$(date): Deployment failed, rolling back..." >> $LOG_FILE
    # 回滚逻辑
fi
```

## 🔧 本章小结

本章我们学习了：
- ✅ Tomcat服务器的核心架构和配置
- ✅ Spring Boot内嵌Tomcat的配置方法
- ✅ 外部Tomcat部署WAR包的方式
- ✅ 多环境配置和外部化配置
- ✅ Tomcat性能调优技巧
- ✅ 应用监控和调试方法
- ✅ 生产环境部署最佳实践

## 🚀 下一步

下一章我们将学习Spring Web的高级特性，包括拦截器、文件上传等！

---

**💡 练习作业：**
1. 配置一个生产环境的Tomcat服务器
2. 实现多环境配置切换
3. 部署一个Spring Boot应用到外部Tomcat
4. 配置应用监控和健康检查