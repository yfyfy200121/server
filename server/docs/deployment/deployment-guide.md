# 系统部署指南

## 1. 部署环境要求

### 1.1 硬件要求
- **CPU**：4核心以上
- **内存**：8GB以上
- **存储**：100GB以上SSD硬盘
- **网络**：100Mbps以上带宽

### 1.2 软件环境
- **操作系统**：Linux Ubuntu 20.04+ / CentOS 8+
- **Java**：OpenJDK 17+
- **Node.js**：16.0+
- **MySQL**：8.0+
- **Redis**：6.0+（可选）
- **Nginx**：1.18+

## 2. 环境准备

### 2.1 Java环境安装
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# 验证安装
java -version
javac -version
```

### 2.2 MySQL数据库安装
```bash
# Ubuntu/Debian
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-server

# 启动MySQL服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置
sudo mysql_secure_installation
```

### 2.3 Node.js环境安装
```bash
# 使用NodeSource仓库安装
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 验证安装
node --version
npm --version
```

### 2.4 Nginx安装
```bash
# Ubuntu/Debian
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx

# 启动服务
sudo systemctl start nginx
sudo systemctl enable nginx
```

## 3. 数据库初始化

### 3.1 创建数据库
```sql
-- 连接到MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE base_zxsc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'bookstore'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON base_zxsc.* TO 'bookstore'@'localhost';
FLUSH PRIVILEGES;
```

### 3.2 导入数据库结构
```bash
# 导入数据库结构和初始数据
mysql -u bookstore -p base_zxsc < /path/to/base_zxsc.sql
```

### 3.3 数据库配置优化
```ini
# /etc/mysql/mysql.conf.d/mysqld.cnf
[mysqld]
# 基础配置
port = 3306
datadir = /var/lib/mysql
socket = /var/lib/mysql/mysql.sock

# 性能优化
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# 连接配置
max_connections = 200
max_connect_errors = 100
connect_timeout = 60
wait_timeout = 28800

# 字符集配置
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

## 4. 后端部署

### 4.1 项目构建
```bash
# 进入后端项目目录
cd /path/to/server/server

# 构建项目
mvn clean package -DskipTests

# 查看构建结果
ls -la target/
```

### 4.2 配置文件设置
```properties
# application-prod.properties
# 服务器配置
server.port=9100
server.servlet.context-path=/api

# 数据库配置
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/base_zxsc?characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=true&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true
spring.datasource.username=bookstore
spring.datasource.password=your_password
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.maximum-pool-size=50

# Redis配置（如果使用）
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0
spring.redis.timeout=10000

# 日志配置
logging.level.com.gk.study=INFO
logging.file.name=/var/log/bookstore/application.log
logging.file.max-size=100MB
logging.file.max-history=30

# AI服务配置
ai.api.key=your_api_key
ai.api.url=https://ark.cn-beijing.volces.com/api/v3/chat/completions

# 文件上传配置
File.uploadPath=/var/bookstore/upload
```

### 4.3 创建运行脚本
```bash
#!/bin/bash
# /opt/bookstore/start.sh

APP_NAME="bookstore-server"
JAR_PATH="/opt/bookstore/study-0.0.1-SNAPSHOT.jar"
PID_FILE="/var/run/bookstore.pid"
LOG_FILE="/var/log/bookstore/application.log"

# 创建必要目录
mkdir -p /var/log/bookstore
mkdir -p /var/bookstore/upload

# 启动应用
start() {
    if [ -f $PID_FILE ]; then
        echo "$APP_NAME is already running."
        exit 1
    fi
    
    echo "Starting $APP_NAME..."
    nohup java -jar $JAR_PATH \
        --spring.profiles.active=prod \
        --server.port=9100 \
        > $LOG_FILE 2>&1 &
    echo $! > $PID_FILE
    echo "$APP_NAME started."
}

# 停止应用
stop() {
    if [ ! -f $PID_FILE ]; then
        echo "$APP_NAME is not running."
        exit 1
    fi
    
    PID=$(cat $PID_FILE)
    echo "Stopping $APP_NAME (PID: $PID)..."
    kill $PID
    rm -f $PID_FILE
    echo "$APP_NAME stopped."
}

# 重启应用
restart() {
    stop
    sleep 5
    start
}

# 查看状态
status() {
    if [ -f $PID_FILE ]; then
        PID=$(cat $PID_FILE)
        if ps -p $PID > /dev/null; then
            echo "$APP_NAME is running (PID: $PID)"
        else
            echo "$APP_NAME is not running (stale PID file)"
            rm -f $PID_FILE
        fi
    else
        echo "$APP_NAME is not running"
    fi
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status}"
        exit 1
        ;;
esac
```

### 4.4 创建系统服务
```ini
# /etc/systemd/system/bookstore.service
[Unit]
Description=Bookstore Management System
After=network.target mysql.service

[Service]
Type=forking
User=bookstore
Group=bookstore
ExecStart=/opt/bookstore/start.sh start
ExecStop=/opt/bookstore/start.sh stop
ExecReload=/opt/bookstore/start.sh restart
PIDFile=/var/run/bookstore.pid
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# 启用并启动服务
sudo systemctl daemon-reload
sudo systemctl enable bookstore
sudo systemctl start bookstore
sudo systemctl status bookstore
```

## 5. 前端部署

### 5.1 项目构建
```bash
# 进入前端项目目录
cd /path/to/server/web

# 安装依赖
npm install

# 生产环境构建
npm run build

# 查看构建结果
ls -la dist/
```

### 5.2 环境配置
```javascript
// .env.production
VUE_APP_API_BASE_URL=https://your-domain.com/api
VUE_APP_UPLOAD_URL=https://your-domain.com/api/upload
VUE_APP_TITLE=智能书城管理系统
```

### 5.3 Nginx配置
```nginx
# /etc/nginx/sites-available/bookstore
server {
    listen 80;
    server_name your-domain.com;
    
    # 重定向到HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    # SSL证书配置
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    
    # 前端静态文件
    root /var/www/bookstore;
    index index.html;
    
    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
    
    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # 前端路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api/ {
        proxy_pass http://localhost:9100;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # 文件上传大小限制
    client_max_body_size 10M;
    
    # 安全头部
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
}
```

### 5.4 部署静态文件
```bash
# 创建网站目录
sudo mkdir -p /var/www/bookstore

# 复制构建文件
sudo cp -r dist/* /var/www/bookstore/

# 设置权限
sudo chown -R www-data:www-data /var/www/bookstore
sudo chmod -R 755 /var/www/bookstore

# 启用站点
sudo ln -s /etc/nginx/sites-available/bookstore /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## 6. SSL证书配置

### 6.1 使用Let's Encrypt免费证书
```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 设置自动续期
sudo crontab -e
# 添加以下行
0 12 * * * /usr/bin/certbot renew --quiet
```

### 6.2 使用自签名证书（测试环境）
```bash
# 生成私钥
sudo openssl genrsa -out /etc/ssl/private/bookstore.key 2048

# 生成证书
sudo openssl req -new -x509 -key /etc/ssl/private/bookstore.key \
    -out /etc/ssl/certs/bookstore.crt -days 365
```

## 7. 监控和日志

### 7.1 系统监控
```bash
# 安装监控工具
sudo apt install htop iotop nethogs

# 监控脚本
#!/bin/bash
# /opt/bookstore/monitor.sh
while true; do
    echo "=== $(date) ==="
    echo "CPU使用率: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)"
    echo "内存使用: $(free -h | grep Mem | awk '{print $3"/"$2}')"
    echo "磁盘使用: $(df -h / | tail -1 | awk '{print $5}')"
    echo "应用状态: $(systemctl is-active bookstore)"
    echo ""
    sleep 300  # 每5分钟检查一次
done
```

### 7.2 日志管理
```bash
# 创建日志轮转配置
# /etc/logrotate.d/bookstore
/var/log/bookstore/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 bookstore bookstore
    postrotate
        systemctl reload bookstore
    endscript
}
```

### 7.3 备份脚本
```bash
#!/bin/bash
# /opt/bookstore/backup.sh
BACKUP_DIR="/backup/bookstore"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p $BACKUP_DIR

# 数据库备份
mysqldump -u bookstore -p base_zxsc > $BACKUP_DIR/db_backup_$DATE.sql

# 文件备份
tar -czf $BACKUP_DIR/files_backup_$DATE.tar.gz /var/bookstore/upload

# 清理30天前的备份
find $BACKUP_DIR -name "*.sql" -mtime +30 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete

echo "备份完成: $DATE"
```

## 8. 性能优化

### 8.1 JVM优化
```bash
# JVM启动参数
JAVA_OPTS="
-Xms2g -Xmx4g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/bookstore/
-Djava.security.egd=file:/dev/./urandom
"
```

### 8.2 MySQL优化
```ini
# my.cnf优化配置
[mysqld]
innodb_buffer_pool_size = 4G
innodb_log_buffer_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_lock_wait_timeout = 120
innodb_file_per_table = 1

query_cache_size = 256M
query_cache_type = 1
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
```

### 8.3 Nginx优化
```nginx
# nginx.conf优化
worker_processes auto;
worker_connections 4096;

http {
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    
    # 缓存配置
    open_file_cache max=10000 inactive=300s;
    open_file_cache_valid 60s;
    open_file_cache_min_uses 2;
    open_file_cache_errors on;
    
    # 压缩配置
    gzip on;
    gzip_comp_level 6;
    gzip_min_length 1000;
    gzip_proxied any;
    gzip_vary on;
}
```

## 9. 故障排查

### 9.1 常见问题
1. **应用启动失败**
   - 检查Java版本和环境变量
   - 查看应用日志：`tail -f /var/log/bookstore/application.log`
   - 检查端口占用：`netstat -tlnp | grep 9100`

2. **数据库连接失败**
   - 检查MySQL服务状态：`systemctl status mysql`
   - 验证数据库连接：`mysql -u bookstore -p`
   - 检查防火墙设置

3. **前端页面无法访问**
   - 检查Nginx配置：`nginx -t`
   - 查看Nginx错误日志：`tail -f /var/log/nginx/error.log`
   - 检查文件权限

4. **AI功能异常**
   - 检查API密钥配置
   - 验证网络连接
   - 查看AI服务调用日志

### 9.2 日志分析
```bash
# 查看应用日志
tail -f /var/log/bookstore/application.log

# 查看访问日志
tail -f /var/log/nginx/access.log

# 查看错误日志
tail -f /var/log/nginx/error.log

# 查看系统日志
journalctl -u bookstore -f
```

## 10. 维护和更新

### 10.1 应用更新流程
```bash
# 1. 备份当前版本
cp study-0.0.1-SNAPSHOT.jar study-0.0.1-SNAPSHOT.jar.backup

# 2. 停止应用
sudo systemctl stop bookstore

# 3. 更新jar文件
cp new-version.jar study-0.0.1-SNAPSHOT.jar

# 4. 启动应用
sudo systemctl start bookstore

# 5. 验证更新
curl http://localhost:9100/api/monitor/system-info
```

### 10.2 数据库升级
```sql
-- 创建升级脚本
-- upgrade_v1.1.sql
ALTER TABLE b_thing ADD COLUMN new_field VARCHAR(255);
UPDATE b_thing SET new_field = 'default_value';
```

### 10.3 定期维护任务
- 每日：数据库备份、日志清理
- 每周：系统更新、性能检查
- 每月：安全检查、容量规划
- 每季度：系统优化、备份恢复测试

通过以上部署指南，可以成功将智能书城管理系统部署到生产环境，并确保系统的稳定运行。