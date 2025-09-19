# 智能书城管理系统部署运维指南

## 一、环境要求

### 1.1 系统要求

#### 生产环境
- **操作系统**: Ubuntu 20.04 LTS / CentOS 7+ / Windows Server 2019+
- **CPU**: 4核心 2.4GHz+
- **内存**: 8GB+
- **存储**: 100GB+ SSD
- **网络**: 100Mbps+

#### 开发环境
- **操作系统**: Windows 10/11, macOS 10.15+, Ubuntu 18.04+
- **CPU**: 2核心 2.0GHz+
- **内存**: 4GB+
- **存储**: 50GB+

### 1.2 软件依赖

#### 后端环境
```bash
# Java开发环境
Java JDK 1.8+
Maven 3.6.3+

# 数据库
MySQL 8.0+
Redis 6.0+

# 应用服务器
SpringBoot 2.5.5 (内嵌Tomcat)
```

#### 前端环境
```bash
# Node.js环境
Node.js 16+
npm 8+

# 构建工具
Vite 4.0+
```

#### 反向代理
```bash
# Web服务器
Nginx 1.18+
```

## 二、开发环境搭建

### 2.1 后端开发环境

#### 安装Java JDK
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-8-jdk

# CentOS/RHEL
sudo yum install java-1.8.0-openjdk-devel

# 验证安装
java -version
javac -version
```

#### 安装Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven

# 验证安装
mvn -version
```

#### 安装MySQL
```bash
# Ubuntu/Debian
sudo apt install mysql-server mysql-client

# 启动服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置
sudo mysql_secure_installation

# 创建数据库
mysql -u root -p
CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'bookstore'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON bookstore.* TO 'bookstore'@'localhost';
FLUSH PRIVILEGES;
```

#### 安装Redis
```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# 启动服务
sudo systemctl start redis
sudo systemctl enable redis

# 测试连接
redis-cli ping
```

### 2.2 前端开发环境

#### 安装Node.js
```bash
# 使用NodeSource安装最新版本
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 验证安装
node --version
npm --version
```

#### 配置npm镜像（可选）
```bash
# 使用淘宝镜像加速
npm config set registry https://registry.npmmirror.com
```

### 2.3 开发工具配置

#### IntelliJ IDEA配置
1. 安装插件：
   - Lombok Plugin
   - MyBatis Plugin
   - Spring Boot Helper

2. 项目配置：
   - JDK版本：1.8
   - 编码：UTF-8
   - Maven配置：指向本地Maven安装

#### VSCode配置
1. 安装扩展：
   - Vetur (Vue开发)
   - TypeScript Vue Plugin
   - ESLint
   - Prettier

2. 配置文件：
```json
// .vscode/settings.json
{
  "typescript.preferences.quoteStyle": "single",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  }
}
```

## 三、项目部署

### 3.1 源码下载与配置

#### 克隆项目
```bash
git clone https://github.com/yfyfy200121/server.git
cd server
```

#### 后端配置
```bash
cd server/server

# 修改配置文件
vim src/main/resources/application.properties
```

**application.properties配置**:
```properties
# 服务器配置
server.port=9100
server.servlet.context-path=/api

# 数据库配置
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore?characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true
spring.datasource.username=bookstore
spring.datasource.password=password123

# MyBatis-Plus配置
mybatis-plus.mapper-locations=classpath:/mapper/*.xml
mybatis-plus.type-aliases-package=com.gk.study.entity
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0
spring.redis.timeout=10000
spring.redis.jedis.pool.max-active=8
spring.redis.jedis.pool.max-wait=-1
spring.redis.jedis.pool.max-idle=8
spring.redis.jedis.pool.min-idle=0

# 文件上传配置
File.uploadPath=/var/www/uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# AI服务配置（可选）
ai.apikey=your_ai_api_key
ai.endpoint=https://ark.cn-beijing.volces.com/api/v3/chat/completions

# 日志配置
logging.level.com.gk.study=INFO
logging.file.name=logs/application.log
logging.file.max-size=100MB
logging.file.max-history=30
```

#### 前端配置
```bash
cd server/web

# 修改配置文件
vim .env.production
```

**.env.production配置**:
```env
# 生产环境配置
VITE_APP_ENV=production
VITE_APP_BASE_API=http://your-domain.com/api
VITE_APP_TITLE=智能书城管理系统
```

### 3.2 数据库初始化

```bash
# 导入数据库结构
mysql -u bookstore -p bookstore < sql/base_zxsc.sql

# 验证导入
mysql -u bookstore -p
USE bookstore;
SHOW TABLES;
```

### 3.3 后端编译部署

#### 开发环境启动
```bash
cd server/server

# 编译项目
mvn clean compile

# 启动开发服务器
mvn spring-boot:run
```

#### 生产环境部署
```bash
# 打包项目
mvn clean package -DskipTests

# 检查打包结果
ls -la target/

# 启动应用
java -jar target/study-0.0.1-SNAPSHOT.jar

# 后台启动
nohup java -jar target/study-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

#### 创建启动脚本
```bash
# 创建启动脚本
vim start.sh
```

**start.sh内容**:
```bash
#!/bin/bash

APP_NAME="study-0.0.1-SNAPSHOT.jar"
APP_PATH="/opt/bookstore"
PID_FILE="$APP_PATH/app.pid"
LOG_FILE="$APP_PATH/logs/app.log"

# 创建日志目录
mkdir -p $APP_PATH/logs

# 检查是否已运行
if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null; then
        echo "应用已在运行 (PID: $PID)"
        exit 1
    else
        rm -f $PID_FILE
    fi
fi

# 启动应用
cd $APP_PATH
nohup java -jar -Xms512m -Xmx1024m $APP_NAME > $LOG_FILE 2>&1 &
echo $! > $PID_FILE

echo "应用启动成功 (PID: $(cat $PID_FILE))"
echo "日志文件: $LOG_FILE"
```

```bash
# 创建停止脚本
vim stop.sh
```

**stop.sh内容**:
```bash
#!/bin/bash

APP_PATH="/opt/bookstore"
PID_FILE="$APP_PATH/app.pid"

if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null; then
        kill $PID
        echo "正在停止应用 (PID: $PID)..."
        
        # 等待进程结束
        for i in {1..30}; do
            if ! ps -p $PID > /dev/null; then
                echo "应用已停止"
                rm -f $PID_FILE
                exit 0
            fi
            sleep 1
        done
        
        # 强制停止
        kill -9 $PID
        echo "强制停止应用"
        rm -f $PID_FILE
    else
        echo "应用未运行"
        rm -f $PID_FILE
    fi
else
    echo "PID文件不存在，应用可能未运行"
fi
```

```bash
# 设置执行权限
chmod +x start.sh stop.sh
```

### 3.4 前端编译部署

#### 安装依赖
```bash
cd server/web
npm install
```

#### 构建生产版本
```bash
# 构建
npm run build

# 检查构建结果
ls -la dist/
```

#### 部署到Web服务器
```bash
# 创建Web目录
sudo mkdir -p /var/www/bookstore

# 复制文件
sudo cp -r dist/* /var/www/bookstore/

# 设置权限
sudo chown -R www-data:www-data /var/www/bookstore
sudo chmod -R 755 /var/www/bookstore
```

### 3.5 Nginx配置

#### 安装Nginx
```bash
# Ubuntu/Debian
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx

# 启动服务
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### 配置Nginx
```bash
# 创建配置文件
sudo vim /etc/nginx/sites-available/bookstore
```

**Nginx配置文件**:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    root /var/www/bookstore;
    index index.html index.htm;
    
    # 前端路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api {
        proxy_pass http://localhost:9100;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 跨域配置
        add_header Access-Control-Allow-Origin *;
        add_header Access-Control-Allow-Methods 'GET, POST, OPTIONS';
        add_header Access-Control-Allow-Headers 'DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization,TOKEN';
        
        if ($request_method = 'OPTIONS') {
            return 204;
        }
    }
    
    # 文件上传目录
    location /uploads {
        alias /var/www/uploads;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
    
    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # 压缩配置
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/javascript
        application/xml+rss
        application/json;
}
```

#### 启用配置
```bash
# 创建软链接
sudo ln -s /etc/nginx/sites-available/bookstore /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重新加载配置
sudo systemctl reload nginx
```

#### HTTPS配置（推荐）
```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo crontab -e
# 添加以下行
0 12 * * * /usr/bin/certbot renew --quiet
```

## 四、系统监控

### 4.1 应用监控

#### 健康检查脚本
```bash
# 创建监控脚本
vim health_check.sh
```

**health_check.sh**:
```bash
#!/bin/bash

# 配置
APP_URL="http://localhost:9100/api"
LOG_FILE="/var/log/bookstore_health.log"
EMAIL_ALERT="admin@example.com"

# 检查应用健康状态
check_app_health() {
    response=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL/user/list" -H "TOKEN: health_check_token")
    
    if [ "$response" == "200" ] || [ "$response" == "401" ]; then
        echo "$(date): 应用健康检查正常" >> $LOG_FILE
        return 0
    else
        echo "$(date): 应用健康检查失败，HTTP状态码: $response" >> $LOG_FILE
        return 1
    fi
}

# 检查数据库连接
check_database() {
    mysql -u bookstore -ppassword123 -e "SELECT 1" bookstore > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo "$(date): 数据库连接正常" >> $LOG_FILE
        return 0
    else
        echo "$(date): 数据库连接失败" >> $LOG_FILE
        return 1
    fi
}

# 检查Redis连接
check_redis() {
    redis-cli ping > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo "$(date): Redis连接正常" >> $LOG_FILE
        return 0
    else
        echo "$(date): Redis连接失败" >> $LOG_FILE
        return 1
    fi
}

# 发送告警邮件
send_alert() {
    local message="$1"
    echo "$message" | mail -s "书城系统告警" $EMAIL_ALERT
}

# 主检查逻辑
main() {
    failed_checks=""
    
    if ! check_app_health; then
        failed_checks="$failed_checks 应用健康检查"
    fi
    
    if ! check_database; then
        failed_checks="$failed_checks 数据库连接"
    fi
    
    if ! check_redis; then
        failed_checks="$failed_checks Redis连接"
    fi
    
    if [ -n "$failed_checks" ]; then
        alert_message="书城系统监控告警：$failed_checks 失败"
        echo "$(date): $alert_message" >> $LOG_FILE
        send_alert "$alert_message"
        exit 1
    fi
}

main
```

#### 设置定时检查
```bash
# 设置可执行权限
chmod +x health_check.sh

# 添加到crontab
crontab -e
# 每5分钟检查一次
*/5 * * * * /path/to/health_check.sh
```

### 4.2 性能监控

#### 系统资源监控脚本
```bash
vim resource_monitor.sh
```

**resource_monitor.sh**:
```bash
#!/bin/bash

LOG_FILE="/var/log/bookstore_resources.log"

# 获取系统资源使用情况
get_system_info() {
    # CPU使用率
    cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | awk -F'%' '{print $1}')
    
    # 内存使用率
    memory_info=$(free | grep Mem)
    memory_total=$(echo $memory_info | awk '{print $2}')
    memory_used=$(echo $memory_info | awk '{print $3}')
    memory_usage=$(echo "scale=2; $memory_used/$memory_total*100" | bc)
    
    # 磁盘使用率
    disk_usage=$(df -h / | awk 'NR==2{print $5}' | sed 's/%//')
    
    # 应用进程信息
    app_pid=$(pgrep -f "study-0.0.1-SNAPSHOT.jar")
    if [ -n "$app_pid" ]; then
        app_memory=$(ps -p $app_pid -o %mem --no-headers)
        app_cpu=$(ps -p $app_pid -o %cpu --no-headers)
    else
        app_memory="0"
        app_cpu="0"
    fi
    
    # 记录日志
    echo "$(date),CPU:${cpu_usage}%,Memory:${memory_usage}%,Disk:${disk_usage}%,AppCPU:${app_cpu}%,AppMem:${app_memory}%" >> $LOG_FILE
    
    # 告警检查
    if (( $(echo "$cpu_usage > 80" | bc -l) )); then
        echo "CPU使用率过高: ${cpu_usage}%" | mail -s "系统告警" admin@example.com
    fi
    
    if (( $(echo "$memory_usage > 80" | bc -l) )); then
        echo "内存使用率过高: ${memory_usage}%" | mail -s "系统告警" admin@example.com
    fi
    
    if [ "$disk_usage" -gt 80 ]; then
        echo "磁盘使用率过高: ${disk_usage}%" | mail -s "系统告警" admin@example.com
    fi
}

get_system_info
```

### 4.3 日志管理

#### 日志轮转配置
```bash
# 创建logrotate配置
sudo vim /etc/logrotate.d/bookstore
```

**logrotate配置**:
```
/opt/bookstore/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 bookstore bookstore
    postrotate
        /bin/kill -USR1 $(cat /opt/bookstore/app.pid 2>/dev/null) 2>/dev/null || true
    endscript
}

/var/log/bookstore_*.log {
    weekly
    rotate 12
    compress
    delaycompress
    missingok
    notifempty
}
```

#### 日志分析脚本
```bash
vim log_analysis.sh
```

**log_analysis.sh**:
```bash
#!/bin/bash

LOG_FILE="/opt/bookstore/logs/app.log"
REPORT_FILE="/var/log/bookstore_report_$(date +%Y%m%d).log"

# 分析错误日志
analyze_errors() {
    echo "=== 错误日志分析 ===" >> $REPORT_FILE
    echo "时间范围: $(date)" >> $REPORT_FILE
    
    # 统计错误数量
    error_count=$(grep -c "ERROR" $LOG_FILE)
    echo "错误总数: $error_count" >> $REPORT_FILE
    
    # 统计最频繁的错误
    echo "最频繁的错误:" >> $REPORT_FILE
    grep "ERROR" $LOG_FILE | awk '{print $5}' | sort | uniq -c | sort -nr | head -10 >> $REPORT_FILE
    
    echo "" >> $REPORT_FILE
}

# 分析访问日志
analyze_access() {
    echo "=== 访问日志分析 ===" >> $REPORT_FILE
    
    # 统计请求数量
    request_count=$(grep -c "INFO.*Controller" $LOG_FILE)
    echo "请求总数: $request_count" >> $REPORT_FILE
    
    # 统计最热门的API
    echo "最热门的API:" >> $REPORT_FILE
    grep "INFO.*Controller" $LOG_FILE | awk '{print $6}' | sort | uniq -c | sort -nr | head -10 >> $REPORT_FILE
    
    echo "" >> $REPORT_FILE
}

analyze_errors
analyze_access

# 清理旧的报告文件（保留30天）
find /var/log -name "bookstore_report_*.log" -mtime +30 -delete
```

## 五、备份策略

### 5.1 数据库备份

#### 自动备份脚本
```bash
vim backup_database.sh
```

**backup_database.sh**:
```bash
#!/bin/bash

# 配置
DB_USER="bookstore"
DB_PASSWORD="password123"
DB_NAME="bookstore"
BACKUP_DIR="/backup/database"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/bookstore_$DATE.sql"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
mysqldump -u$DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE

if [ $? -eq 0 ]; then
    echo "$(date): 数据库备份成功: $BACKUP_FILE" >> /var/log/backup.log
    
    # 压缩备份文件
    gzip $BACKUP_FILE
    
    # 删除7天前的备份
    find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
else
    echo "$(date): 数据库备份失败" >> /var/log/backup.log
fi
```

#### 设置定时备份
```bash
# 添加到crontab
crontab -e
# 每天凌晨2点备份
0 2 * * * /path/to/backup_database.sh
```

### 5.2 文件备份

#### 应用文件备份
```bash
vim backup_files.sh
```

**backup_files.sh**:
```bash
#!/bin/bash

# 配置
APP_DIR="/opt/bookstore"
UPLOAD_DIR="/var/www/uploads"
BACKUP_DIR="/backup/files"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份应用文件
tar -czf "$BACKUP_DIR/app_$DATE.tar.gz" -C $APP_DIR .

# 备份上传文件
tar -czf "$BACKUP_DIR/uploads_$DATE.tar.gz" -C $UPLOAD_DIR .

# 删除30天前的备份
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete

echo "$(date): 文件备份完成" >> /var/log/backup.log
```

### 5.3 配置文件备份

#### 系统配置备份
```bash
vim backup_config.sh
```

**backup_config.sh**:
```bash
#!/bin/bash

BACKUP_DIR="/backup/config"
DATE=$(date +%Y%m%d_%H%M%S)
CONFIG_BACKUP="$BACKUP_DIR/config_$DATE.tar.gz"

mkdir -p $BACKUP_DIR

# 备份配置文件
tar -czf $CONFIG_BACKUP \
    /etc/nginx/sites-available/bookstore \
    /opt/bookstore/src/main/resources/application.properties \
    /etc/mysql/mysql.conf.d/mysqld.cnf \
    /etc/redis/redis.conf

echo "$(date): 配置文件备份完成: $CONFIG_BACKUP" >> /var/log/backup.log
```

## 六、故障排除

### 6.1 常见问题

#### 应用启动失败
```bash
# 检查端口占用
netstat -tlnp | grep 9100

# 检查Java进程
ps aux | grep java

# 查看错误日志
tail -f /opt/bookstore/logs/app.log

# 检查配置文件
vim src/main/resources/application.properties
```

#### 数据库连接失败
```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 测试数据库连接
mysql -u bookstore -p bookstore

# 检查防火墙
sudo ufw status
```

#### Redis连接失败
```bash
# 检查Redis服务状态
sudo systemctl status redis

# 测试Redis连接
redis-cli ping

# 查看Redis日志
sudo tail -f /var/log/redis/redis-server.log
```

#### Nginx配置错误
```bash
# 测试Nginx配置
sudo nginx -t

# 查看Nginx错误日志
sudo tail -f /var/log/nginx/error.log

# 重新加载配置
sudo systemctl reload nginx
```

### 6.2 性能优化

#### JVM调优
```bash
# 修改启动参数
java -jar -Xms1024m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 study-0.0.1-SNAPSHOT.jar
```

#### MySQL优化
```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log';
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- 查看索引使用情况
SHOW INDEX FROM b_thing;

-- 优化查询
EXPLAIN SELECT * FROM b_thing WHERE title LIKE '%keyword%';
```

#### Redis优化
```bash
# 编辑Redis配置
sudo vim /etc/redis/redis.conf

# 优化配置项
maxmemory 256mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

### 6.3 安全加固

#### 系统安全
```bash
# 更新系统
sudo apt update && sudo apt upgrade

# 配置防火墙
sudo ufw enable
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# 禁用root登录
sudo vim /etc/ssh/sshd_config
# PermitRootLogin no

# 重启SSH服务
sudo systemctl restart ssh
```

#### 数据库安全
```sql
-- 删除匿名用户
DELETE FROM mysql.user WHERE User='';

-- 删除test数据库
DROP DATABASE IF EXISTS test;

-- 限制远程连接
UPDATE mysql.user SET Host='localhost' WHERE User='bookstore';

FLUSH PRIVILEGES;
```

#### 应用安全
```bash
# 设置文件权限
sudo chown -R bookstore:bookstore /opt/bookstore
sudo chmod 750 /opt/bookstore
sudo chmod 640 /opt/bookstore/src/main/resources/application.properties

# 隐藏敏感信息
sudo vim /opt/bookstore/src/main/resources/application.properties
# 使用环境变量存储敏感配置
```

## 七、扩展部署

### 7.1 负载均衡部署

#### 多实例部署
```bash
# 启动多个应用实例
java -jar -Dserver.port=9101 study-0.0.1-SNAPSHOT.jar &
java -jar -Dserver.port=9102 study-0.0.1-SNAPSHOT.jar &
java -jar -Dserver.port=9103 study-0.0.1-SNAPSHOT.jar &
```

#### Nginx负载均衡配置
```nginx
upstream bookstore_backend {
    server localhost:9101 weight=1;
    server localhost:9102 weight=1;
    server localhost:9103 weight=1;
}

server {
    listen 80;
    server_name your-domain.com;
    
    location /api {
        proxy_pass http://bookstore_backend;
        # 其他配置...
    }
}
```

### 7.2 容器化部署

#### Dockerfile (后端)
```dockerfile
FROM openjdk:8-jre-slim

WORKDIR /app

COPY target/study-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9100

CMD ["java", "-jar", "app.jar"]
```

#### Dockerfile (前端)
```dockerfile
FROM node:18-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

#### Docker Compose
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: bookstore
      MYSQL_USER: bookstore
      MYSQL_PASSWORD: password123
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

  backend:
    build: ./server/server
    ports:
      - "9100:9100"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/bookstore
      SPRING_REDIS_HOST: redis

  frontend:
    build: ./server/web
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

## 八、更新升级

### 8.1 应用更新流程

#### 蓝绿部署
```bash
# 准备新版本
cd /opt/bookstore-new
# 部署新版本应用...

# 切换流量
sudo vim /etc/nginx/sites-available/bookstore
# 修改proxy_pass指向新版本

# 重新加载Nginx
sudo systemctl reload nginx

# 验证新版本正常后，停止旧版本
```

#### 滚动更新
```bash
# 更新脚本
vim rolling_update.sh
```

**rolling_update.sh**:
```bash
#!/bin/bash

NEW_VERSION="$1"
if [ -z "$NEW_VERSION" ]; then
    echo "请指定版本号"
    exit 1
fi

# 备份当前版本
cp /opt/bookstore/study-0.0.1-SNAPSHOT.jar /opt/bookstore/backup/

# 下载新版本
wget "http://releases.example.com/study-$NEW_VERSION.jar" -O /opt/bookstore/study-$NEW_VERSION.jar

# 停止应用
./stop.sh

# 更新应用
mv /opt/bookstore/study-$NEW_VERSION.jar /opt/bookstore/study-0.0.1-SNAPSHOT.jar

# 启动应用
./start.sh

# 健康检查
sleep 10
if ./health_check.sh; then
    echo "更新成功"
else
    echo "更新失败，回滚中..."
    ./stop.sh
    mv /opt/bookstore/backup/study-0.0.1-SNAPSHOT.jar /opt/bookstore/
    ./start.sh
fi
```

### 8.2 数据库迁移

#### 版本迁移脚本
```sql
-- V1.1.0 升级脚本
ALTER TABLE b_user ADD COLUMN last_login_time varchar(19) NULL COMMENT '最后登录时间';
ALTER TABLE b_thing ADD COLUMN author varchar(100) NULL COMMENT '作者';

-- 创建新表
CREATE TABLE `b_book_review` (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `book_id` bigint(0) NOT NULL,
  `user_id` bigint(0) NOT NULL,
  `rating` int NOT NULL,
  `review_text` text,
  `create_time` varchar(19) NULL,
  PRIMARY KEY (`id`)
);
```

---

本部署运维指南为智能书城管理系统提供了完整的部署、监控、维护和扩展方案，确保系统在生产环境中稳定可靠地运行。