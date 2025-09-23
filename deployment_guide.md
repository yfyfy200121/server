# 智能书城管理系统 - 部署运维指南

本文档详细说明了基于SpringBoot的智能书城管理系统的完整部署流程。

## 1. 系统环境要求

### 1.1 硬件要求
- **CPU**: 2核心以上
- **内存**: 4GB以上（推荐8GB）
- **存储**: 50GB以上可用空间
- **网络**: 稳定的互联网连接

### 1.2 软件环境
| 软件 | 版本要求 | 说明 |
|------|----------|------|
| Java | JDK 8+ | 推荐使用JDK 11或17 |
| Node.js | 14.0+ | 前端构建环境 |
| MySQL | 8.0+ | 数据库服务 |
| Maven | 3.6+ | 构建工具 |

## 2. 开发环境搭建

### 2.1 启动后端服务
```bash
# 进入后端目录
cd server/server

# 清理并编译
mvn clean compile

# 启动Spring Boot应用
mvn spring-boot:run
```

### 2.2 启动前端服务
```bash
# 进入前端目录
cd server/web

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 浏览器访问 http://localhost:3000
```

## 3. 生产环境部署

### 3.1 后端打包部署
```bash
# 进入后端目录
cd server/server

# 清理并打包
mvn clean package -DskipTests

# 运行打包后的应用
java -jar target/study-0.0.1-SNAPSHOT.jar
```

### 3.2 前端构建部署
```bash
# 进入前端目录
cd server/web

# 构建生产版本
npm run build

# 部署dist目录到Web服务器
```

## 4. Docker容器化部署

### 4.1 创建docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: your_password
      MYSQL_DATABASE: base_zxsc
    ports:
      - "3306:3306"

  backend:
    build: ./server/server
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/base_zxsc

  frontend:
    build: ./server/web
    depends_on:
      - backend
    ports:
      - "80:80"
```

### 4.2 启动服务
```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 5. 配置说明

### 5.1 后端配置 (application.properties)
```properties
# 服务端口
server.port=9100
server.servlet.context-path=/api

# 数据库连接
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/base_zxsc
spring.datasource.username=root
spring.datasource.password=123456

# AI服务配置
ai.api.key=your-deepseek-api-key
ai.api.url=https://ark.cn-beijing.volces.com/api/v3/chat/completions

# 文件上传路径
File.uploadPath=${user.dir}/upload
```

### 5.2 前端配置 (package.json)
```json
{
  "name": "my-web-app",
  "version": "0.1.2",
  "scripts": {
    "dev": "vite --mode development",
    "build": "vite build",
    "preview": "vite preview"
  }
}
```

## 6. 运维监控

### 6.1 健康检查
```bash
# 检查后端服务
curl http://localhost:9100/api/ai-chat/test-connection

# 检查数据库连接
mysql -h localhost -u root -p base_zxsc
```

### 6.2 日志管理
```bash
# 查看应用日志
tail -f logs/application.log

# 查看访问日志
tail -f logs/access.log
```

### 6.3 备份策略
```bash
# 数据库备份
mysqldump -u root -p base_zxsc > backup_$(date +%Y%m%d).sql

# 文件备份
tar -czf upload_backup_$(date +%Y%m%d).tar.gz upload/
```

## 7. 故障排查

### 7.1 常见问题
1. **数据库连接失败**: 检查MySQL服务状态和连接配置
2. **AI服务调用失败**: 验证API密钥配置
3. **前端页面无法访问**: 检查Nginx配置和静态文件路径
4. **文件上传失败**: 检查上传目录权限

### 7.2 性能优化
1. **JVM参数调优**: -Xms1g -Xmx2g -XX:+UseG1GC
2. **数据库索引优化**: 为常用查询字段创建索引
3. **前端资源压缩**: 启用Gzip压缩
4. **缓存策略**: 使用Redis缓存热点数据

## 8. 安全配置

### 8.1 数据库安全
- 使用强密码
- 限制远程访问
- 定期备份数据

### 8.2 应用安全
- 启用HTTPS
- 配置防火墙
- 隐藏敏感信息

通过本部署指南，您可以成功部署智能书城管理系统。
