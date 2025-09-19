# 部署指南

## 开发环境部署

### 环境准备

#### 1. 安装 Java 环境
```bash
# 检查Java版本
java -version

# 如果没有安装Java 8，请下载安装
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-8-jdk

# CentOS/RHEL
sudo yum install java-1.8.0-openjdk-devel
```

#### 2. 安装 MySQL
```bash
# Ubuntu/Debian
sudo apt install mysql-server

# CentOS/RHEL
sudo yum install mysql-server

# 启动MySQL服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 创建数据库
mysql -u root -p
CREATE DATABASE shop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 安装 Redis
```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# 启动Redis服务
sudo systemctl start redis
sudo systemctl enable redis

# 验证Redis安装
redis-cli ping
```

#### 4. 安装 Node.js
```bash
# 使用 nvm 安装 Node.js 18
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18

# 验证安装
node --version
npm --version
```

#### 5. 安装 Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven

# 验证安装
mvn --version
```

### 项目配置

#### 1. 数据库配置
编辑 `server/server/src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/shop_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis配置
mybatis-plus.mapper-locations=classpath:mapper/*.xml
mybatis-plus.type-aliases-package=com.gk.study.entity

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0

# 服务器配置
server.port=8888
server.servlet.context-path=/

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# 日志配置
logging.level.com.gk.study=DEBUG
```

#### 2. 前端环境配置
编辑 `server/web/.env.development`：

```env
# API基础URL
VITE_API_BASE_URL=http://localhost:8888

# 应用标题
VITE_APP_TITLE=智能购物平台
```

### 启动项目

#### 1. 启动后端
```bash
cd server/server

# 方式1：使用Maven
mvn clean compile
mvn spring-boot:run

# 方式2：使用启动脚本
chmod +x start.sh
./start.sh

# 方式3：打包后运行
mvn clean package
java -jar target/study-0.0.1-SNAPSHOT.jar
```

#### 2. 启动前端
```bash
cd server/web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

## 生产环境部署

### 使用 Docker 部署

#### 1. 创建 Dockerfile (后端)
在 `server/server/` 目录下创建 `Dockerfile`：

```dockerfile
FROM openjdk:8-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制jar包
COPY target/study-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8888

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. 创建 Dockerfile (前端)
在 `server/web/` 目录下创建 `Dockerfile`：

```dockerfile
# 构建阶段
FROM node:18-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

# 生产阶段
FROM nginx:alpine

# 复制构建产物到nginx目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

#### 3. 创建 nginx.conf
在 `server/web/` 目录下创建 `nginx.conf`：

```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    server {
        listen 80;
        server_name localhost;

        location / {
            root /usr/share/nginx/html;
            index index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        location /api/ {
            proxy_pass http://backend:8888;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

#### 4. 创建 docker-compose.yml
在项目根目录创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: shop_mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: shop_db
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - shop_network

  redis:
    image: redis:6-alpine
    container_name: shop_redis
    ports:
      - "6379:6379"
    networks:
      - shop_network

  backend:
    build: ./server/server
    container_name: shop_backend
    depends_on:
      - mysql
      - redis
    ports:
      - "8888:8888"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/shop_db?useSSL=false&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123
      SPRING_REDIS_HOST: redis
    networks:
      - shop_network

  frontend:
    build: ./server/web
    container_name: shop_frontend
    depends_on:
      - backend
    ports:
      - "80:80"
    networks:
      - shop_network

volumes:
  mysql_data:

networks:
  shop_network:
    driver: bridge
```

#### 5. 部署命令
```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend

# 停止服务
docker-compose down
```

### 传统部署方式

#### 1. 后端部署

```bash
# 打包项目
cd server/server
mvn clean package -DskipTests

# 创建部署目录
sudo mkdir -p /opt/shop-backend
sudo cp target/study-0.0.1-SNAPSHOT.jar /opt/shop-backend/app.jar

# 创建启动脚本
sudo tee /opt/shop-backend/start.sh << 'EOF'
#!/bin/bash
cd /opt/shop-backend
nohup java -jar \
  -Xms512m -Xmx1024m \
  -Dspring.profiles.active=prod \
  app.jar > app.log 2>&1 &
echo $! > app.pid
EOF

sudo chmod +x /opt/shop-backend/start.sh

# 创建停止脚本
sudo tee /opt/shop-backend/stop.sh << 'EOF'
#!/bin/bash
cd /opt/shop-backend
if [ -f app.pid ]; then
  kill $(cat app.pid)
  rm app.pid
fi
EOF

sudo chmod +x /opt/shop-backend/stop.sh

# 创建systemd服务
sudo tee /etc/systemd/system/shop-backend.service << 'EOF'
[Unit]
Description=Shop Backend Service
After=network.target

[Service]
Type=forking
User=root
WorkingDirectory=/opt/shop-backend
ExecStart=/opt/shop-backend/start.sh
ExecStop=/opt/shop-backend/stop.sh
Restart=always

[Install]
WantedBy=multi-user.target
EOF

# 启动服务
sudo systemctl daemon-reload
sudo systemctl enable shop-backend
sudo systemctl start shop-backend
```

#### 2. 前端部署

```bash
# 构建前端项目
cd server/web
npm install
npm run build

# 部署到nginx
sudo mkdir -p /var/www/shop-frontend
sudo cp -r dist/* /var/www/shop-frontend/

# 配置nginx
sudo tee /etc/nginx/sites-available/shop-frontend << 'EOF'
server {
    listen 80;
    server_name your-domain.com;

    root /var/www/shop-frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8888;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

# 启用站点
sudo ln -s /etc/nginx/sites-available/shop-frontend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## 监控和维护

### 1. 健康检查
```bash
# 检查后端服务
curl http://localhost:8888/actuator/health

# 检查数据库连接
mysql -u root -p -e "SELECT 1"

# 检查Redis连接
redis-cli ping
```

### 2. 日志管理
```bash
# 查看后端日志
tail -f /opt/shop-backend/app.log

# 查看nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# 使用Docker时查看日志
docker-compose logs -f backend
docker-compose logs -f frontend
```

### 3. 备份策略
```bash
# 数据库备份脚本
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p shop_db > /backup/shop_db_$DATE.sql
find /backup -name "shop_db_*.sql" -mtime +7 -delete
```

### 4. 性能优化
- 数据库索引优化
- Redis缓存策略
- 静态资源CDN
- 负载均衡配置

## 故障排除

### 常见问题

1. **后端启动失败**
   - 检查Java版本和环境变量
   - 验证数据库连接配置
   - 查看端口占用情况

2. **前端构建失败**
   - 检查Node.js版本
   - 清理npm缓存
   - 检查依赖版本冲突

3. **数据库连接问题**
   - 验证MySQL服务状态
   - 检查防火墙设置
   - 确认用户权限

4. **Redis连接问题**
   - 检查Redis服务状态
   - 验证配置文件
   - 检查网络连接

### 性能监控

建议安装以下监控工具：
- Prometheus + Grafana
- ELK Stack (日志分析)
- APM工具 (如 SkyWalking)

## 安全建议

1. 定期更新系统和依赖包
2. 配置防火墙规则
3. 使用HTTPS证书
4. 设置数据库访问限制
5. 实施备份和恢复策略