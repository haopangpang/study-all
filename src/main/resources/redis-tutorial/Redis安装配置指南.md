# Redis安装和环境配置指南

## 🖥️ Windows环境安装

### 方法1：使用Chocolatey（推荐）
```powershell
# 安装Chocolatey（如果还没有安装）
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 安装Redis
choco install redis-64

# 启动Redis服务
redis-server
```

### 方法2：下载Windows版本
```powershell
# 1. 访问Microsoft Archive下载页面
# https://github.com/microsoftarchive/redis/releases

# 2. 下载Redis-x64-3.2.100.zip

# 3. 解压到指定目录
Expand-Archive -Path "Redis-x64-3.2.100.zip" -DestinationPath "C:\Redis"

# 4. 启动Redis
cd C:\Redis
redis-server.exe redis.windows.conf
```

### 方法3：使用Docker（最简单）
```powershell
# 安装Docker Desktop
# https://www.docker.com/products/docker-desktop

# 拉取并运行Redis容器
docker run -d -p 6379:6379 --name my-redis redis:latest

# 验证运行状态
docker ps
```

## 🐧 Linux环境安装

### Ubuntu/Debian系统
```bash
# 更新包管理器
sudo apt update

# 安装Redis
sudo apt install redis-server

# 启动Redis服务
sudo systemctl start redis-server
sudo systemctl enable redis-server

# 检查服务状态
sudo systemctl status redis-server
```

### CentOS/RHEL系统
```bash
# 安装EPEL仓库
sudo yum install epel-release

# 安装Redis
sudo yum install redis

# 启动Redis服务
sudo systemctl start redis
sudo systemctl enable redis

# 检查服务状态
sudo systemctl status redis
```

### 使用Docker（跨平台推荐）
```bash
# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 运行Redis容器
docker run -d -p 6379:6379 --name my-redis redis:latest

# 带持久化的运行
docker run -d -p 6379:6379 -v /mydata/redis/data:/data --name my-redis redis:latest redis-server --appendonly yes
```

## ☕ macOS环境安装

### 使用Homebrew（推荐）
```bash
# 安装Homebrew（如果没有）
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 安装Redis
brew install redis

# 启动Redis服务
brew services start redis

# 或者直接运行
redis-server /usr/local/etc/redis.conf
```

### 使用Docker
```bash
# 安装Docker Desktop for Mac
# https://www.docker.com/products/docker-desktop

# 运行Redis
docker run -d -p 6379:6379 --name my-redis redis:latest
```

## ⚙️ 基础配置

### redis.conf 配置文件示例
```conf
# 基础配置
bind 127.0.0.1                    # 绑定本地地址
port 6379                         # 监听端口
daemonize yes                     # 后台运行（Linux）
pidfile /var/run/redis_6379.pid   # PID文件路径
loglevel notice                   # 日志级别
logfile /var/log/redis.log        # 日志文件

# 内存配置
maxmemory 2gb                     # 最大内存使用
maxmemory-policy allkeys-lru      # 内存淘汰策略

# 持久化配置
save 900 1                        # 900秒内至少1个key变化则保存
save 300 10                       # 300秒内至少10个key变化则保存
save 60 10000                     # 60秒内至少10000个key变化则保存
dbfilename dump.rdb               # RDB文件名
dir /var/lib/redis                # 工作目录

# AOF持久化
appendonly yes                    # 开启AOF
appendfilename "appendonly.aof"   # AOF文件名
appendfsync everysec              # AOF同步频率

# 安全配置
requirepass your_strong_password  # 设置密码
rename-command FLUSHDB ""         # 禁用危险命令
rename-command FLUSHALL ""        # 禁用危险命令
```

## 🔧 Java开发环境配置

### Maven依赖配置
```xml
<dependencies>
    <!-- Jedis Redis客户端 -->
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>4.3.1</version>
    </dependency>
    
    <!-- 日志框架 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.32</version>
    </dependency>
    
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.6</version>
    </dependency>
</dependencies>
```

### Gradle依赖配置
```gradle
dependencies {
    // Jedis Redis客户端
    implementation 'redis.clients:jedis:4.3.1'
    
    // 日志框架
    implementation 'org.slf4j:slf4j-api:1.7.32'
    implementation 'ch.qos.logback:logback-classic:1.2.6'
}
```

## 🧪 环境验证

### 1. 检查Redis服务状态
```bash
# Windows
tasklist | findstr redis

# Linux/macOS
ps aux | grep redis
# 或者
sudo systemctl status redis

# Docker
docker ps | grep redis
```

### 2. 测试Redis连接
```bash
# 使用redis-cli
redis-cli ping
# 应该返回: PONG

# 测试基本命令
redis-cli
127.0.0.1:6379> SET test "Hello Redis"
127.0.0.1:6379> GET test
"Hello Redis"
```

### 3. 测试Java连接
```java
import redis.clients.jedis.Jedis;

public class RedisTest {
    public static void main(String[] args) {
        try {
            Jedis jedis = new Jedis("localhost", 6379);
            System.out.println("连接成功: " + jedis.ping());
            
            jedis.set("test", "Hello from Java");
            String value = jedis.get("test");
            System.out.println("获取值: " + value);
            
            jedis.close();
        } catch (Exception e) {
            System.err.println("连接失败: " + e.getMessage());
        }
    }
}
```

## 🔧 常见问题解决

### Q1: 端口被占用
```bash
# Windows查看端口占用
netstat -ano | findstr :6379

# Linux/macOS查看端口占用
lsof -i :6379

# 杀掉占用进程（Windows）
taskkill /PID <进程ID> /F

# 杀掉占用进程（Linux/macOS）
kill -9 <进程ID>
```

### Q2: 权限问题
```bash
# Linux/macOS给予权限
sudo chown redis:redis /var/lib/redis
sudo chmod 770 /var/lib/redis
```

### Q3: 内存不足
```bash
# 检查内存使用
redis-cli info memory

# 配置内存限制
# 在redis.conf中设置:
maxmemory 1gb
maxmemory-policy allkeys-lru
```

### Q4: 连接超时
```bash
# 检查防火墙设置
# Windows
netsh advfirewall firewall add rule name="Redis" dir=in action=allow protocol=TCP localport=6379

# Linux
sudo ufw allow 6379/tcp
```

## 🚀 启动脚本示例

### Windows批处理脚本
```batch
@echo off
echo 启动Redis服务...
cd /d C:\Redis
redis-server.exe redis.windows.conf
pause
```

### Linux Shell脚本
```bash
#!/bin/bash
echo "启动Redis服务..."
sudo systemctl start redis-server
sudo systemctl status redis-server
```

### Docker一键启动脚本
```bash
#!/bin/bash
echo "启动Redis Docker容器..."

# 停止已存在的容器
docker stop my-redis 2>/dev/null
docker rm my-redis 2>/dev/null

# 启动新的容器
docker run -d \
  -p 6379:6379 \
  -v redis-data:/data \
  --name my-redis \
  redis:latest redis-server --appendonly yes

echo "Redis容器已启动"
docker ps | grep my-redis
```

## 📊 性能基准测试

### Redis自带基准测试工具
```bash
# 测试SET性能
redis-benchmark -t set -n 100000 -q

# 测试GET性能
redis-benchmark -t get -n 100000 -q

# 综合性能测试
redis-benchmark -n 100000 -c 50 -q

# 管道测试
redis-benchmark -t set,get -n 100000 -P 10 -q
```

## 🛡️ 安全加固建议

### 1. 网络安全
```conf
# 只绑定必要的IP地址
bind 127.0.0.1 192.168.1.100

# 修改默认端口
port 6380
```

### 2. 认证安全
```conf
# 设置强密码
requirepass YourVeryStrongPassword123!

# 重命名危险命令
rename-command FLUSHDB ""
rename-command FLUSHALL ""
rename-command KEYS ""
rename-command CONFIG ""
```

### 3. 访问控制
```bash
# 配置防火墙只允许特定IP访问
# iptables规则示例
iptables -A INPUT -p tcp --dport 6379 -s 192.168.1.0/24 -j ACCEPT
iptables -A INPUT -p tcp --dport 6379 -j DROP
```

---

*Redis环境配置指南 - 帮你快速搭建开发环境*