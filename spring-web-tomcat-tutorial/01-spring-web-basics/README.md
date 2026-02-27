# 第1章：Spring Web核心概念

## 🎯 本章学习目标

- 理解Spring Web MVC架构模式
- 掌握Spring框架的核心组件
- 了解HTTP请求处理流程
- 熟悉Spring Web的基本工作原理

## 1.1 Spring Web MVC概述

### 1.1.1 什么是MVC模式？

**MVC（Model-View-Controller）** 是一种软件架构设计模式，将应用程序分为三个核心组件：

```
┌─────────────┐    请求    ┌─────────────┐    更新    ┌─────────────┐
│             │ ────────→ │             │ ────────→ │             │
│    View     │           │ Controller  │           │    Model    │
│  （视图层）  │ ←──────── │  （控制层）  │ ←──────── │  （模型层）  │
│             │   响应    │             │   数据    │             │
└─────────────┘           └─────────────┘           └─────────────┘
```

#### 各层职责：

**Model（模型层）**
- 负责数据的处理和业务逻辑
- 封装应用程序的数据和状态
- 与数据库交互，执行业务规则

**View（视图层）**
- 负责数据的展示
- 用户界面的呈现
- 不包含业务逻辑

**Controller（控制层）**
- 处理用户输入
- 协调Model和View
- 决定如何处理请求

### 1.1.2 Spring Web MVC的优势

✅ **松耦合设计** - 各层职责分离，易于维护
✅ **可重用性** - 组件可以独立开发和测试
✅ **易于测试** - 支持单元测试和集成测试
✅ **灵活性** - 可以轻松更换视图技术

## 1.2 Spring Web架构详解

### 1.2.1 核心组件介绍

```
客户端请求
    ↓
┌─────────────────────────────────────────────┐
│              DispatcherServlet              │ ← 前端控制器
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│              HandlerMapping                 │ ← 处理器映射
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│              Controller                     │ ← 控制器
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│              ModelAndView                   │ ← 模型视图
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│              ViewResolver                   │ ← 视图解析器
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│                  View                       │ ← 视图
└─────────────────────────────────────────────┘
    ↓
客户端响应
```

### 1.2.2 主要组件详解

#### 1. DispatcherServlet（前端控制器）
- Spring Web的入口点
- 负责接收所有HTTP请求
- 协调其他组件完成请求处理

```java
// 配置示例
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }
}
```

#### 2. HandlerMapping（处理器映射）
- 将请求URL映射到具体的处理器方法
- 常见实现：RequestMappingHandlerMapping

#### 3. Controller（控制器）
- 包含处理请求的业务逻辑
- 返回ModelAndView或直接返回数据

#### 4. ModelAndView（模型视图）
- 包含模型数据和视图信息
- 控制器处理结果的封装

#### 5. ViewResolver（视图解析器）
- 解析逻辑视图名称为实际视图对象
- 常见实现：InternalResourceViewResolver

#### 6. View（视图）
- 负责渲染最终的响应内容
- 如JSP、Thymeleaf模板等

## 1.3 HTTP请求处理流程

### 1.3.1 完整处理流程

```
1. 客户端发送HTTP请求
   ↓
2. DispatcherServlet接收请求
   ↓
3. HandlerMapping查找匹配的处理器
   ↓
4. 执行拦截器preHandle方法
   ↓
5. 调用Controller处理方法
   ↓
6. 执行拦截器postHandle方法
   ↓
7. ViewResolver解析视图名称
   ↓
8. 渲染视图生成响应
   ↓
9. 执行拦截器afterCompletion方法
   ↓
10. 返回响应给客户端
```

### 1.3.2 流程示例代码

```java
@Controller
public class UserController {
    
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        // 1. 处理业务逻辑
        User user = userService.findById(id);
        
        // 2. 添加数据到模型
        model.addAttribute("user", user);
        
        // 3. 返回视图名称
        return "user/detail";
    }
}
```

## 1.4 Spring Web注解详解

### 1.4.1 控制器相关注解

#### @Controller
标记一个类为Spring MVC控制器

```java
@Controller
public class HomeController {
    // 控制器方法
}
```

#### @RestController
@RestController = @Controller + @ResponseBody
用于构建RESTful API

```java
@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }
}
```

#### @RequestMapping
映射HTTP请求到处理方法

```java
@Controller
@RequestMapping("/products")
public class ProductController {
    
    // GET /products
    @RequestMapping(method = RequestMethod.GET)
    public String list() { }
    
    // POST /products
    @RequestMapping(method = RequestMethod.POST)
    public String create() { }
}
```

### 1.4.2 HTTP方法注解

```java
@GetMapping      // 对应GET请求
@PostMapping     // 对应POST请求
@PutMapping      // 对应PUT请求
@DeleteMapping   // 对应DELETE请求
@PatchMapping    // 对应PATCH请求
```

### 1.4.3 参数绑定注解

#### @RequestParam
绑定请求参数

```java
@GetMapping("/search")
public String search(@RequestParam String keyword,
                    @RequestParam(defaultValue = "1") int page) {
    // 处理搜索逻辑
}
```

#### @PathVariable
绑定URL路径变量

```java
@GetMapping("/users/{id}")
public String getUser(@PathVariable Long id) {
    // 根据ID获取用户
}
```

#### @RequestBody
绑定请求体数据

```java
@PostMapping("/users")
public String createUser(@RequestBody User user) {
    // 创建用户
}
```

#### @RequestHeader
绑定请求头

```java
@GetMapping("/info")
public String getInfo(@RequestHeader("User-Agent") String userAgent) {
    // 获取浏览器信息
}
```

## 1.5 配置方式对比

### 1.5.1 XML配置（传统方式）

```xml
<!-- web.xml -->
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

```xml
<!-- dispatcher-servlet.xml -->
<context:component-scan base-package="com.example.controller"/>
<mvc:annotation-driven/>
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

### 1.5.2 Java配置（推荐方式）

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.controller")
public class WebConfig implements WebMvcConfigurer {
    
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}
```

### 1.5.3 Spring Boot自动配置（最简方式）

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot!";
    }
}
```

## 1.6 最佳实践建议

### 1.6.1 控制器设计原则

✅ **单一职责** - 一个控制器处理一类相关的请求
✅ **清晰命名** - 使用有意义的方法名和URL路径
✅ **统一返回格式** - REST API应该有统一的响应结构
✅ **适当的分层** - 控制器只负责请求转发，业务逻辑放在Service层

### 1.6.2 异常处理

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

## 🔧 本章小结

本章我们学习了：
- MVC架构模式的核心概念
- Spring Web MVC的工作原理和组件
- HTTP请求的完整处理流程
- 常用注解的使用方法
- 不同配置方式的特点

## 📚 下一步

下一章我们将学习如何搭建开发环境并创建第一个Spring Web应用！

---

**💡 思考题：**
1. 为什么需要DispatcherServlet作为前端控制器？
2. MVC模式相比传统JSP/Servlet开发有什么优势？
3. @Controller和@RestController的区别是什么？