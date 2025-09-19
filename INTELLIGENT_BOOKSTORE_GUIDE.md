# 基于SpringBoot的智能书城管理系统设计与实现

## 项目概述

本项目是一个基于SpringBoot框架开发的智能书城管理系统，集成了人工智能客服功能，为用户提供智能化的图书购买和咨询服务。

### 核心特性

- **智能客服**: 基于火山引擎AI的智能客服系统，能够理解用户需求并提供个性化的图书推荐
- **图书管理**: 完整的图书信息管理系统，包括分类、价格、库存等
- **用户系统**: 用户注册、登录、个人中心等功能
- **订单管理**: 完整的购物车和订单处理流程
- **前后端分离**: SpringBoot后端 + Vue.js前端的现代化架构

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.5.5
- **数据库**: MySQL 8.0 / H2 (开发环境)
- **ORM**: MyBatis Plus 3.5.2
- **构建工具**: Maven
- **API文档**: 内置RESTful API
- **AI集成**: 火山引擎DeepSeek API

### 前端技术栈
- **框架**: Vue.js 3.2.45
- **语言**: TypeScript 4.9.4
- **构建工具**: Vite
- **UI组件**: Ant Design Vue 3.2.20
- **状态管理**: Pinia 2.0.28
- **HTTP客户端**: Axios 1.2.2

### 数据库设计

#### 核心表结构
- `b_thing`: 图书信息表
- `b_classification`: 图书分类表
- `b_user`: 用户信息表
- `b_order`: 订单信息表
- `b_comment`: 评论表
- `b_thing_wish`: 用户收藏表
- `b_thing_collect`: 用户关注表

## 智能客服系统

### AI客服功能特点
1. **商品知识库**: 自动加载所有图书信息到AI知识库
2. **智能推荐**: 根据用户需求推荐合适的图书
3. **实时更新**: 5分钟缓存更新机制，确保信息时效性
4. **多语言支持**: 支持中英文对话

### 技术实现
```java
@RestController
@RequestMapping("/ai-chat")
@CrossOrigin(origins = "*")
public class AiChatController {
    
    /**
     * 构建包含商品信息的系统提示
     */
    private String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是购物商城的智能客服助手。你的任务是帮助用户了解商城中的商品信息。");
        
        List<Thing> things = getThingInfo();
        for (Thing thing : things) {
            prompt.append("商品名称：").append(thing.getTitle()).append("\\n");
            prompt.append("价格：").append(thing.getPrice()).append("\\n");
            prompt.append("描述：").append(thing.getDescription()).append("\\n");
            prompt.append("库存：").append(thing.getRepertory()).append("\\n");
        }
        
        return prompt.toString();
    }
}
```

## API接口文档

### 智能客服接口

#### 1. AI对话接口
- **URL**: `GET /api/ai-chat/message`
- **参数**: 
  - `message`: 用户消息内容
  - `sessionId`: 会话ID（可选）
- **响应**: AI回复内容

#### 2. 连接测试接口
- **URL**: `GET /api/ai-chat/test-connection`
- **功能**: 测试AI服务配置状态
- **响应**: 配置状态信息

#### 3. 缓存刷新接口
- **URL**: `GET /api/ai-chat/refresh-cache`
- **功能**: 手动刷新商品信息缓存

### 图书管理接口

#### 1. 图书列表
- **URL**: `GET /api/thing/list`
- **参数**: 
  - `keyword`: 搜索关键词
  - `sort`: 排序方式
  - `c`: 分类ID
  - `tag`: 标签
- **响应**: 图书列表信息

#### 2. 图书详情
- **URL**: `GET /api/thing/detail`
- **参数**: `id` - 图书ID
- **响应**: 图书详细信息

## 部署指南

### 开发环境搭建

#### 1. 环境要求
- JDK 1.8+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+ 或 H2数据库

#### 2. 后端启动
```bash
# 进入后端目录
cd server/server

# 使用H2数据库启动（开发环境）
mvn spring-boot:run -Dspring-boot.run.profiles=demo

# 使用MySQL数据库启动（生产环境）
mvn spring-boot:run
```

#### 3. 前端启动
```bash
# 进入前端目录
cd server/web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 生产环境部署

#### 1. 数据库配置
```sql
# 创建数据库
CREATE DATABASE base_zxsc CHARACTER SET utf8 COLLATE utf8_general_ci;

# 导入表结构
mysql -u root -p base_zxsc < sql/base_zxsc.sql
```

#### 2. 配置文件
修改 `application.properties`:
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/base_zxsc
spring.datasource.username=your_username
spring.datasource.password=your_password

# AI API配置
ai.api.key=your_volcengine_api_key
ai.api.url=https://ark.cn-beijing.volces.com/api/v3/chat/completions
```

#### 3. 打包部署
```bash
# 后端打包
mvn clean package

# 前端打包
npm run build

# 启动应用
java -jar target/study-0.0.1-SNAPSHOT.jar
```

## 功能展示

### 1. 智能客服对话
- 支持自然语言查询图书信息
- 根据用户需求智能推荐图书
- 实时响应用户问题

### 2. 图书管理
- 图书分类浏览
- 详细信息查看
- 价格和库存管理

### 3. 用户体验
- 响应式设计，支持多设备访问
- 直观的用户界面
- 流畅的交互体验

## 系统特色

### 1. 智能化
集成AI技术，提供智能客服和个性化推荐服务

### 2. 现代化架构
采用前后端分离的现代化开发模式

### 3. 可扩展性
模块化设计，易于扩展新功能

### 4. 高性能
缓存机制和数据库优化，确保系统高性能运行

## 开发团队

本项目由专业的开发团队打造，结合了现代Web开发技术和人工智能技术，为用户提供优质的在线图书购买体验。

## 技术支持

如有技术问题，请联系开发团队或查看项目文档。系统支持二次开发和定制化服务。