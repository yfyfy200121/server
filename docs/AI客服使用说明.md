# AI客服功能使用说明

## 一、功能概述

本系统集成了基于火山引擎DeepSeek V3大语言模型的智能客服功能，可以实现：
- ✅ 7×24小时智能问答
- ✅ 商品智能推荐
- ✅ 价格库存查询
- ✅ 用户评价分析
- ✅ 实时对话交互

## 二、快速使用

### 1. 配置API密钥

编辑配置文件 `server/src/main/resources/application.properties`：

```properties
# 火山引擎DeepSeek API密钥（必填）
ai.api.key=your-api-key-here

# API端点（可选，默认值通常不需要修改）
ai.api.url=https://ark.cn-beijing.volces.com/api/v3/chat/completions
```

### 2. 启动服务

```bash
# 启动后端
cd server
mvn spring-boot:run

# 启动前端
cd web
npm install
npm run dev
```

### 3. 使用AI客服

1. 打开浏览器访问：http://localhost:3000
2. 在页面右下角看到蓝色的AI客服图标
3. 点击图标打开对话窗口
4. 输入问题，如"推荐一些算法书"，然后发送
5. AI客服会根据商城商品信息给出智能回复

## 三、关键代码解析

### 后端核心代码

#### 1. AI客服控制器 (AiChatController.java)

```java
@RestController
@RequestMapping("/ai-chat")
@CrossOrigin(origins = "*")
public class AiChatController {
    
    @Autowired
    private ThingService thingService;  // 商品服务
    
    @Value("${ai.api.key:}")
    private String apikey;  // API密钥
    
    @Value("${ai.api.url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;  // API端点
    
    /**
     * AI客服聊天接口
     */
    @GetMapping("/message")
    public ResponseEntity<Object> chat(
        @RequestParam String message,
        @RequestParam(required = false) String sessionId
    ) {
        // 1. 检查API密钥
        if (apikey == null || apikey.isEmpty()) {
            return ResponseEntity.ok(mockResponse());
        }
        
        // 2. 构建请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-v3-250324");
        
        // 3. 添加系统提示（包含商品信息）
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createSystemMessage());
        messages.add(createUserMessage(message));
        requestBody.put("messages", messages);
        
        // 4. 调用AI服务
        ResponseEntity<Map> response = restTemplate.postForEntity(
            apiUrl, 
            requestEntity, 
            Map.class
        );
        
        // 5. 返回结果
        return processResponse(response);
    }
}
```

#### 2. 构建系统提示词

```java
private String buildSystemPrompt() {
    StringBuilder prompt = new StringBuilder();
    
    // 角色定义
    prompt.append("你是购物商城的智能客服助手。");
    prompt.append("你的任务是帮助用户了解商城中的商品信息。\n");
    
    // 获取商品信息
    List<Thing> things = getThingInfo();
    
    // 添加每个商品的详细信息
    for (Thing thing : things) {
        prompt.append("商品名称：").append(thing.getTitle()).append("\n");
        prompt.append("价格：").append(thing.getPrice()).append("\n");
        prompt.append("描述：").append(thing.getDescription()).append("\n");
        prompt.append("库存：").append(thing.getRepertory()).append("\n");
        
        // 添加用户评价
        if (thing.getComments() != null) {
            prompt.append("用户评价：\n");
            for (Comment comment : thing.getComments()) {
                prompt.append("- 评分：").append(comment.getScore())
                      .append("，评论：").append(comment.getContent()).append("\n");
            }
        }
        prompt.append("---\n");
    }
    
    // 行为指引
    prompt.append("\n请根据以上商品信息和用户评价回答用户的问题。");
    prompt.append("如果用户询问推荐商品，请优先考虑高评分的商品。");
    
    return prompt.toString();
}
```

#### 3. 商品信息缓存

```java
// 缓存变量
private List<Thing> thingCache = new ArrayList<>();
private long lastCacheUpdate = 0;
private final long CACHE_DURATION = 5 * 60 * 1000; // 5分钟

/**
 * 获取商品信息（带缓存）
 */
private List<Thing> getThingInfo() {
    long currentTime = System.currentTimeMillis();
    
    // 检查缓存是否过期
    if (thingCache.isEmpty() || 
        (currentTime - lastCacheUpdate) > CACHE_DURATION) {
        // 重新加载数据
        thingCache = thingService.getThingListWithComments(
            null, null, null, null
        );
        lastCacheUpdate = currentTime;
    }
    
    return thingCache;
}
```

### 前端核心代码

#### 1. AI客服组件 (ai-chat.vue)

```vue
<template>
  <div class="ai-chat-container">
    <!-- AI客服图标 -->
    <div class="ai-chat-icon" @click="toggleChatWindow">
      <div class="icon-badge" v-if="unreadCount > 0">
        {{ unreadCount }}
      </div>
      <!-- SVG图标 -->
    </div>

    <!-- 对话窗口 -->
    <div v-if="chatVisible" class="ai-chat-window">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-title">AI 客服</div>
        <div class="header-actions">
          <span class="minimize-btn" @click="minimizeChat">一</span>
          <span class="close-btn" @click="closeChat">×</span>
        </div>
      </div>
      
      <!-- 消息列表 -->
      <div class="chat-messages" ref="messagesContainer">
        <div v-for="(message, index) in messages" 
             :key="index" 
             :class="['message', message.type]">
          <div class="message-content" 
               v-html="formatContent(message.content)">
          </div>
          <div class="message-time">
            {{ formatTime(message.timestamp) }}
          </div>
        </div>
      </div>
      
      <!-- 输入框 -->
      <div class="chat-input">
        <a-input v-model:value="inputMessage"
                 placeholder="请输入消息..."
                 @pressEnter="sendMessage"
                 :disabled="loading" />
        <a-button type="primary" 
                  @click="sendMessage" 
                  :loading="loading">
          发送
        </a-button>
      </div>
    </div>
  </div>
</template>
```

#### 2. 发送消息逻辑

```javascript
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  // 1. 添加用户消息
  messages.value.push({
    type: 'user',
    content: inputMessage.value,
    timestamp: new Date()
  })
  
  const userContent = inputMessage.value
  inputMessage.value = ''
  
  try {
    loading.value = true
    
    // 2. 调用后端API
    const response = await axios.get('/api/ai-chat/message', {
      params: { message: userContent }
    })
    
    // 3. 添加AI回复
    if (response.data.code === 200) {
      messages.value.push({
        type: 'ai',
        content: response.data.data,
        timestamp: new Date()
      })
    }
    
  } catch (error) {
    console.error('AI客服调用失败:', error)
    messages.value.push({
      type: 'system',
      content: '抱歉，AI客服暂时无法回复，请稍后再试。',
      timestamp: new Date()
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
```

## 四、API接口说明

### 1. AI对话接口

**接口：** `GET /api/ai-chat/message`

**参数：**
- `message` (必填): 用户输入的消息
- `sessionId` (可选): 会话ID

**示例：**
```bash
curl "http://localhost:9100/api/ai-chat/message?message=推荐一些算法书"
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": "为您推荐几本优秀的算法书籍：《数据结构与算法分析》..."
}
```

### 2. 缓存刷新接口

**接口：** `GET /api/ai-chat/refresh-cache`

**功能：** 手动刷新商品信息缓存

**示例：**
```bash
curl "http://localhost:9100/api/ai-chat/refresh-cache"
```

### 3. 连接测试接口

**接口：** `GET /api/ai-chat/test-connection`

**功能：** 测试AI服务配置和连接状态

**示例：**
```bash
curl "http://localhost:9100/api/ai-chat/test-connection"
```

## 五、使用示例

### 示例1：商品推荐

**用户输入：** 推荐一些算法书

**AI回复：**
```
为您推荐几本优秀的算法书籍：

1. 《数据结构与算法分析》- 价格：89元，评分：4.8分
   用户评价：内容详实，讲解清晰，适合初学者

2. 《算法精粹》- 价格：55元，评分：4.6分
   用户评价：内容精炼，重点突出，适合进阶学习

这些书籍都有较高的用户评价，是学习算法的好选择。
```

### 示例2：价格查询

**用户输入：** 数据结构与算法分析多少钱？

**AI回复：**
```
《数据结构与算法分析》的价格是89元。
这本书评分4.8分，用户评价很高，认为内容详实、讲解清晰。
目前库存充足，您可以放心购买。
```

### 示例3：库存查询

**用户输入：** 算法精粹还有货吗？

**AI回复：**
```
《算法精粹》目前有货，库存为45本。
价格55元，评分4.6分，内容精炼实用。
```

## 六、常见问题

### 问题1：AI客服无响应

**解决方案：**
1. 检查API密钥是否配置正确
2. 查看后端日志是否有错误信息
3. 访问测试接口验证配置：`/api/ai-chat/test-connection`

### 问题2：返回"未配置AI服务"

**原因：** API密钥未配置

**解决方案：**
```properties
# 在application.properties中添加
ai.api.key=your-actual-api-key
```

### 问题3：响应速度慢

**优化方案：**
1. 商品数据过多，可以限制数量
2. 调整缓存时间
3. 考虑使用异步处理

## 七、核心特性

### 1. 智能对话
- 基于DeepSeek V3大语言模型
- 支持自然语言理解
- 多轮对话上下文管理

### 2. 商品信息集成
- 实时获取商品信息
- 自动包含用户评价
- 智能推荐高评分商品

### 3. 性能优化
- 商品信息缓存（5分钟）
- 减少数据库查询
- 快速响应（通常<5秒）

### 4. 错误处理
- 完善的异常处理机制
- 友好的错误提示
- 降级方案支持

## 八、技术栈

**后端：**
- SpringBoot 2.5.5
- RestTemplate (HTTP客户端)
- MyBatis-Plus (数据访问)

**前端：**
- Vue 3.2.45
- Axios (HTTP客户端)
- Ant Design Vue (UI组件)

**AI服务：**
- 火山引擎DeepSeek V3
- REST API集成

## 九、扩展功能

### 1. 会话管理
可以通过sessionId参数实现多轮对话：
```javascript
const response = await axios.get('/api/ai-chat/message', {
  params: { 
    message: userContent,
    sessionId: getCurrentUserId()  // 用户ID作为会话ID
  }
})
```

### 2. 商品链接跳转
AI回复中可以包含商品链接：
```java
// 在AI回复中添加商品链接
String reply = "推荐《数据结构与算法分析》，"
    + "点击查看详情：/detail?id=1";
```

前端会自动将其转换为可点击链接。

### 3. 推荐商品按钮
前端会自动提取AI推荐的商品，生成快捷按钮：
```vue
<div v-if="recommendedThings.length > 0" class="recommend-buttons">
  <div v-for="thing in recommendedThings" 
       @click="goToDetail(thing.id)"
       class="recommend-button">
    {{ thing.title }}
  </div>
</div>
```

## 十、最佳实践

### 1. 优化系统提示词
- 明确AI的角色和职责
- 提供详细的商品信息
- 添加行为指引和限制

### 2. 合理使用缓存
- 根据业务调整缓存时长
- 支持手动刷新缓存
- 监控缓存命中率

### 3. 保护API密钥
- 不要在日志中输出完整密钥
- 使用环境变量存储密钥
- 定期更换密钥

### 4. 错误处理
- 设置合理的超时时间
- 提供友好的错误提示
- 实现降级方案

## 十一、总结

AI客服系统的核心要点：

1. **配置简单**：只需配置API密钥即可使用
2. **集成容易**：前后端代码模块化，易于集成
3. **功能强大**：基于DeepSeek V3，智能理解用户意图
4. **性能优化**：缓存机制，快速响应
5. **易于扩展**：清晰的架构，便于定制和扩展

通过本文档，您应该能够：
- ✅ 理解AI客服的工作原理
- ✅ 配置和启动AI客服功能
- ✅ 了解关键代码的实现
- ✅ 解决常见问题
- ✅ 进行功能扩展和优化

## 十二、参考资源

- [完整开发文档](./ai-customer-service-guide.md)
- [系统架构文档](./architecture/)
- [API接口文档](./api/)
- [火山引擎DeepSeek官方文档](https://www.volcengine.com/)

---

如有问题，欢迎提交Issue或查阅完整开发文档。
