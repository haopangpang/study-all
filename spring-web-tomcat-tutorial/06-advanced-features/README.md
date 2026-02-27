# 第6章：Spring Web高级特性

## 🎯 本章学习目标

- 掌握拦截器和过滤器的使用
- 学会文件上传下载功能实现
- 理解异常处理和全局异常捕获
- 掌握数据验证和国际化配置
- 学会异步处理和定时任务
- 理解跨域资源共享(CORS)配置

## 6.1 拦截器(Interceptor)详解

### 6.1.1 拦截器基础概念

#### 拦截器执行流程：
```
客户端请求
    ↓
DispatcherServlet
    ↓
HandlerInterceptor.preHandle()  ← 执行前拦截
    ↓
Controller处理
    ↓
HandlerInterceptor.postHandle() ← 执行后拦截  
    ↓
视图渲染
    ↓
HandlerInterceptor.afterCompletion() ← 完成后拦截
    ↓
响应返回客户端
```

### 6.1.2 自定义拦截器实现

```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        // 请求处理前执行
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        
        logger.info("请求开始: {} {} from {}", 
                   request.getMethod(), 
                   request.getRequestURI(), 
                   request.getRemoteAddr());
        
        // 返回true继续执行，返回false中断执行
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, 
                          HttpServletResponse response, 
                          Object handler, 
                          ModelAndView modelAndView) throws Exception {
        // 请求处理后，视图渲染前执行
        if (modelAndView != null) {
            modelAndView.addObject("serverTime", new Date());
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                              HttpServletResponse response, 
                              Object handler, 
                              Exception ex) throws Exception {
        // 视图渲染完成后执行
        Long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        
        logger.info("请求结束: {} {} 耗时: {}ms", 
                   request.getMethod(), 
                   request.getRequestURI(), 
                   (endTime - startTime));
        
        if (ex != null) {
            logger.error("请求处理异常: ", ex);
        }
    }
}
```

### 6.1.3 权限验证拦截器

```java
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private UserService userService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // 检查是否需要登录
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
        
        if (requireLogin == null) {
            requireLogin = handlerMethod.getBeanType().getAnnotation(RequireLogin.class);
        }
        
        if (requireLogin != null) {
            // 验证登录状态
            String token = request.getHeader("Authorization");
            if (token == null || !userService.validateToken(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"error\":\"未授权访问\"}");
                return false;
            }
        }
        
        return true;
    }
}

// 自定义注解
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireLogin {
}
```

### 6.1.4 拦截器配置注册

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LoggingInterceptor loggingInterceptor;
    
    @Autowired
    private AuthenticationInterceptor authInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册日志拦截器
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")                    // 拦截所有路径
                .excludePathPatterns("/static/**",         // 排除静态资源
                                   "/error",
                                   "/health");
        
        // 注册权限拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")                // 拦截API请求
                .excludePathPatterns("/api/auth/login",    // 排除登录接口
                                   "/api/auth/register");
    }
}
```

## 6.2 过滤器(Filter)详解

### 6.2.1 过滤器与拦截器区别

| 特性 | Filter(过滤器) | Interceptor(拦截器) |
|------|----------------|---------------------|
| 实现机制 | Servlet规范 | Spring框架 |
| 执行时机 | 请求进入容器前 | DispatcherServlet之后 |
| 作用范围 | 所有请求 | Spring MVC请求 |
| 配置方式 | web.xml或注解 | Java配置类 |

### 6.2.2 自定义过滤器实现

```java
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    public void doFilter(ServletRequest request, 
                        ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 请求前处理
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        
        logger.info("请求ID: {} - {} {} from {}", 
                   requestId, 
                   httpRequest.getMethod(), 
                   httpRequest.getRequestURI(), 
                   httpRequest.getRemoteAddr());
        
        // 包装响应以便记录响应信息
        ContentCachingResponseWrapper responseWrapper = 
            new ContentCachingResponseWrapper(httpResponse);
        
        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            // 响应后处理
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();
            
            logger.info("响应ID: {} - Status: {} Duration: {}ms", 
                       requestId, status, duration);
            
            // 复制响应内容
            responseWrapper.copyBodyToResponse();
        }
    }
}
```

### 6.2.3 CORS跨域过滤器

```java
@Component
@Order(2)
public class CorsFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, 
                        ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 设置CORS头部
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Access-Control-Allow-Headers", 
                             "Content-Type, Authorization, X-Requested-With");
        
        // 处理预检请求
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }
}
```

## 6.3 文件上传下载功能

### 6.3.1 文件上传配置

#### application.yml配置：
```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 50MB
      file-size-threshold: 2KB
      location: /tmp/uploads
```

#### 文件上传控制器：
```java
@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(createError("文件不能为空"));
        }
        
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            
            // 保存文件
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath);
            
            // 保存文件信息到数据库
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginalName(originalFilename);
            fileInfo.setStoredName(newFilename);
            fileInfo.setSize(file.getSize());
            fileInfo.setContentType(file.getContentType());
            fileInfo.setDescription(description);
            fileInfo.setUploadTime(LocalDateTime.now());
            
            fileInfoService.save(fileInfo);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("fileId", fileInfo.getId());
            result.put("filename", newFilename);
            result.put("size", file.getSize());
            
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createError("文件上传失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            FileInfo fileInfo = fileInfoService.findById(fileId);
            Path filePath = Paths.get(uploadPath).resolve(fileInfo.getStoredName());
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                               "attachment; filename=\"" + fileInfo.getOriginalName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    private Map<String, Object> createError(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
}
```

### 6.3.2 多文件上传

```java
@PostMapping("/upload/multiple")
public ResponseEntity<List<Map<String, Object>>> uploadMultipleFiles(
        @RequestParam("files") MultipartFile[] files) {
    
    List<Map<String, Object>> results = new ArrayList<>();
    
    for (MultipartFile file : files) {
        if (!file.isEmpty()) {
            try {
                String filename = saveFile(file);
                Map<String, Object> result = new HashMap<>();
                result.put("filename", filename);
                result.put("size", file.getSize());
                result.put("success", true);
                results.add(result);
            } catch (IOException e) {
                Map<String, Object> result = new HashMap<>();
                result.put("filename", file.getOriginalFilename());
                result.put("error", e.getMessage());
                result.put("success", false);
                results.add(result);
            }
        }
    }
    
    return ResponseEntity.ok(results);
}
```

## 6.4 全局异常处理

### 6.4.1 统一异常处理器

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        logger.warn("业务异常: ", ex);
        ErrorResponse error = new ErrorResponse(400, ex.getMessage(), ex.getCode());
        return ResponseEntity.badRequest().body(error);
    }
    
    // 处理参数验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        
        ErrorResponse error = new ErrorResponse(400, "参数验证失败", errors);
        return ResponseEntity.badRequest().body(error);
    }
    
    // 处理文件上传异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(
            MaxUploadSizeExceededException ex) {
        
        ErrorResponse error = new ErrorResponse(413, "文件大小超出限制");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }
    
    // 处理资源未找到异常
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // 处理通用异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("系统异常: ", ex);
        ErrorResponse error = new ErrorResponse(500, "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// 错误响应实体
public class ErrorResponse {
    private int code;
    private String message;
    private Object details;
    private long timestamp;
    
    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ErrorResponse(int code, String message, Object details) {
        this(code, message);
        this.details = details;
    }
    
    // getters and setters...
}
```

### 6.4.2 自定义业务异常

```java
// 基础业务异常
public class BusinessException extends RuntimeException {
    private String code;
    
    public BusinessException(String message) {
        super(message);
        this.code = "BIZ_ERROR";
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    // getters...
}

// 具体业务异常
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "用户不存在: " + userId);
    }
}

public class InsufficientPermissionException extends BusinessException {
    public InsufficientPermissionException() {
        super("INSUFFICIENT_PERMISSION", "权限不足");
    }
}

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceType, Long id) {
        super("RESOURCE_NOT_FOUND", resourceType + "不存在: " + id);
    }
}
```

## 6.5 数据验证和国际化

### 6.5.1 Bean Validation

```java
public class UserCreateRequest {
    
    @NotBlank(message = "{user.username.notblank}")
    @Size(min = 3, max = 20, message = "{user.username.size}")
    private String username;
    
    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    private String email;
    
    @NotNull(message = "{user.age.notnull}")
    @Min(value = 18, message = "{user.age.min}")
    @Max(value = 120, message = "{user.age.max}")
    private Integer age;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "{user.phone.pattern}")
    private String phone;
    
    @Valid  // 嵌套验证
    private Address address;
    
    // getters and setters...
}

public class Address {
    @NotBlank(message = "{address.street.notblank}")
    private String street;
    
    @NotBlank(message = "{address.city.notblank}")
    private String city;
    
    // getters and setters...
}
```

### 6.5.2 国际化配置

#### messages.properties（默认语言）：
```properties
# 用户相关
user.username.notblank=用户名不能为空
user.username.size=用户名长度必须在{min}到{max}之间
user.email.notblank=邮箱不能为空
user.email.invalid=邮箱格式不正确
user.age.notnull=年龄不能为空
user.age.min=年龄不能小于{value}岁
user.age.max=年龄不能大于{value}岁
user.phone.pattern=手机号格式不正确

# 地址相关
address.street.notblank=街道地址不能为空
address.city.notblank=城市不能为空

# 通用消息
success.operation=操作成功
error.operation=操作失败
```

#### messages_en.properties（英文）：
```properties
# User related
user.username.notblank=Username cannot be blank
user.username.size=Username length must be between {min} and {max}
user.email.notblank=Email cannot be blank
user.email.invalid=Invalid email format
user.age.notnull=Age cannot be null
user.age.min=Age cannot be less than {value}
user.age.max=Age cannot be greater than {value}
user.phone.pattern=Invalid phone number format

# Address related
address.street.notblank=Street address cannot be blank
address.city.notblank=City cannot be blank

# Common messages
success.operation=Operation successful
error.operation=Operation failed
```

#### 配置类：
```java
@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {
    
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.CHINA);
        return localeResolver;
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = 
            new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // 缓存1小时
        return messageSource;
    }
    
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }
}
```

## 6.6 异步处理和定时任务

### 6.6.1 异步控制器

```java
@RestController
@RequestMapping("/api/async")
public class AsyncController {
    
    @Autowired
    private AsyncService asyncService;
    
    @GetMapping("/process")
    public CompletableFuture<ResponseEntity<String>> asyncProcess() {
        return asyncService.processData()
                .thenApply(result -> ResponseEntity.ok("处理完成: " + result))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                         .body("处理失败: " + ex.getMessage()));
    }
    
    @PostMapping("/batch")
    public ResponseEntity<String> batchProcess(@RequestBody List<Long> ids) {
        // 异步执行批量处理
        asyncService.batchProcess(ids);
        return ResponseEntity.accepted().body("批量处理已启动");
    }
}

@Service
public class AsyncService {
    
    @Async
    public CompletableFuture<String> processData() {
        try {
            // 模拟耗时操作
            Thread.sleep(5000);
            return CompletableFuture.completedFuture("数据处理完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Async
    public void batchProcess(List<Long> ids) {
        ids.parallelStream().forEach(this::processSingleItem);
    }
    
    private void processSingleItem(Long id) {
        // 处理单个项目
        System.out.println("处理项目: " + id);
    }
}

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
```

### 6.6.2 定时任务

```java
@Component
public class ScheduledTasks {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
    @Scheduled(fixedRate = 30000)  // 每30秒执行一次
    public void reportCurrentTime() {
        logger.info("当前时间: {}", new Date());
    }
    
    @Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点执行
    public void dailyCleanup() {
        logger.info("执行每日清理任务");
        // 清理过期数据
    }
    
    @Scheduled(initialDelay = 5000, fixedDelay = 60000)  // 延迟5秒后首次执行，之后每隔60秒执行
    public void periodicCheck() {
        logger.info("执行周期性检查");
        // 执行健康检查
    }
}

@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("Scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        return scheduler;
    }
}
```

## 🔧 本章小结

本章我们学习了：
- ✅ 拦截器和过滤器的实现与应用
- ✅ 文件上传下载功能开发
- ✅ 全局异常处理机制
- ✅ 数据验证和国际化配置
- ✅ 异步处理和定时任务
- ✅ 跨域资源共享配置

## 🚀 下一步

下一章我们将构建一个完整的实战项目，综合运用所学知识！

---

**💡 练习作业：**
1. 实现一个完整的权限管理系统拦截器
2. 开发文件管理功能（上传、下载、删除）
3. 配置全局异常处理和统一响应格式
4. 实现多语言支持功能
5. 添加异步邮件发送功能