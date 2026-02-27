# 第4章：控制器与路由详解

## 🎯 本章学习目标

- 深入理解@Controller和@RestController的区别
- 掌握各种HTTP请求映射注解的使用
- 学会灵活的URL路径匹配规则
- 理解请求参数绑定机制
- 掌握控制器方法返回值处理

## 4.1 控制器基础概念

### 4.1.1 @Controller vs @RestController

#### @Controller 特点：
```java
@Controller
public class ViewController {
    // 返回视图名称，用于页面渲染
    @GetMapping("/page")
    public String showPage(Model model) {
        model.addAttribute("data", "页面数据");
        return "mypage"; // 返回视图名
    }
}
```

#### @RestController 特点：
```java
@RestController
public class ApiController {
    // 直接返回数据，自动序列化为JSON/XML
    @GetMapping("/api/data")
    public Map<String, Object> getData() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "API数据");
        return result; // 直接返回数据
    }
}
```

#### 关系图解：
```
@Controller                    @RestController
     │                              │
     │                              │
     ▼                              ▼
返回视图名 ──→ ViewResolver ──→ 渲染页面    直接返回数据 ──→ HttpMessageConverter ──→ JSON/XML
```

### 4.1.2 控制器生命周期

```java
@Controller
public class LifecycleController {
    
    public LifecycleController() {
        System.out.println("1. 控制器实例化");
    }
    
    @PostConstruct
    public void init() {
        System.out.println("2. 控制器初始化完成");
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("3. 控制器销毁前");
    }
}
```

## 4.2 HTTP请求映射详解

### 4.2.1 基础映射注解

#### 方法级别映射：
```java
@Controller
@RequestMapping("/users")  // 类级别的基础路径
public class UserController {
    
    @GetMapping                    // GET /users
    public String listUsers() { }
    
    @GetMapping("/{id}")          // GET /users/123
    public String getUser(@PathVariable Long id) { }
    
    @PostMapping                  // POST /users
    public String createUser() { }
    
    @PutMapping("/{id}")          // PUT /users/123
    public String updateUser(@PathVariable Long id) { }
    
    @DeleteMapping("/{id}")       // DELETE /users/123
    public String deleteUser(@PathVariable Long id) { }
}
```

#### 组合注解简化：
```java
// 等价于 @RequestMapping(method = RequestMethod.GET)
@GetMapping("/users")

// 等价于 @RequestMapping(method = RequestMethod.POST)  
@PostMapping("/users")

// 等价于 @RequestMapping(method = RequestMethod.PUT)
@PutMapping("/users/{id}")

// 等价于 @RequestMapping(method = RequestMethod.DELETE)
@DeleteMapping("/users/{id}")
```

### 4.2.2 URL路径匹配规则

#### 1. 精确匹配：
```java
@GetMapping("/users/profile")     // 只匹配 /users/profile
```

#### 2. 路径变量匹配：
```java
@GetMapping("/users/{id}")        // 匹配 /users/123, /users/abc
@GetMapping("/users/{userId}/orders/{orderId}")  // 多个路径变量
```

#### 3. 通配符匹配：
```java
@GetMapping("/api/**")            // 匹配 /api/下的所有路径
@GetMapping("/files/*.pdf")       // 匹配.pdf文件
@GetMapping("/docs/?")            // ?匹配单个字符
```

#### 4. 正则表达式匹配：
```java
@GetMapping("/users/{id:\\d+}")   // 只匹配数字ID
@GetMapping("/files/{name:.+\\.txt}")  // 匹配.txt文件
```

### 4.2.3 多个URL映射

```java
// 一个方法处理多个URL
@GetMapping({"/", "/index", "/home"})
public String home() {
    return "home";
}

// 一个方法处理多种HTTP方法
@RequestMapping(value = "/resource", method = {RequestMethod.GET, RequestMethod.POST})
public String handleResource() {
    return "resource";
}
```

## 4.3 请求参数绑定

### 4.3.1 @RequestParam - 请求参数绑定

#### 基础用法：
```java
@GetMapping("/search")
public String search(
    @RequestParam("keyword") String keyword,           // 必需参数
    @RequestParam(value = "page", defaultValue = "1") int page,  // 可选参数
    @RequestParam(value = "size", required = false) Integer size  // 非必需参数
) {
    // 处理搜索逻辑
    return "search-results";
}
```

#### 参数类型转换：
```java
@GetMapping("/filter")
public String filter(
    @RequestParam Boolean active,           // 自动转换 "true"/"false"
    @RequestParam Integer categoryId,       // 自动转换数字
    @RequestParam LocalDate date,           // 需要合适的转换器
    @RequestParam String[] tags             // 数组参数
) {
    return "filtered-results";
}
```

#### 集合参数绑定：
```java
@GetMapping("/batch")
public String batchProcess(
    @RequestParam List<Long> ids,           // ?ids=1&ids=2&ids=3
    @RequestParam Set<String> categories    // ?categories=A&categories=B
) {
    return "batch-result";
}
```

### 4.3.2 @PathVariable - 路径变量绑定

#### 单个路径变量：
```java
@GetMapping("/users/{id}")
public String getUser(@PathVariable Long id) {
    return "user-detail";
}
```

#### 多个路径变量：
```java
@GetMapping("/users/{userId}/orders/{orderId}")
public String getOrder(
    @PathVariable Long userId,
    @PathVariable("orderId") Long orderId  // 指定参数名
) {
    return "order-detail";
}
```

#### 路径变量验证：
```java
@GetMapping("/files/{filename:.+}")  // 匹配带扩展名的文件名
public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
    // 文件下载逻辑
}
```

### 4.3.3 @RequestBody - 请求体绑定

#### JSON数据绑定：
```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    // user对象会自动从JSON反序列化
    User savedUser = userService.save(user);
    return ResponseEntity.ok(savedUser);
}
```

#### 复杂对象绑定：
```java
@PostMapping("/orders")
public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
    // 处理复杂的订单创建请求
    return orderService.processOrder(request);
}
```

### 4.3.4 @RequestHeader - 请求头绑定

```java
@GetMapping("/info")
public String getClientInfo(
    @RequestHeader("User-Agent") String userAgent,
    @RequestHeader(value = "Authorization", required = false) String auth,
    @RequestHeader Map<String, String> allHeaders  // 获取所有请求头
) {
    return "client-info";
}
```

### 4.3.5 @CookieValue - Cookie绑定

```java
@GetMapping("/profile")
public String getUserProfile(@CookieValue("sessionId") String sessionId) {
    // 根据session ID获取用户信息
    return "user-profile";
}
```

## 4.4 控制器方法返回值处理

### 4.4.1 视图相关返回值

#### 1. 字符串返回值（视图名）：
```java
@Controller
public class PageController {
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "仪表板");
        return "dashboard";  // 返回视图名称
    }
    
    @GetMapping("/redirect-example")
    public String redirectExample() {
        return "redirect:/success";  // 重定向
    }
    
    @GetMapping("/forward-example")  
    public String forwardExample() {
        return "forward:/process";   // 转发
    }
}
```

#### 2. ModelAndView对象：
```java
@GetMapping("/complex-page")
public ModelAndView complexPage() {
    ModelAndView mav = new ModelAndView("complex-view");
    mav.addObject("data", fetchData());
    mav.addObject("metadata", getMetadata());
    return mav;
}
```

### 4.4.2 数据相关返回值

#### 1. 直接返回数据（@ResponseBody）：
```java
@RestController
public class DataController {
    
    @GetMapping("/api/users")
    public List<User> getUsers() {
        return userService.findAll();  // 自动序列化为JSON
    }
    
    @GetMapping("/api/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("online", true);
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
}
```

#### 2. ResponseEntity包装：
```java
@GetMapping("/api/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    if (user != null) {
        return ResponseEntity.ok(user);  // 200 OK
    } else {
        return ResponseEntity.notFound().build();  // 404 Not Found
    }
}

@PostMapping("/api/users")
public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    User savedUser = userService.save(user);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedUser.getId())
        .toUri();
    return ResponseEntity.created(location).body(savedUser);  // 201 Created
}
```

### 4.4.3 文件下载返回值

```java
@GetMapping("/download/{filename}")
public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
    Resource file = storageService.loadAsResource(filename);
    
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
}
```

## 4.5 高级路由配置

### 4.5.1 路径变量正则表达式

```java
@RestController
@RequestMapping("/api")
public class AdvancedController {
    
    // 只匹配数字ID
    @GetMapping("/users/{id:[0-9]+}")
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    // 匹配UUID格式
    @GetMapping("/sessions/{uuid:[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}}")
    public Session getSession(@PathVariable String uuid) {
        return sessionService.findByUuid(uuid);
    }
    
    // 匹配特定文件扩展名
    @GetMapping("/documents/{name:.+\\.(pdf|doc|docx)}")
    public Document getDocument(@PathVariable String name) {
        return documentService.findByName(name);
    }
}
```

### 4.5.2 条件映射

```java
@RestController
public class ConditionalController {
    
    // 根据请求参数条件映射
    @GetMapping(value = "/data", params = "format=json")
    public List<Data> getJsonData() {
        return dataService.getJsonFormat();
    }
    
    @GetMapping(value = "/data", params = "format=xml")  
    public List<Data> getXmlData() {
        return dataService.getXmlFormat();
    }
    
    // 根据请求头条件映射
    @GetMapping(value = "/content", headers = "Accept=application/json")
    public Content getJsonContent() {
        return contentService.getJson();
    }
    
    @GetMapping(value = "/content", headers = "Accept=text/xml")
    public Content getXmlContent() {
        return contentService.getXml();
    }
    
    // 根据Content-Type条件映射
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return "file uploaded";
    }
    
    @PostMapping(value = "/upload", consumes = "application/json")
    public String uploadJson(@RequestBody UploadRequest request) {
        return "json uploaded";
    }
}
```

### 4.5.3 分组路由配置

```java
@Configuration
public class RouteConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 简单的页面跳转配置
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/404").setViewName("error/404");
    }
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 配置路径匹配规则
        configurer
            .setUseTrailingSlashMatch(true)    // /users 和 /users/ 视为相同
            .setUseSuffixPatternMatch(false);  // 禁用后缀匹配
    }
}
```

## 4.6 控制器最佳实践

### 4.6.1 命名规范

```java
// ✅ 推荐的命名方式
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) { }
    
    @PostMapping
    public CreateUserResponse createUser(@Valid @RequestBody CreateUserRequest request) { }
}

// ❌ 不推荐的命名方式
@Controller
@RequestMapping("/userController")
public class UserCtrl {
    
    @RequestMapping("/getUserById")
    public String getUser(Long id) { }
}
```

### 4.6.2 参数验证

```java
@RestController
@RequestMapping("/api/users")
public class ValidatedUserController {
    
    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequest request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        User user = userService.create(request);
        return ResponseEntity.ok(user);
    }
}

// DTO类
public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Min(value = 18, message = "年龄不能小于18岁")
    private Integer age;
    
    // getters and setters...
}
```

### 4.6.3 异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        
        ErrorResponse error = new ErrorResponse(400, "参数验证失败", errors);
        return ResponseEntity.badRequest().body(error);
    }
}
```

## 🔧 本章小结

本章我们深入学习了：
- ✅ 控制器的基础概念和不同类型
- ✅ 各种HTTP请求映射注解的使用
- ✅ 灵活的URL路径匹配规则
- ✅ 多种参数绑定机制
- ✅ 控制器方法返回值处理
- ✅ 高级路由配置技巧
- ✅ 控制器开发最佳实践

## 🚀 下一步

下一章我们将学习请求处理的更多细节，包括数据绑定、验证和异常处理！

---

**💡 练习作业：**
1. 创建一个博客系统的控制器，包含文章的增删改查功能
2. 实现RESTful风格的API接口
3. 添加参数验证和全局异常处理
4. 设计合理的URL路径结构