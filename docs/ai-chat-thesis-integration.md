# AI客服模块论文集成说明

## 概述

AI客服模块现已支持读取系统论文内容，可以回答关于系统设计、技术实现、功能模块等方面的问题。

## 功能特性

### 1. 论文内容自动加载
- 系统启动时自动加载 `docs/thesis/complete-thesis.md` 文件
- 支持多个路径自动查找，提高兼容性
- 为避免提示词过长，只取论文前10000字符作为参考

### 2. 智能缓存机制
- 论文内容缓存时间：30分钟
- 商品信息缓存时间：5分钟
- 支持手动刷新缓存

### 3. 双重知识库
AI客服现在可以回答两类问题：
- **商品相关问题**：商品推荐、价格查询、用户评价等
- **系统相关问题**：技术架构、功能模块、设计思路等

## 技术实现

### 代码改动
文件：`server/server/src/main/java/com/gk/study/controller/AiChatController.java`

#### 1. 新增导入
```java
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
```

#### 2. 新增缓存变量
```java
// 缓存论文内容
private String thesisContent = null;
private long lastThesisCacheUpdate = 0;
private final long THESIS_CACHE_DURATION = 30 * 60 * 1000; // 30分钟缓存
```

#### 3. 新增方法
- `getThesisContent()`: 读取并缓存论文内容
- 更新 `buildSystemPrompt()`: 将论文内容集成到系统提示中
- 更新 `refreshCache()`: 同时刷新论文和商品缓存
- 更新 `testConnection()`: 显示论文加载状态

## API接口

### 1. 测试连接接口
**请求：**
```
GET /api/ai-chat/test-connection
```

**响应示例：**
```json
{
  "apiKeyConfigured": true,
  "apiKeyLength": 36,
  "apiUrl": "https://ark.cn-beijing.volces.com/api/v3/chat/completions",
  "thingCount": 10,
  "thesisLoaded": true,
  "thesisContentLength": 10000,
  "maskedApiKey": "4df2***caef"
}
```

### 2. 刷新缓存接口
**请求：**
```
GET /api/ai-chat/refresh-cache
```

**响应示例：**
```json
{
  "code": 200,
  "message": "缓存已刷新",
  "data": "成功加载 10 件商品信息和论文内容（10000 字符）"
}
```

### 3. 聊天接口
**请求：**
```
GET /api/ai-chat/message?message=介绍一下系统的AI功能&sessionId=user123
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": "本系统集成了多项AI功能：\n1. 智能客服：基于DeepSeek V3大语言模型，提供24小时智能问答服务...\n2. 商品推荐：根据用户行为和商品特征进行个性化推荐...\n3. 情感分析：分析用户评论的情感倾向..."
}
```

## 使用示例

### 商品推荐问题
```
用户：推荐一些算法相关的书籍
AI：为您推荐以下算法书籍：《数据结构与算法分析》价格89元，评分4.8...
```

### 系统技术问题
```
用户：系统使用了哪些技术？
AI：本系统采用了以下技术栈：
- 后端：SpringBoot 2.5.5、MyBatis-Plus 3.5.2、MySQL 8.0
- 前端：Vue 3.2.45、Ant Design Vue 3.2.20、Vite 4.0
- AI服务：火山引擎DeepSeek V3大语言模型
...
```

### 系统功能问题
```
用户：系统有哪些主要功能模块？
AI：系统主要包含以下功能模块：
1. 用户管理模块：用户注册、登录、权限控制
2. 商品管理模块：商品CRUD、分类管理、库存控制
3. 订单管理模块：购物车、订单处理、支付集成
4. AI智能服务模块：智能客服、商品推荐、数据分析
...
```

## 配置说明

### 论文文件路径
系统会按以下顺序查找论文文件：
1. `docs/thesis/complete-thesis.md`
2. `../docs/thesis/complete-thesis.md`
3. `../../docs/thesis/complete-thesis.md`
4. `/home/runner/work/server/server/docs/thesis/complete-thesis.md`

如果所有路径都找不到文件，系统会在日志中记录警告，但不会影响正常运行。

### AI API配置
在 `application.properties` 中配置：
```properties
ai.api.key=your-api-key
ai.api.url=https://ark.cn-beijing.volces.com/api/v3/chat/completions
```

## 性能优化

### 缓存策略
- **论文内容**：由于内容较大且不常变化，缓存30分钟
- **商品信息**：内容相对较小但更新频繁，缓存5分钟

### 内容限制
- 论文内容限制在前10000字符，避免提示词过长
- 对于超长内容会自动截断并添加说明

## 日志记录

系统会记录以下关键操作：
- 论文内容加载成功/失败
- 缓存刷新操作
- 文件查找路径
- 内容长度信息

查看日志：
```bash
tail -f logs/application.log | grep "thesis"
```

## 故障排除

### 问题1：论文内容未加载
**症状**：AI无法回答系统相关问题
**解决**：
1. 检查论文文件是否存在：`ls docs/thesis/complete-thesis.md`
2. 检查日志中的警告信息
3. 手动刷新缓存：`GET /api/ai-chat/refresh-cache`

### 问题2：响应时间过长
**症状**：AI响应超过5秒
**解决**：
1. 考虑进一步减少论文内容长度
2. 优化提示词结构
3. 检查AI服务性能

### 问题3：内容不是最新
**症状**：论文更新后AI仍返回旧内容
**解决**：
1. 等待缓存自动刷新（30分钟）
2. 手动刷新缓存：`GET /api/ai-chat/refresh-cache`

## 未来改进方向

1. **向量数据库集成**：使用向量数据库存储论文内容，支持语义检索
2. **动态内容提取**：根据用户问题动态提取相关章节
3. **多文档支持**：支持加载多个文档文件
4. **实时更新**：监控文件变化，自动刷新缓存

## 贡献者

- 开发：AI客服模块增强
- 文档：功能说明和使用指南
- 测试：功能验证和性能测试

## 版本历史

- **v1.1.0** (2024-10-08): 新增论文内容读取功能
- **v1.0.0**: 初始版本，仅支持商品信息查询
