# 项目详细技术文档

## 十一、前后端结构与依赖关系

### 11.1 后端文件结构
```
src/main/java
├── com.gk.study
│   ├── controller
│   │   ├── AiChatTestController.java    # AI服务测试控制器
│   │   ├── CommentController.java       # 评论管理控制器
│   │   ├── GoodsController.java         # 商品管理控制器
│   │   ├── OrderController.java         # 订单管理控制器
│   │   └── UserController.java          # 用户管理控制器
│   │
│   ├── service
│   │   ├── AiChatService.java         # AI聊天服务接口
│   │   ├── CommentService.java          # 评论服务接口
│   │   ├── GoodsService.java            # 商品服务接口
│   │   ├── OrderService.java            # 订单服务接口
│   │   └── UserService.java             # 用户服务接口
│   │
│   ├── service.impl
│   │   ├── AiChatServiceImpl.java       # AI聊天服务实现
│   │   ├── CommentServiceImpl.java        # 评论服务实现
│   │   ├── GoodsServiceImpl.java          # 商品服务实现
│   │   ├── OrderServiceImpl.java          # 订单服务实现
│   │   └── UserServiceImpl.java           # 用户服务实现
│   │
│   ├── mapper
│   │   ├── CommentMapper.java           # 评论数据访问接口
│   │   ├── GoodsMapper.java             # 商品数据访问接口
│   │   ├── OrderMapper.java             # 订单数据访问接口
│   │   └── UserMapper.java              # 用户数据访问接口
│   │
│   ├── entity
│   │   ├── Comment.java               # 评论实体类
│   │   ├── Goods.java                   # 商品实体类
│   │   ├── Order.java                   # 订单实体类
│   │   └── User.java                    # 用户实体类
│   │
│   ├── config
│   │   ├── CorsConfig.java              # 跨域配置
│   │   ├── InterceptorConfig.java         # 拦截器配置
│   │   └── RedisConfig.java              # Redis配置
│   │
│   └── interceptor
│       └── AccessInterceptor.java        # 访问拦截器
│
└── application.properties               # 核心配置文件
```

### 11.2 后端类依赖关系
```
[UserController] --> [UserService] : 用户操作接口
[UserService] --> [UserMapper] : 数据访问
[UserMapper] --> [User] : 实体类映射

[GoodsController] --> [GoodsService] : 商品操作接口
[GoodsService] --> [GoodsMapper] : 数据访问
[GoodsMapper] --> [Goods] : 实体类映射

[OrderController] --> [OrderService] : 订单操作接口
[OrderService] --> [OrderMapper] : 数据访问
[OrderMapper] --> [Order] : 实体类映射

[CommentController] --> [CommentService] : 评论操作接口
[CommentService] --> [CommentMapper] : 数据访问
[CommentMapper] --> [Comment] : 实体类映射

[AccessInterceptor] --> [UserService] : 用户权限验证
[AccessInterceptor] --> [GoodsService] : 商品权限验证

[AiChatTestController] --> [AiChatService] : AI服务测试接口
[AiChatService] --> [RedisUtil] : 商品信息缓存
```

### 11.3 前端文件结构（详细版）
```
src
├── assets
│   └── logo.png                      # 项目logo
│
├── components
│   ├── AiChat.vue                    # AI客服组件
│   │   ├── 消息输入框                # 用户输入区域
│   │   ├── 消息展示区                # 聊天记录展示
│   │   ├── 商品信息卡片                # AI回复中展示商品信息
│   │   └── 快捷咨询按钮               # 快速发起商品咨询
│   │
│   ├── GoodsCard.vue                 # 商品卡片组件
│   │   ├── 商品图片展示                # 图片懒加载优化
│   │   ├── 商品基本信息                # 名称、价格、评分
│   │   ├── 商品操作按钮                # 加入购物车、立即购买
│   │   └── 商品评分组件                # 嵌套Rating.vue组件
│   │
│   └── Rating.vue                    # 评分组件
│       ├── 星级评分展示               # 支持只读和可编辑模式
│       └── 评分交互处理               # 鼠标悬停效果和点击事件
│
├── views
│   ├── home
│   │   └── Index.vue                # 首页
│   │       ├── 商品推荐模块           # 调用商品API获取数据
│   │       ├── 热门商品展示           # 使用GoodsCard组件
│   │       └── 商品分类导航           # 动态生成分类过滤
│   │
│   ├── goods
│   │   ├── Detail.vue               # 商品详情页
│   │   │   ├── 商品图片轮播           # 支持缩放和预览
│   │   │   ├── 商品规格选择           # SKU处理
│   │   │   ├── 商品咨询按钮           # 打开AI客服咨询
│   │   │   └── 商品评价展示           # 分页加载评论
│   │   │
│   │   └── List.vue                 # 商品列表页
│   │       ├── 商品筛选组件           # 分类、价格、排序过滤
│   │       ├── 商品展示网格           # 使用GoodsCard组件
│   │       └── 分页组件             # 支持分页加载
│   │
│   ├── order
│   │   ├── Confirm.vue              # 订单确认页
│   │   │   ├── 收货地址选择         # 调用地址API
│   │   │   ├── 商品信息展示           # 显示选中商品信息
│   │   │   └── 支付方式选择         # 支持多种支付方式
│   │   │
│   │   └── List.vue                 # 订单列表页
│   │       ├── 订单状态筛选           # 按状态过滤订单
│   │       ├── 订单信息展示           # 订单详情和商品信息
│   │       └── 订单操作               # 支付、取消、评价
│   │
│   ├── user
│   │   ├── Login.vue                # 用户登录
│   │   │   ├── 手机/邮箱登录         # 多种登录方式
│   │   │   ├── 第三方登录             # 微信、QQ等
│   │   │   └── 注册链接             # 跳转注册页面
│   │   │
│   │   └── Profile.vue              # 用户中心
│   │       ├── 个人信息管理           # 修改头像、昵称等
│   │       ├── 我的订单             # 跳转订单列表
│   │       └── 退出登录             # 清除Token
│   │
│   └── error
│       └── 404.vue                 # 404页面
│
├── router
│   └── index.js                    # 路由配置
│       ├── 首页路由                 # / 或 /home
│       ├── 商品路由                 # /goods
│       ├── 订单路由                 # /order
│       └── 用户路由                 # /user
│
├── store
│   └── index.js                    # Pinia状态管理
│       ├── 用户状态管理             # 登录状态、用户信息
│       ├── 购物车状态管理           # 商品缓存、数量统计
│       └── 配置状态管理             # 系统配置信息
│
├── utils
│   ├── request.js                  # 网络请求封装
│   │   ├── 请求拦截器               # 添加Token
│   │   ├── 响应拦截器               # 错误处理
│   │   └── API封装                # 统一API调用方式
│   │
│   └── auth.js                     # 认证相关工具
│       ├── Token存储                # localStorage管理
│       ├── Token验证                # 过期时间检查
│       └── 登录状态同步             # Vuex与组件状态同步
│
├── App.vue                         # 根组件
│   ├── 全局样式                    # 主题和公共样式
│   ├── 路由出口                    # <router-view>渲染位置
│   └── 状态管理挂载                # Pinia实例注入
│
└── main.js                         # 入口文件
    ├── Vue实例创建                  # 引入Vue和App.vue
    ├── 路由挂载                   # Vue Router实例
    ├── 状态管理挂载                # Pinia实例
    └── 插件安装                   # Ant Design Vue、Axios等
```

### 11.4 前端模块依赖关系（详细版）
```
[App.vue] --> [router/index.js] : 路由配置
[App.vue] --> [store/index.js] : 状态管理

[GoodsList.vue] --> [GoodsCard.vue] : 商品展示
[GoodsDetail.vue] --> [AiChat.vue] : 商品详情页集成AI客服

[OrderConfirm.vue] --> [GoodsDetail.vue] : 订单确认依赖商品信息

[UserLogin.vue] --> [utils/auth.js] : 认证处理

[GoodsList.vue] --> [utils/request.js] : 商品信息请求

[AiChat.vue] --> [utils/request.js] : AI聊天接口调用

[GoodsDetail.vue] --> [utils/request.js] : 商品详情请求

[GoodsDetail.vue] --> [AiChat.vue] : 商品详情页集成AI客服

[App.vue] --> [main.js] : 入口文件
[GoodsCard.vue] --> [Rating.vue] : 评分组件
[OrderConfirm.vue] --> [utils/request.js] : 创建订单
```

### 11.5 前后端交互结构
```
[Vue前端] --> [Spring Boot后端] : RESTful API调用
[AiChat.vue] --> [AiChatTestController] : AI客服接口
[GoodsList.vue] --> [GoodsController] : 商品列表接口
[GoodsDetail.vue] --> [GoodsController] : 商品详情接口
[OrderConfirm.vue] --> [OrderController] : 订单创建接口
[UserLogin.vue] --> [UserController] : 用户登录接口

[AccessInterceptor] --> [UserService] : 权限验证
[AiChatService] --> [RedisUtil] : 商品信息缓存
[GoodsService] --> [GoodsMapper] : 数据访问
[OrderService] --> [OrderMapper] : 数据访问
```

### 11.6 关键依赖说明

#### 11.6.1 后端核心依赖
```xml
<!-- Spring Boot核心依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis-Plus依赖 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>

<!-- MySQL数据库驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.19</version>
</dependency>

<!-- Redis缓存 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Lombok简化代码 -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Gson数据处理 -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.9.0</version>
</dependency>
```

#### 11.6.2 前端核心依赖
```json
{
  "dependencies": {
    "vue": "^3.4.21",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "axios": "^1.6.7",
    "@vitejs/plugin-vue": "^5.0.4",
    "typescript": "^5.3.3",
    "ant-design-vue": "^3.2.18",
    "@ant-design/icons-vue": "^6.1.2"
  }
}
```

### 11.7 核心功能实现

#### 11.7.1 商品信息缓存实现
- 数据流：GoodsController --> GoodsService --> GoodsMapper --> MySQL
- 缓存：GoodsService中实现Redis缓存，5分钟TTL，支持手动刷新
- 接口：/api/goods/list 获取商品列表（支持缓存）
- 刷新接口：/api/goods/refresh-cache（管理权限）

#### 11.7.2 订单处理实现
- 数据流：OrderController --> OrderService --> OrderMapper --> MySQL
- 业务逻辑：订单创建、支付状态更新、订单查询
- 接口：/api/order/create 创建订单
- 接口：/api/order/list 用户订单列表

#### 11.7.3 权限控制实现
- AccessInterceptor拦截器
- 拦截路径：/api/**
- 白名单路径：/api/user/login, /api/goods/list
- Token验证：JWT验证用户身份
- 权限判断：区分普通用户和管理员

#### 11.7.4 AI客服功能实现
- AiChatTestController测试控制器
- AiChatService核心服务
- 商品信息缓存：RedisUtil实现
- 测试接口：/api/ai-chat/test/auth 认证测试
- 测试接口：/api/ai-chat/test/key API密钥测试
- AI服务：使用火山引擎DeepSeek V3模型
- API端点：https://ark.cn-beijing.volces.com/api/v3/chat/completions
- 请求头：Authorization: Bearer {apikey}, Content-Type: application/json

### 11.8 环境配置与构建

#### 11.8.1 核心配置文件
```properties
# 数据库配置
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/shop?characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456

# 服务器配置
server.port=9100
server.servlet.context-path=/api

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
```

#### 11.8.2 构建流程
**后端构建**:
1. mvn clean package
2. java -jar target/study-0.0.1-SNAPSHOT.jar

**前端构建**:
1. npm install
2. npm run build
3. 部署dist目录到Web服务器

#### 11.8.1 构建配置
```
// vite.config.js
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';

export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
  ],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:9100',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '')
      }
    }
  },
  define: {
    __APP_ENV__: JSON.stringify(process.env.VITE_APP_ENV)
  }
});
```

#### 11.8.2 环境变量配置
```
// .env.development
VITE_APP_ENV = "development"
VITE_APP_BASE_API = "http://127.0.0.1:9100"

// .env.production
VITE_APP_ENV = "production"
VITE_APP_BASE_API = "/api"
```

### 11.9 核心类实现细节

#### 11.9.1 GoodsController
- 路径：/api/goods
- 方法：
  - @GetMapping("/list") 获取商品列表（支持缓存）
  - @GetMapping("/refresh-cache") 刷新商品缓存（管理权限）

#### 11.9.2 AiChatService
- 方法：
  - aiChat(String userContent) : String  处理AI聊天请求
  - refreshGoodsKnowledge() : boolean   刷新商品知识库

#### 11.9.3 RedisUtil
- 方法：
  - get(String key) : String        获取缓存
  - set(String key, String value) : boolean  设置缓存
  - set(String key, String value, long timeout) : boolean  设置带过期时间的缓存

#### 11.9.4 AccessInterceptor
- 方法：
  - preHandle() : boolean  拦截请求，验证Token
  - postHandle() : void    处理响应

#### 11.9.5 GoodsServiceImpl
- 依赖注入：GoodsMapper
- 方法：
  - getGoodsList() : List<Goods>  获取商品列表（支持缓存）
  - refreshGoodsCache() : boolean  刷新商品缓存

#### 11.9.6 AiChat.vue
- 功能：
  - 聊天界面
  - 消息输入/输出
  - 商品信息展示
- 接口调用：
  - /api/ai-chat/message 获取AI回复

#### 11.9.7 GoodsDetail.vue
- 功能：
  - 商品详情展示
  - 加入购物车
  - 发送咨询
- 接口调用：
  - /api/goods/detail/:id 获取商品详情
  - /api/ai-chat/message 发送AI咨询

### 11.10 关键交互流程

#### 11.10.1 商品浏览
```
用户 --> GoodsList.vue
GoodsList.vue --> /api/goods/list
/api/goods/list --> GoodsController
GoodsController --> GoodsService
GoodsService --> RedisUtil (缓存存在)
GoodsService --> GoodsMapper (缓存不存在)
GoodsMapper --> MySQL
GoodsMapper <-- MySQL
GoodsService <-- GoodsMapper
GoodsController <-- GoodsService
GoodsList.vue <-- /api/goods/list
```

#### 11.10.2 订单创建
```
用户 --> OrderConfirm.vue
OrderConfirm.vue --> /api/order/create
/api/order/create --> OrderController
OrderController --> OrderService
OrderService --> OrderMapper
OrderMapper --> MySQL
OrderMapper <-- MySQL
OrderService <-- OrderMapper
OrderController <-- OrderService
OrderConfirm.vue <-- /api/order/create
```

#### 11.10.3 AI客服咨询
```
用户 --> AiChat.vue
AiChat.vue --> /api/ai-chat/message
/api/ai-chat/message --> AiChatTestController
AiChatTestController --> AiChatService
AiChatService --> RedisUtil (获取商品知识)
AiChatService --> 调用火山引擎API
AiChat.vue <-- /api/ai-chat/message
```

#### 11.10.3 AI客服咨询（前端）
```
用户 --> AiChat.vue
AiChat.vue --> /api/ai-chat/message
/api/ai-chat/message --> AiChatTestController
AiChatTestController --> AiChatService
AiChatService --> RedisUtil (获取商品知识)
AiChatService --> 调用火山引擎API
AiChat.vue <-- /api/ai-chat/message
```

#### 11.10.4 商品详情页交互
```
用户 --> GoodsDetail.vue
GoodsDetail.vue --> /api/goods/detail/:id
/api/goods/detail/:id --> GoodsController
GoodsController --> GoodsService
GoodsService --> RedisUtil (缓存存在)
GoodsService --> GoodsMapper (缓存不存在)
GoodsMapper --> MySQL
GoodsMapper <-- MySQL
GoodsService <-- GoodsMapper
GoodsDetail.vue <-- /api/goods/detail/:id
```

### 11.7 核心功能实现（前端部分）

#### 11.7.1 AI客服功能实现
- AiChat.vue组件
- 功能：
  - 聊天界面
  - 消息输入/输出
  - 商品信息展示
- 接口调用：
  - /api/ai-chat/message 获取AI回复
  - 请求参数：`message`（用户输入）
  - 响应处理：解析AI回复并展示

#### 11.7.2 商品详情页实现
- GoodsDetail.vue组件
- 功能：
  - 商品图片轮播
  - 商品规格选择
  - 加入购物车
  - 发送咨询
- 接口调用：
  - /api/goods/detail/:id 获取商品详情
  - /api/ai-chat/message 发送AI咨询

#### 11.7.3 网络请求封装
- request.js工具类
- 功能：
  - 请求拦截器：添加Token
  - 响应拦截器：错误处理
  - 统一API调用方式
- 示例代码：
```javascript
// 请求拦截器
const instance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 5000
});

// 添加请求拦截器
instance.interceptors.request.use(
  config => {
    // 在发送请求前做些什么，如添加Token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);
```

### 11.9 关键组件实现细节

#### 11.9.1 AiChat.vue
- 功能：
  - 聊天界面
  - 消息输入/输出
  - 商品信息展示
- 交互流程：
  - 用户输入 → 调用API → 接收AI回复 → 展示结果
  - 支持商品信息展示 → 点击跳转详情

#### 11.9.2 GoodsDetail.vue
- 功能：
  - 商品图片轮播
  - 商品规格选择
  - 加入购物车
  - 发送咨询
- 数据流：
  - 获取商品详情 → 渲染页面 → 用户操作 → 调用API → 更新状态

#### 11.9.3 request.js
- 方法：
  - get() : Promise  获取数据
  - post() : Promise  提交数据
  - 请求拦截：添加Token
  - 响应拦截：错误处理

### 11.11 依赖管理

#### 11.11.1 后端依赖管理
- 使用Maven进行依赖管理
- 依赖树：
  - Spring Boot
  - MyBatis-Plus
  - MySQL
  - Redis
  - Lombok
  - Gson

#### 11.11.2 前端依赖管理
- 使用npm进行依赖管理
- 依赖树：
  - Vue 3
  - Vue Router 4
  - Pinia
  - Ant Design Vue 3
  - Axios
  - TypeScript

#### 11.11.3 依赖管理最佳实践
- 保持依赖版本一致性
- 定期更新依赖版本
- 依赖最小化原则
- 依赖版本统一管理
- 依赖安全漏洞检测

### 11.12 部署与维护

#### 11.12.1 后端部署
- 推荐部署方式：使用start.sh脚本启动
- 环境要求：JDK 1.8
- 依赖服务：MySQL 8.0.19
- 依赖服务：Redis 6.0+

#### 11.12.2 前端部署
- 构建方式：npm run build
- 部署方式：静态文件服务器
- 推荐服务器：Nginx
- 运行环境：Node.js 18+

#### 11.12.3 日常维护
- 定期检查日志
- 监控API性能
- 数据库优化
- 缓存维护
- 安全审计
- 依赖更新

#### 11.12.4 版本升级
- Spring Boot升级：需注意版本兼容性
- MyBatis-Plus升级：需检查SQL语句兼容性
- Vue升级：需检查组件兼容性
- TypeScript升级：需检查类型定义
- Redis升级：需检查序列化兼容性

## 十二、可视化图表说明

### 12.1 系统架构图
```
+----------------+     +-------------------+     +----------------+
|    Vue前端     |<--->|  Spring Boot后端  |<--->|   数据库/Redis  |
+----------------+ HTTP +-------------------+ JDBC +----------------+
     ↑    ↑                ↑    ↑                 ↑    ↑
  Axios  WebSocket     RESTful  API          MyBatis  Redis
```

### 12.2 类依赖关系图
```
+-----------------+     +-------------------+
|  GoodsController |<--->|   GoodsService    |
+-----------------+ HTTP +-------------------+
      ↑                     ↑
+-----------------+     +-------------------+
|  GoodsMapper    |<--->|    GoodsEntity    |
+-----------------+ JDBC +-------------------+

+-----------------+     +-------------------+
|  RedisUtil      |<--->|  Redis数据库      |
+-----------------+ TCP  +-------------------+
```

## 十三、关键功能代码示例

### 13.1 商品管理API
```
// 商品实体类（Goods.java）
@Data
public class Goods {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
}

// 商品服务接口（GoodsService.java）
public interface GoodsService {
    List<Goods> getGoodsList();
    Goods getGoodsDetail(Long id);
    boolean refreshCache();
}

// 商品服务实现（GoodsServiceImpl.java）
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    
    @Override
    public List<Goods> getGoodsList() {
        return goodsMapper.selectList(null);
    }
}
```

### 13.2 AI客服交互
```
// AI客服组件（AiChat.vue）
<script setup>
import { ref } from 'vue';
import { request } from '@/utils/request';

const messages = ref([]);
const input = ref('');

const sendMessage = async () => {
    if (!input.value) return;
    
    messages.value.push({ role: 'user', content: input.value });
    
    try {
        const response = await request.post('/api/ai-chat/message', {
            message: input.value
        });
        
        messages.value.push({ 
            role: 'assistant', 
            content: response.data.response,
            goods: response.data.goods 
        });
    } catch (error) {
        messages.value.push({ 
            role: 'system', 
            content: 'AI服务暂时不可用，请稍后重试'
        });
    }
    
    input.value = '';
};
</script>
```

### 13.3 商品缓存实现
```
// Redis工具类（RedisUtil.java）
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;
    
    public RedisUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

// 商品服务实现（GoodsServiceImpl.java）
@Override
public List<Goods> getGoodsList() {
    String cacheKey = "goods:list:all";
    String cachedData = redisUtil.get(cacheKey);
    
    if (StringUtils.hasText(cachedData)) {
        return JSON.parseArray(cachedData, Goods.class);
    }
    
    List<Goods> goodsList = goodsMapper.selectList(null);
    if (!goodsList.isEmpty()) {
        redisUtil.set(cacheKey, JSON.toJSONString(goodsList));
    }
    
    return goodsList;
}
```

### 13.4 订单处理流程
```
// 订单创建流程
@PostMapping("/create")
public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
    try {
        // 1. 验证用户权限
        if (!userService.checkUserPermission(orderDTO.getUserId())) {
            return ResponseEntity.status(403).body("无权限操作");
        }
        
        // 2. 检查商品库存
        if (!goodsService.checkStock(orderDTO.getGoodsId(), orderDTO.getQuantity())) {
            return ResponseEntity.status(400).body("库存不足");
        }
        
        // 3. 创建订单
        Order order = orderService.createOrder(orderDTO);
        
        // 4. 更新库存
        goodsService.updateStock(orderDTO.getGoodsId(), orderDTO.getQuantity());
        
        return ResponseEntity.ok(order);
    } catch (Exception e) {
        log.error("订单创建失败: {}", e);
        return ResponseEntity.status(500).body("系统错误");
    }
}
```

## 十四、项目运行指南（小白版）

### 14.1 开发环境准备
1. **JDK安装**
   - 下载JDK 1.8（推荐OpenJDK）
   - 配置JAVA_HOME环境变量
   - 验证安装：`java -version` 和 `javac -version`

2. **Maven安装**
   - 下载Maven 3.6.3+
   - 配置MAVEN_HOME环境变量
   - 验证安装：`mvn -v`

3. **MySQL安装**
   - 下载MySQL 8.0.19+
   - 创建数据库：`CREATE DATABASE shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
   - 验证安装：`mysql -u root -p`

4. **Redis安装**
   - 下载Redis 6.0+
   - 启动服务：`redis-server`
   - 验证安装：`redis-cli ping`

### 14.2 项目启动步骤
1. **后端启动**
   ```bash
   # 进入项目目录
   cd C:\Users\11568\OneDrive\Graduation project\shop_project\server\server
   
   # 构建项目
   mvn clean package
   
   # 启动项目
   mvn spring-boot:run
   # 或使用jar包启动
   java -jar target/study-0.0.1-SNAPSHOT.jar
   ```
