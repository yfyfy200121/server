# 智能购物平台 (Smart Shopping Platform)

一个基于Spring Boot + Vue 3的现代化电商平台，集成AI智能客服功能。

## 🌟 项目特性

- **前后端分离架构**：Spring Boot后端 + Vue 3前端
- **AI智能客服**：集成AI聊天功能，智能商品推荐
- **现代化UI**：基于Ant Design Vue 3的响应式界面
- **完整的电商功能**：商品管理、订单处理、用户系统
- **高性能**：Redis缓存、MyBatis-Plus数据访问
- **TypeScript支持**：前端全面TypeScript化

## 🛠️ 技术栈

### 后端技术
- **框架**：Spring Boot 2.5.5
- **数据库**：MySQL 8.0.19
- **缓存**：Redis 6.0+
- **ORM**：MyBatis-Plus
- **Java版本**：JDK 1.8
- **其他**：Lombok、Gson

### 前端技术
- **框架**：Vue 3
- **构建工具**：Vite
- **UI库**：Ant Design Vue 3
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **HTTP客户端**：Axios
- **语言**：TypeScript
- **样式**：Less

## 📁 项目结构

```
├── server/                 # 后端项目目录
│   ├── server/            # Spring Boot 应用
│   │   ├── src/main/java/com/gk/study/
│   │   │   ├── controller/    # 控制器层
│   │   │   ├── service/       # 服务接口
│   │   │   ├── service/impl/  # 服务实现
│   │   │   ├── mapper/        # 数据访问层
│   │   │   ├── entity/        # 实体类
│   │   │   ├── config/        # 配置类
│   │   │   └── interceptor/   # 拦截器
│   │   ├── pom.xml           # Maven依赖配置
│   │   └── start.sh          # 启动脚本
│   ├── web/               # 前端项目目录
│   │   ├── src/
│   │   │   ├── components/    # 组件
│   │   │   ├── views/         # 页面
│   │   │   ├── router/        # 路由配置
│   │   │   ├── store/         # 状态管理
│   │   │   ├── api/           # API接口
│   │   │   └── utils/         # 工具函数
│   │   ├── package.json      # 依赖配置
│   │   └── vite.config.ts    # Vite配置
│   └── sql/               # 数据库脚本
└── README.md              # 项目说明文档
```

## 🚀 快速开始

### 环境要求

- **Java**: JDK 1.8+
- **Node.js**: 18.0+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+

### 1. 克隆项目

```bash
git clone https://github.com/yfyfy200121/server.git
cd server
```

### 2. 数据库配置

1. 创建MySQL数据库
2. 执行`sql/`目录下的数据库脚本
3. 修改`server/server/src/main/resources/application.properties`中的数据库配置

```properties
# 数据库配置示例
spring.datasource.url=jdbc:mysql://localhost:3306/your_database?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Redis配置

确保Redis服务已启动，修改配置文件中的Redis连接信息。

### 4. 后端启动

```bash
cd server/server
mvn clean install
./start.sh
# 或者
mvn spring-boot:run
```

后端服务将在 `http://localhost:8888` 启动

### 5. 前端启动

```bash
cd server/web
npm install
npm run dev
```

前端应用将在 `http://localhost:3000` 启动

## 🔧 开发指南

### 后端开发

1. **控制器开发**：在`controller`包下创建REST API
2. **服务层开发**：在`service`包下定义接口，在`service.impl`包下实现
3. **数据访问**：使用MyBatis-Plus在`mapper`包下定义数据访问接口
4. **实体类**：在`entity`包下定义数据模型

### 前端开发

1. **页面开发**：在`src/views`下创建Vue组件
2. **组件开发**：在`src/components`下创建可复用组件
3. **API调用**：在`src/api`下定义API接口
4. **状态管理**：使用Pinia在`src/store`下管理应用状态

## 📚 核心功能

### AI智能客服
- 智能对话交互
- 商品推荐
- 购物咨询

### 商品管理
- 商品展示
- 分类浏览
- 搜索功能
- 商品详情

### 订单系统
- 购物车管理
- 订单创建
- 订单跟踪
- 支付集成

### 用户系统
- 用户注册/登录
- 个人信息管理
- 收货地址管理

## 🔨 构建部署

### 后端部署

```bash
cd server/server
mvn clean package
java -jar target/study-0.0.1-SNAPSHOT.jar
```

### 前端部署

```bash
cd server/web
npm run build
# 将dist目录部署到静态文件服务器
```

## 📋 API文档

主要API端点：

- `GET /api/goods` - 获取商品列表
- `GET /api/goods/{id}` - 获取商品详情
- `POST /api/order` - 创建订单
- `POST /api/user/login` - 用户登录
- `POST /api/ai-chat/message` - AI对话

## 🐛 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库配置信息
   - 确认数据库和表已创建

2. **Redis连接失败**
   - 检查Redis服务状态
   - 验证Redis配置
   - 确认端口和访问权限

3. **前端跨域问题**
   - 检查后端CORS配置
   - 确认API接口地址配置

4. **依赖安装失败**
   - 清理npm缓存：`npm cache clean --force`
   - 使用淘宝镜像：`npm config set registry https://registry.npm.taobao.org`

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📝 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 开发团队

- 项目负责人：yfyfy200121
- 前端开发：lengqin1024

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- GitHub Issues: [提交问题](https://github.com/yfyfy200121/server/issues)
- Email: [联系邮箱]

---

⭐ 如果这个项目对您有帮助，请给我们一个星标！