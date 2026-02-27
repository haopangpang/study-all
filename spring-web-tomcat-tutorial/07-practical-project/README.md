# 第7章：员工管理系统实战项目

## 🎯 项目概述

本章将构建一个完整的员工管理系统，综合运用前面学到的所有知识点，包括：
- Spring Boot + Spring Web MVC
- Tomcat部署和配置
- 数据库集成（MySQL + JPA）
- 前后端分离架构
- 安全认证和权限控制
- 文件上传和报表导出
- 异常处理和日志记录

## 7.1 项目需求分析

### 7.1.1 功能需求

**核心功能模块：**
1. ✅ 员工信息管理（增删改查）
2. ✅ 部门组织架构管理
3. ✅ 职位和薪酬管理
4. ✅ 考勤打卡系统
5. ✅ 请假申请审批流程
6. ✅ 工资条查询和导出
7. ✅ 系统管理和权限控制

**非功能性需求：**
- 响应时间 < 2秒
- 支持1000+并发用户
- 数据安全性保障
- 完善的日志记录
- 友好的用户界面

### 7.1.2 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                         前端层                              │
│  Vue.js + Element UI + Axios                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      API网关层                              │
│  Nginx负载均衡 + SSL终止                                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      应用服务层                             │
│  Spring Boot + Spring Security + Spring Data JPA           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      数据存储层                             │
│  MySQL主从 + Redis缓存 + 文件存储                           │
└─────────────────────────────────────────────────────────────┘
```

## 7.2 项目结构设计

### 7.2.1 目录结构

```
employee-management-system/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── src/
│   └── main/
│       ├── java/
│       │   └── com/company/hr/
│       │       ├── EmployeeManagementApplication.java
│       │       ├── config/
│       │       │   ├── SecurityConfig.java
│       │       │   ├── DatabaseConfig.java
│       │       │   ├── WebConfig.java
│       │       │   └── SwaggerConfig.java
│       │       ├── controller/
│       │       │   ├── EmployeeController.java
│       │       │   ├── DepartmentController.java
│       │       │   ├── AttendanceController.java
│       │       │   ├── LeaveController.java
│       │       │   └── AuthController.java
│       │       ├── service/
│       │       │   ├── impl/
│       │       │   │   ├── EmployeeServiceImpl.java
│       │       │   │   ├── DepartmentServiceImpl.java
│       │       │   │   └── AuthServiceImpl.java
│       │       │   ├── EmployeeService.java
│       │       │   ├── DepartmentService.java
│       │       │   └── AuthService.java
│       │       ├── repository/
│       │       │   ├── EmployeeRepository.java
│       │       │   ├── DepartmentRepository.java
│       │       │   └── AttendanceRepository.java
│       │       ├── entity/
│       │       │   ├── Employee.java
│       │       │   ├── Department.java
│       │       │   ├── Attendance.java
│       │       │   └── LeaveApplication.java
│       │       ├── dto/
│       │       │   ├── request/
│       │       │   │   ├── EmployeeCreateRequest.java
│       │       │   │   └── LoginRequest.java
│       │       │   └── response/
│       │       │       ├── EmployeeResponse.java
│       │       │       └── ApiResponse.java
│       │       ├── exception/
│       │       │   ├── GlobalExceptionHandler.java
│       │       │   ├── BusinessException.java
│       │       │   └── ErrorCode.java
│       │       └── util/
│       │           ├── JwtUtil.java
│       │           ├── ExcelUtil.java
│       │           └── PasswordEncoder.java
│       └── resources/
│           ├── application.yml
│           ├── messages.properties
│           ├── static/
│           └── templates/
└── docs/
    ├── api-docs.md
    ├── database-schema.sql
    └── deployment-guide.md
```

### 7.2.2 核心配置文件

#### build.gradle依赖配置：
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.0'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
}

group = 'com.company'
version = '1.0.0'
sourceCompatibility = '11'

def springBootVersion = '2.7.0'

repositories {
    maven { url 'https://maven.aliyun.com/repository/public' }
    maven { url 'https://maven.aliyun.com/repository/spring' }
    mavenCentral()
}

dependencies {
    // Web框架
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // 安全框架
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    // 数据库相关
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'mysql:mysql-connector-java'
    
    // Redis缓存
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    
    // JWT支持
    implementation 'io.jsonwebtoken:jjwt:0.9.1'
    
    // 工具类
    implementation 'org.apache.commons:commons-lang3'
    
    // Excel处理
    implementation 'org.apache.poi:poi:5.2.2'
    implementation 'org.apache.poi:poi-ooxml:5.2.2'
    
    // API文档
    implementation 'io.springfox:springfox-swagger2:3.0.0'
    implementation 'io.springfox:springfox-swagger-ui:3.0.0'
    
    // 测试依赖
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
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

// 自定义任务
task copyDocs(type: Copy) {
    description = '复制文档到输出目录'
    group = 'documentation'
    
    from 'docs'
    into 'build/docs'
}

task runApp(type: JavaExec) {
    description = '运行应用'
    group = 'application'
    
    mainClass = 'com.company.hr.EmployeeManagementApplication'
    classpath = sourceSets.main.runtimeClasspath
}

// 构建配置
jar {
    enabled = false // 禁用普通jar构建
}

bootJar {
    archiveFileName = "${project.name}-${version}.jar"
}
```

#### application.yml配置：
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: employee-management-system
    
  datasource:
    url: jdbc:mysql://localhost:3306/hr_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: hr_user
    password: hr_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    jedis:
      pool:
        max-active: 20
        max-wait: -1ms
        max-idle: 10
        min-idle: 0
        
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000  # 24小时

logging:
  level:
    com.company.hr: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
  file:
    name: logs/hr-system.log
    max-size: 10MB
    max-history: 30
```

## 7.3 核心实体设计

### 7.3.1 员工实体

```java
@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String employeeNumber;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private LocalDate birthDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Enumerated(EnumType.STRING)
    private Position position;
    
    private BigDecimal salary;
    
    private LocalDate hireDate;
    
    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private String password;
    
    // 枚举定义
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public enum Position {
        INTERN, JUNIOR, SENIOR, MANAGER, DIRECTOR, VP, CEO
    }
    
    public enum EmploymentStatus {
        ACTIVE, INACTIVE, TERMINATED, ON_LEAVE
    }
    
    public enum Role {
        EMPLOYEE, MANAGER, HR_ADMIN, SYSTEM_ADMIN
    }
}
```

### 7.3.2 部门实体

```java
@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Department> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## 7.4 核心业务实现

### 7.4.1 安全认证模块

#### JWT工具类：
```java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());
        return generateToken(claims);
    }
    
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    private Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
```

#### 安全配置：
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**", "/swagger-ui/**", "/v2/api-docs").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

### 7.4.2 员工管理控制器

```java
@RestController
@RequestMapping("/employees")
@PreAuthorize("hasRole('HR_ADMIN') or hasRole('MANAGER')")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping
    @ApiOperation("获取员工列表")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(keyword, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(employees));
    }
    
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取员工详情")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }
    
    @PostMapping
    @ApiOperation("创建员工")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request) {
        
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employee));
    }
    
    @PutMapping("/{id}")
    @ApiOperation("更新员工信息")
    @PreAuthorize("hasRole('HR_ADMIN') or (hasRole('MANAGER') and @employeeService.isInSameDepartment(authentication.name, #id))")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        
        EmployeeResponse employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除员工")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @GetMapping("/export")
    @ApiOperation("导出员工信息")
    public void exportEmployees(HttpServletResponse response,
                               @RequestParam(required = false) String departmentCode) throws IOException {
        List<EmployeeExportDTO> employees = employeeService.exportEmployees(departmentCode);
        ExcelUtil.writeExcel(response, employees, "员工信息表", EmployeeExportDTO.class);
    }
}
```

### 7.4.3 考勤管理模块

```java
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    @PostMapping("/clock-in")
    @ApiOperation("上班打卡")
    public ResponseEntity<ApiResponse<AttendanceRecord>> clockIn(
            Authentication authentication) {
        
        String employeeNumber = authentication.getName();
        AttendanceRecord record = attendanceService.clockIn(employeeNumber);
        return ResponseEntity.ok(ApiResponse.success(record));
    }
    
    @PostMapping("/clock-out")
    @ApiOperation("下班打卡")
    public ResponseEntity<ApiResponse<AttendanceRecord>> clockOut(
            Authentication authentication) {
        
        String employeeNumber = authentication.getName();
        AttendanceRecord record = attendanceService.clockOut(employeeNumber);
        return ResponseEntity.ok(ApiResponse.success(record));
    }
    
    @GetMapping("/records")
    @ApiOperation("获取考勤记录")
    public ResponseEntity<ApiResponse<Page<AttendanceRecord>>> getAttendanceRecords(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String employeeNumber = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceRecord> records = attendanceService.getAttendanceRecords(
                employeeNumber, startDate, endDate, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @GetMapping("/statistics")
    @ApiOperation("获取考勤统计")
    public ResponseEntity<ApiResponse<AttendanceStatistics>> getAttendanceStatistics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth month) {
        
        String employeeNumber = authentication.getName();
        AttendanceStatistics statistics = attendanceService.getAttendanceStatistics(
                employeeNumber, month);
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}
```

## 7.5 前端页面示例

### 7.5.1 登录页面

```vue
<template>
  <div class="login-container">
    <el-card class="login-card">
      <div slot="header" class="login-header">
        <h2>员工管理系统</h2>
      </div>
      
      <el-form 
        :model="loginForm" 
        :rules="rules" 
        ref="loginForm" 
        class="login-form">
        
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="工号"
            prefix-icon="el-icon-user">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
            @keyup.enter.native="handleLogin">
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading"
            class="login-button">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入工号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  
  methods: {
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const response = await this.$http.post('/auth/login', this.loginForm)
            const { token, user } = response.data.data
            
            // 保存token和用户信息
            localStorage.setItem('token', token)
            localStorage.setItem('user', JSON.stringify(user))
            
            this.$message.success('登录成功')
            this.$router.push('/dashboard')
          } catch (error) {
            this.$message.error(error.response?.data?.message || '登录失败')
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>
```

### 7.5.2 员工列表页面

```vue
<template>
  <div class="employee-list">
    <el-card>
      <div slot="header" class="clearfix">
        <span>员工管理</span>
        <el-button 
          style="float: right;" 
          type="primary" 
          @click="showAddDialog">
          新增员工
        </el-button>
      </div>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/工号"></el-input>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="searchForm.departmentId" clearable>
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchEmployees">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格 -->
      <el-table :data="employees" style="width: 100%" v-loading="loading">
        <el-table-column prop="employeeNumber" label="工号" width="120"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="department.name" label="部门"></el-table-column>
        <el-table-column prop="position" label="职位"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="hireDate" label="入职日期" width="120">
          <template slot-scope="scope">
            {{ scope.row.hireDate | dateFormat }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="viewEmployee(scope.row)">查看</el-button>
            <el-button size="mini" @click="editEmployee(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="deleteEmployee(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 50]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      employees: [],
      departments: [],
      searchForm: {
        keyword: '',
        departmentId: null
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  
  mounted() {
    this.loadEmployees()
    this.loadDepartments()
  },
  
  methods: {
    async loadEmployees() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.currentPage - 1,
          size: this.pagination.pageSize,
          ...this.searchForm
        }
        
        const response = await this.$http.get('/employees', { params })
        this.employees = response.data.data.content
        this.pagination.total = response.data.data.totalElements
      } catch (error) {
        this.$message.error('加载员工列表失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadDepartments() {
      try {
        const response = await this.$http.get('/departments')
        this.departments = response.data.data
      } catch (error) {
        console.error('加载部门列表失败')
      }
    },
    
    searchEmployees() {
      this.pagination.currentPage = 1
      this.loadEmployees()
    },
    
    resetSearch() {
      this.searchForm = { keyword: '', departmentId: null }
      this.searchEmployees()
    },
    
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadEmployees()
    },
    
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadEmployees()
    }
  }
}
</script>
```

## 7.6 部署和运维

### 7.6.1 Docker部署配置

#### Dockerfile：
```dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

# 复制Gradle构建的JAR文件
COPY build/libs/employee-management-system-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml：
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: hr-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: hr_system
      MYSQL_USER: hr_user
      MYSQL_PASSWORD: hr_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - hr-network

  redis:
    image: redis:6-alpine
    container_name: hr-redis
    ports:
      - "6379:6379"
    networks:
      - hr-network

  app:
    build: .
    container_name: hr-app
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/hr_system
      - SPRING_REDIS_HOST=redis
    networks:
      - hr-network

networks:
  hr-network:
    driver: bridge

volumes:
  mysql_data:
```

#### Gradle构建Docker镜像：
```bash
# 1. 构建项目
./gradlew clean build

# 2. 构建Docker镜像
docker build -t hr-system:1.0.0 .

# 3. 运行容器
docker run -p 8080:8080 hr-system:1.0.0

# 4. 或者使用docker-compose
docker-compose up -d

# 5. 查看日志
docker-compose logs -f app
```

### 7.6.2 Nginx配置

```nginx
upstream hr_backend {
    server app:8080;
}

server {
    listen 80;
    server_name hr.company.com;
    
    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api/ {
        proxy_pass http://hr_backend/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # WebSocket支持（如果需要）
    location /websocket/ {
        proxy_pass http://hr_backend/websocket/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

## 🔧 项目总结

本实战项目涵盖了：
- ✅ 完整的企业级应用架构设计
- ✅ Spring Boot核心技术应用
- ✅ 前后端分离开发模式
- ✅ 安全认证和权限控制
- ✅ 数据库设计和ORM映射
- ✅ RESTful API设计规范
- ✅ 容器化部署方案
- ✅ 完善的异常处理和日志记录

## 🚀 后续优化方向

1. **性能优化**：引入缓存机制、数据库读写分离
2. **微服务改造**：拆分为员工服务、考勤服务等独立服务
3. **消息队列**：异步处理耗时操作
4. **监控告警**：集成Prometheus + Grafana
5. **CI/CD**：自动化构建和部署流水线

---

**🎉 恭喜完成Spring Web和Tomcat完整学习之旅！**

通过这个实战项目，你应该已经掌握了：
- Spring Web MVC框架的核心原理和应用
- Tomcat服务器的配置和部署
- 企业级Web应用的完整开发流程
- 前后端协作的开发模式
- 生产环境的部署和运维实践

继续深入学习，成为优秀的Java Web开发者！