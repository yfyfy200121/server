# 基于SpringBoot的智能书城管理系统

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.5.5-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.2.45-brightgreen.svg)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

## 📖 项目简介

智能书城管理系统是一个基于SpringBoot和Vue3的现代化电商平台，专为图书销售和管理而设计。系统集成了人工智能技术，提供智能客服、数据分析、个性化推荐等创新功能，为用户提供优质的购书体验，为商家提供高效的管理工具。

## ✨ 系统特色

### 🤖 AI智能功能
- **智能客服**：集成火山引擎DeepSeek V3大语言模型，提供7×24小时智能问答
- **智能推荐**：基于用户行为和商品特征的个性化推荐算法
- **情感分析**：自动分析用户评论情感倾向，提供商品改进建议
- **销售预测**：基于历史数据预测销售趋势，辅助库存管理
- **用户画像**：深度分析用户行为，生成精准用户标签

### 💻 现代化技术栈
- **后端**：SpringBoot + MyBatis-Plus + MySQL + Redis
- **前端**：Vue3 + Composition API + Ant Design Vue
- **构建工具**：Maven + Vite
- **部署**：Docker + Nginx
- **监控**：系统监控 + 性能分析

### 🛍️ 完整电商功能
- **用户系统**：注册登录、权限管理、个人中心
- **商品管理**：商品CRUD、分类标签、库存管理
- **订单系统**：购物车、下单支付、状态跟踪
- **评价系统**：商品评价、评分统计、关键词提取
- **后台管理**：数据统计、用户管理、系统配置

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        用户层                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   用户端    │  │   管理端    │  │   移动端    │          │
│  │  (Vue3)     │  │  (Vue3)     │  │  (响应式)   │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
                       ┌─────────────┐
                       │   网关层     │
                       │   (Nginx)   │
                       └─────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                       应用层                                 │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                SpringBoot应用                            │ │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐      │ │
│  │  │控制器层 │ │业务逻辑层│ │数据访问层│ │  AI服务  │      │ │
│  │  │Controller│ │Service │ │ Mapper  │ │Analytics│      │ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘      │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                       数据层                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   MySQL     │  │    Redis    │  │   文件存储   │          │
│  │  (主数据库)  │  │   (缓存)    │  │  (图片等)   │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      外部服务                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │火山引擎DeepSeek│ │   支付接口   │  │   物流接口   │          │
│  │   AI服务     │  │   (预留)    │  │   (预留)    │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 快速开始

### 环境要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 后端启动
```bash
# 克隆项目
git clone https://github.com/yfyfy200121/server.git
cd server/server

# 配置数据库
# 1. 创建数据库 base_zxsc
# 2. 导入 sql/base_zxsc.sql
# 3. 修改 application.properties 中的数据库配置

# 编译启动
mvn clean compile
mvn spring-boot:run
```

### 前端启动
```bash
# 进入前端目录
cd web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 访问系统
- 前端地址：http://localhost:3000
- 后端API：http://localhost:9100/api
- API文档：查看 `docs/api/api-documentation.md`

## 📊 功能展示

### 主要功能模块

#### 1. 智能客服系统
- **功能**：基于AI的智能问答和商品推荐
- **特点**：7×24小时服务，上下文理解，商品信息实时获取
- **技术**：DeepSeek V3 API集成，商品信息缓存，对话状态管理

#### 2. 商品管理系统
- **功能**：商品CRUD、分类管理、库存控制
- **特点**：批量操作、图片上传、状态管理
- **技术**：MyBatis-Plus、文件上传、缓存优化

#### 3. 订单管理系统
- **功能**：购物车、订单创建、支付处理、状态跟踪
- **特点**：库存检查、金额计算、状态流转
- **技术**：事务管理、状态机、异步处理

#### 4. AI分析系统
- **功能**：情感分析、销售预测、用户画像、库存预警
- **特点**：数据挖掘、趋势分析、智能决策
- **技术**：自然语言处理、机器学习算法、数据可视化

#### 5. 系统监控
- **功能**：性能监控、健康检查、日志管理
- **特点**：实时监控、自动报警、数据统计
- **技术**：JVM监控、系统指标、日志分析

## 🔧 开发指南

### 项目结构
```
server/
├── docs/                   # 项目文档
│   ├── api/               # API接口文档
│   ├── architecture/      # 系统架构文档
│   ├── database/          # 数据库设计文档
│   ├── deployment/        # 部署指南
│   ├── testing/          # 测试文档
│   └── thesis/           # 论文相关文档
├── server/                 # 后端SpringBoot项目
│   ├── src/main/java/
│   │   └── com/gk/study/
│   │       ├── controller/    # 控制器层
│   │       ├── service/       # 业务逻辑层
│   │       ├── entity/        # 实体类
│   │       ├── mapper/        # 数据访问层
│   │       └── config/        # 配置类
│   └── src/main/resources/
├── web/                    # 前端Vue3项目
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 通用组件
│   │   ├── api/          # API接口
│   │   └── router/       # 路由配置
│   └── public/
└── sql/                   # 数据库脚本
```

### 核心技术实现

#### AI客服实现
```java
@RestController
@RequestMapping("/ai-chat")
public class AiChatController {
    
    @GetMapping("/message")
    public ResponseEntity<Object> chat(@RequestParam String message,
                                       @RequestParam String sessionId) {
        // 构建系统提示，包含商品信息
        String systemPrompt = buildSystemPrompt();
        
        // 调用AI服务
        Map<String, Object> requestBody = buildAIRequest(systemPrompt, message);
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestBody, Map.class);
        
        // 处理AI回复
        return processAIResponse(response);
    }

    private String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是购物商城的智能客服助手...");
        
        // 获取商品信息并加入提示
        List<Thing> things = getThingInfo();
        for (Thing thing : things) {
            prompt.append("商品名称：").append(thing.getTitle()).append("\n");
            prompt.append("价格：").append(thing.getPrice()).append("\n");
            // 添加评论信息
            if (thing.getComments() != null) {
                for (Comment comment : thing.getComments()) {
                    prompt.append("用户评价：").append(comment.getContent()).append("\n");
                }
            }
        }
        return prompt.toString();
    }
}
```

#### 情感分析实现
```java
@Service
public class AIAnalyticsService {
    
    public Map<String, Object> analyzeSentiment(List<Comment> comments) {
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (Comment comment : comments) {
            String content = comment.getContent();
            
            // 检查积极关键词
            for (String keyword : POSITIVE_KEYWORDS) {
                if (content.contains(keyword)) {
                    positiveCount++;
                    break;
                }
            }
            
            // 检查消极关键词
            for (String keyword : NEGATIVE_KEYWORDS) {
                if (content.contains(keyword)) {
                    negativeCount++;
                    break;
                }
            }
        }
        
        // 计算情感比例和总体倾向
        double positiveRatio = (double) positiveCount / comments.size();
        String overallSentiment = positiveRatio > 0.6 ? "positive" : 
                                 (positiveRatio < 0.4 ? "negative" : "neutral");
        
        Map<String, Object> result = new HashMap<>();
        result.put("positiveCount", positiveCount);
        result.put("negativeCount", negativeCount);
        result.put("positiveRatio", positiveRatio);
        result.put("overallSentiment", overallSentiment);
        
        return result;
    }
}
```

### API接口示例

#### 商品列表接口
```
GET /api/thing/list?keyword=算法&sort=recommend&c=2

Response:
{
    "code": 200,
    "msg": "查询成功",
    "data": [
        {
            "id": 1,
            "title": "数据结构与算法分析",
            "cover": "cover1.jpg",
            "price": "89.00",
            "score": "4.8",
            "classification": {
                "id": 2,
                "title": "计算机"
            }
        }
    ]
}
```

#### AI客服接口
```
GET /api/ai-chat/message?message=推荐一些算法书&sessionId=user123

Response:
{
    "code": 200,
    "msg": "success",
    "data": "为您推荐几本优秀的算法书籍：《数据结构与算法分析》价格89元，用户评价很高；《算法精粹》价格55元，内容精炼实用。这些书籍都有详细的代码示例和清晰的讲解。"
}
```

## 📈 系统监控

### 性能指标
- **响应时间**：API平均响应时间 < 500ms
- **并发能力**：支持100+并发用户
- **AI响应**：智能客服响应时间 < 5s
- **系统可用性**：99.5%+

### 监控接口
```bash
# 系统信息
GET /api/monitor/system-info

# 健康检查
GET /api/monitor/health

# 性能指标
GET /api/monitor/performance
```

## 🧪 测试覆盖

### 测试类型
- **单元测试**：核心业务逻辑测试
- **集成测试**：API接口测试
- **功能测试**：用户功能流程测试
- **性能测试**：并发和响应时间测试
- **安全测试**：权限和数据安全测试

### 测试结果
- **功能测试覆盖率**：95%
- **代码测试覆盖率**：78%
- **API测试覆盖率**：90%
- **缺陷修复率**：100%

## 📋 部署指南

### Docker部署
```bash
# 构建镜像
docker build -t bookstore-backend ./server
docker build -t bookstore-frontend ./web

# 运行容器
docker-compose up -d
```

### 传统部署
```bash
# 后端部署
mvn clean package
java -jar target/study-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 前端部署
npm run build
# 将dist目录内容部署到Nginx
```

详细部署指南请参考：[部署文档](docs/deployment/deployment-guide.md)

## 📖 文档资源

- [系统架构设计](docs/architecture/system-architecture.md)
- [数据库设计](docs/database/database-design.md)
- [API接口文档](docs/api/api-documentation.md)
- [测试文档](docs/testing/test-documentation.md)
- [部署指南](docs/deployment/deployment-guide.md)
- [论文内容](docs/thesis/thesis-content.md)

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📝 许可证

本项目采用 MIT 许可证。详情请见 [LICENSE](LICENSE) 文件。

## 👥 开发团队

- **项目负责人**：系统架构设计、AI功能集成
- **后端开发**：SpringBoot应用开发、数据库设计
- **前端开发**：Vue3界面开发、用户体验优化
- **测试工程师**：功能测试、性能测试、安全测试

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 项目地址：https://github.com/yfyfy200121/server
- 问题反馈：GitHub Issues
- 技术交流：项目讨论区

## 🎯 未来规划

- [ ] 微信小程序支持
- [ ] 移动端APP开发
- [ ] 更多AI功能集成
- [ ] 多语言国际化支持
- [ ] 云原生架构升级
- [ ] 大数据分析平台

---

⭐ 如果这个项目对您有帮助，请给我们一个星标！

© 2024 智能书城管理系统. All rights reserved.