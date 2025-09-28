# 5 系统实现

本章详细介绍智能书城管理系统的具体实现过程，包括前台功能模块、后台管理模块以及AI智能客服模块的技术实现细节。

## 5.1 系统环境配置

### 5.1.1 开发环境

**后端开发环境**：
- JDK 1.8
- SpringBoot 2.5.5
- Maven 3.9
- IntelliJ IDEA 2023
- MySQL 8.0
- Redis 6.0

**前端开发环境**：
- Node.js 16.0+
- Vue 3.2.45
- Vite 4.0
- Visual Studio Code
- Chrome 浏览器

### 5.1.2 项目结构

**后端项目结构**：
```
server/
├── src/main/java/com/gk/study/
│   ├── MySpringApplication.java    # 主启动类
│   ├── config/                     # 配置类
│   ├── controller/                 # 控制器层
│   ├── service/                    # 业务逻辑层
│   ├── entity/                     # 实体类
│   ├── mapper/                     # 数据访问层
│   ├── common/                     # 公共类
│   ├── utils/                      # 工具类
│   └── interceptor/                # 拦截器
├── src/main/resources/
│   ├── application.yml             # 配置文件
│   ├── mapper/                     # MyBatis映射文件
│   └── static/                     # 静态资源
└── pom.xml                         # Maven配置
```

**前端项目结构**：
```
web/
├── src/
│   ├── views/                      # 页面组件
│   │   ├── index/                  # 前台页面
│   │   └── admin/                  # 后台页面
│   ├── components/                 # 通用组件
│   ├── api/                        # API接口
│   ├── router/                     # 路由配置
│   ├── store/                      # 状态管理
│   ├── utils/                      # 工具函数
│   └── styles/                     # 样式文件
├── public/                         # 公共资源
├── package.json                    # 项目配置
└── vite.config.js                  # 构建配置
```

## 5.2 前台功能模块实现

### 5.2.1 用户认证模块

**用户实体类设计**：
```java
@Data
@TableName("b_user")
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    
    @TableField
    public String username;
    
    @TableField
    public String password;
    
    @TableField
    public String email;
    
    @TableField
    public String mobile;
    
    @TableField
    public String role;
    
    @TableField
    public String status;
    
    @TableField
    public String avatar;
    
    @TableField
    public String createTime;
}
```

**用户认证接口实现**：
```java
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public APIResponse login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new APIResponse(RestStatus.FAIL, "用户名和密码不能为空");
        }
        
        User user = userService.getUserByUserName(username);
        if (user == null) {
            return new APIResponse(RestStatus.FAIL, "用户不存在");
        }
        
        if (!user.password.equals(password)) {
            return new APIResponse(RestStatus.FAIL, "密码错误");
        }
        
        if (!"0".equals(user.status)) {
            return new APIResponse(RestStatus.FAIL, "用户已被禁用");
        }
        
        // 生成token
        String token = TokenUtils.sign(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", token);
        
        return new APIResponse(RestStatus.SUCCESS, "登录成功", data);
    }
    
    @PostMapping("/register")
    public APIResponse register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String email = params.get("email");
        
        // 参数验证
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new APIResponse(RestStatus.FAIL, "用户名和密码不能为空");
        }
        
        // 检查用户名是否已存在
        User existUser = userService.getUserByUserName(username);
        if (existUser != null) {
            return new APIResponse(RestStatus.FAIL, "用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.username = username;
        user.password = password;
        user.email = email;
        user.role = "2"; // 普通用户
        user.status = "0"; // 正常状态
        user.createTime = String.valueOf(System.currentTimeMillis());
        
        userService.createUser(user);
        
        return new APIResponse(RestStatus.SUCCESS, "注册成功");
    }
}
```

**前端登录组件实现**：
```vue
<template>
  <div class="login-container">
    <a-form :model="loginForm" @finish="handleLogin">
      <a-form-item name="username" :rules="[{ required: true, message: '请输入用户名' }]">
        <a-input v-model:value="loginForm.username" placeholder="用户名">
          <template #prefix>
            <UserOutlined />
          </template>
        </a-input>
      </a-form-item>
      
      <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
        <a-input-password v-model:value="loginForm.password" placeholder="密码">
          <template #prefix>
            <LockOutlined />
          </template>
        </a-input-password>
      </a-form-item>
      
      <a-form-item>
        <a-button type="primary" html-type="submit" :loading="loading" block>
          登录
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { userApi } from '/@/api/user'
import { useUserStore } from '/@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await userApi.login(loginForm)
    if (res.code === 200) {
      // 保存用户信息和token
      userStore.setUser(res.data.user)
      userStore.setToken(res.data.token)
      
      message.success('登录成功')
      router.push('/')
    } else {
      message.error(res.msg || '登录失败')
    }
  } catch (error) {
    message.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>
```

### 5.2.2 商品展示模块

**商品实体类设计**：
```java
@Data
@TableName("b_thing")
public class Thing implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    
    @TableField
    public String title;           // 商品标题
    
    @TableField
    public String cover;           // 封面图片
    
    @TableField
    public String description;     // 商品描述
    
    @TableField
    public String price;           // 价格
    
    @TableField
    public String status;          // 状态：0-下架，1-上架
    
    @TableField
    public String repertory;       // 库存
    
    @TableField
    public String score;           // 评分
    
    @TableField
    public String pv;              // 浏览量
    
    @TableField
    public String recommendCount;  // 推荐数
    
    @TableField
    public String wishCount;       // 点赞数
    
    @TableField
    public String collectCount;    // 收藏数
    
    @TableField
    public Long classificationId;  // 分类ID
    
    @TableField
    public String createTime;      // 创建时间
    
    @TableField(exist = false)
    public List<Long> tags;        // 标签列表
}
```

**商品控制器实现**：
```java
@RestController
@RequestMapping("/api/thing")
@CrossOrigin(origins = "*")
public class ThingController {
    
    @Autowired
    private ThingService thingService;
    
    @GetMapping("/list")
    public APIResponse list(@RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer size,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long classificationId,
                           @RequestParam(required = false) Long tag,
                           @RequestParam(defaultValue = "recent") String c) {
        
        Page<Thing> pageInfo = new Page<>(page, size);
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        
        // 只查询上架状态的商品
        queryWrapper.eq("status", "1");
        
        // 关键词搜索
        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("title", keyword)
                       .or()
                       .like("description", keyword);
        }
        
        // 分类筛选
        if (classificationId != null && classificationId > 0) {
            queryWrapper.eq("classification_id", classificationId);
        }
        
        // 标签筛选
        if (tag != null && tag > 0) {
            // 通过商品标签关联表查询
            List<Long> thingIds = thingService.getThingIdsByTag(tag);
            if (!thingIds.isEmpty()) {
                queryWrapper.in("id", thingIds);
            } else {
                // 如果没有找到相关商品，返回空结果
                return new APIResponse(RestStatus.SUCCESS, "查询成功", new PageInfo<>());
            }
        }
        
        // 排序
        switch (c) {
            case "recent":
                queryWrapper.orderByDesc("create_time");
                break;
            case "hot":
                queryWrapper.orderByDesc("pv");
                break;
            case "recommend":
                queryWrapper.orderByDesc("recommend_count");
                break;
            case "price":
                queryWrapper.orderByAsc("price");
                break;
        }
        
        Page<Thing> result = thingService.page(pageInfo, queryWrapper);
        
        // 为每个商品设置标签信息
        for (Thing thing : result.getRecords()) {
            List<Long> tagIds = thingService.getThingTags(thing.id);
            thing.tags = tagIds;
        }
        
        return new APIResponse(RestStatus.SUCCESS, "查询成功", result);
    }
    
    @GetMapping("/detail")
    public APIResponse detail(@RequestParam Long id) {
        Thing thing = thingService.getById(id);
        if (thing == null) {
            return new APIResponse(RestStatus.FAIL, "商品不存在");
        }
        
        // 增加浏览量
        thingService.updatePv(id);
        
        // 获取标签信息
        List<Long> tagIds = thingService.getThingTags(id);
        thing.tags = tagIds;
        
        return new APIResponse(RestStatus.SUCCESS, "查询成功", thing);
    }
}
```

**前端商品列表组件**：
```vue
<template>
  <div class="thing-list">
    <!-- 搜索和筛选区域 -->
    <div class="search-area">
      <a-input-search
        v-model:value="searchForm.keyword"
        placeholder="搜索图书"
        @search="handleSearch"
        style="width: 300px"
      />
      
      <a-select
        v-model:value="searchForm.classificationId"
        placeholder="选择分类"
        style="width: 150px; margin-left: 16px"
        @change="handleSearch"
      >
        <a-select-option value="">全部分类</a-select-option>
        <a-select-option v-for="item in classifications" :key="item.id" :value="item.id">
          {{ item.title }}
        </a-select-option>
      </a-select>
      
      <a-radio-group
        v-model:value="searchForm.c"
        @change="handleSearch"
        style="margin-left: 16px"
      >
        <a-radio-button value="recent">最新</a-radio-button>
        <a-radio-button value="hot">最热</a-radio-button>
        <a-radio-button value="recommend">推荐</a-radio-button>
        <a-radio-button value="price">价格</a-radio-button>
      </a-radio-group>
    </div>
    
    <!-- 商品列表 -->
    <div class="thing-grid">
      <div 
        v-for="item in thingList" 
        :key="item.id" 
        class="thing-item"
        @click="handleViewDetail(item.id)"
      >
        <div class="thing-cover">
          <img :src="getImageUrl(item.cover)" :alt="item.title" />
        </div>
        <div class="thing-info">
          <h3 class="thing-title">{{ item.title }}</h3>
          <p class="thing-desc">{{ item.description }}</p>
          <div class="thing-price">￥{{ item.price }}</div>
          <div class="thing-stats">
            <span>浏览 {{ item.pv }}</span>
            <span>收藏 {{ item.collectCount }}</span>
            <span>推荐 {{ item.recommendCount }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination">
      <a-pagination
        v-model:current="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        show-size-changer
        @change="handlePageChange"
        @showSizeChange="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { thingApi } from '/@/api/thing'
import { classificationApi } from '/@/api/classification'

const router = useRouter()

const thingList = ref([])
const classifications = ref([])
const loading = ref(false)

const searchForm = reactive({
  keyword: '',
  classificationId: '',
  c: 'recent'
})

const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0
})

// 获取商品列表
const getThingList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.pageSize,
      ...searchForm
    }
    
    const res = await thingApi.list(params)
    if (res.code === 200) {
      thingList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    message.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getClassifications = async () => {
  try {
    const res = await classificationApi.list()
    if (res.code === 200) {
      classifications.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  getThingList()
}

// 分页处理
const handlePageChange = (page: number, pageSize: number) => {
  pagination.current = page
  pagination.pageSize = pageSize
  getThingList()
}

// 查看详情
const handleViewDetail = (id: number) => {
  router.push(`/thing/detail/${id}`)
}

// 获取图片URL
const getImageUrl = (cover: string) => {
  return `${import.meta.env.VITE_API_BASE_URL}/api/staticfiles/image/${cover}`
}

onMounted(() => {
  getClassifications()
  getThingList()
})
</script>

<style scoped lang="less">
.thing-list {
  padding: 20px;
}

.search-area {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

.thing-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.thing-item {
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
}

.thing-cover {
  height: 200px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.thing-info {
  padding: 16px;
}

.thing-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.thing-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.thing-price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.thing-stats {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}

.pagination {
  text-align: center;
}
</style>
```

### 5.2.3 购物车模块

**订单实体类设计**：
```java
@Data
@TableName("b_order")
public class Order implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    
    @TableField
    public String orderNumber;     // 订单号
    
    @TableField
    public Long userId;            // 用户ID
    
    @TableField
    public String status;          // 订单状态
    
    @TableField
    public String amount;          // 订单金额
    
    @TableField
    public String payStatus;       // 支付状态
    
    @TableField
    public String payTime;         // 支付时间
    
    @TableField
    public String createTime;      // 创建时间
    
    @TableField
    public String receiverName;    // 收货人姓名
    
    @TableField
    public String receiverAddress; // 收货地址
    
    @TableField
    public String receiverPhone;   // 收货人电话
    
    @TableField
    public String remark;          // 备注
    
    @TableField(exist = false)
    public List<Thing> thingList;  // 订单商品列表
}
```

## 5.3 AI智能客服模块实现

### 5.3.1 AI接口集成

**AI客服控制器完整实现**：
```java
@RestController
@RequestMapping("/ai-chat")
@CrossOrigin(origins = "*")
public class AiChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);
    
    @Autowired
    private ThingService thingService;
    
    @Value("${ai.api.key:}")
    private String apikey;
    
    @Value("${ai.api.url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;
    
    // 缓存商品信息
    private List<Thing> thingCache = new ArrayList<>();
    private long lastCacheUpdate = 0;
    private final long CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存
    
    @PostMapping("/chat")
    public APIResponse chat(@RequestBody Map<String, Object> params) {
        try {
            String message = (String) params.get("message");
            String sessionId = (String) params.get("sessionId");
            
            if (StringUtils.isEmpty(message)) {
                return new APIResponse(RestStatus.FAIL, "消息内容不能为空");
            }
            
            // 获取商品信息用于上下文
            List<Thing> things = getThingInfo();
            String context = buildContextFromThings(things);
            
            // 构建AI请求
            String aiResponse = callAiApi(message, context, sessionId);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("message", aiResponse);
            result.put("timestamp", System.currentTimeMillis());
            
            return new APIResponse(RestStatus.SUCCESS, "AI回复成功", result);
            
        } catch (Exception e) {
            logger.error("AI聊天处理失败", e);
            return new APIResponse(RestStatus.FAIL, "AI服务暂时不可用，请稍后重试");
        }
    }
    
    private String callAiApi(String userMessage, String context, String sessionId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apikey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "ep-20241210083239-v29p6");
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildSystemPrompt(context));
            messages.add(systemMessage);
            
            // 用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    return message.get("content");
                }
            }
            
            return "抱歉，我现在无法回答您的问题，请稍后重试。";
            
        } catch (HttpClientErrorException e) {
            logger.error("AI API调用失败: {}", e.getResponseBodyAsString());
            return "服务暂时不可用，请稍后重试。";
        } catch (Exception e) {
            logger.error("AI API调用异常", e);
            return "抱歉，出现了技术问题，请稍后重试。";
        }
    }
    
    private String buildSystemPrompt(String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的图书推荐助手，负责为用户提供图书咨询和推荐服务。");
        prompt.append("请根据以下图书信息回答用户问题：\n\n");
        prompt.append(context);
        prompt.append("\n\n请注意：");
        prompt.append("1. 优先推荐库存充足的图书");
        prompt.append("2. 根据用户需求提供个性化推荐");
        prompt.append("3. 回答要友好、专业、有帮助");
        prompt.append("4. 如果用户询问的图书不在列表中，可以礼貌地告知并推荐相似的图书");
        
        return prompt.toString();
    }
    
    private String buildContextFromThings(List<Thing> things) {
        StringBuilder context = new StringBuilder();
        context.append("当前可售图书信息：\n");
        
        for (Thing thing : things) {
            if ("1".equals(thing.status) && Integer.parseInt(thing.repertory) > 0) {
                context.append(String.format("书名：%s，价格：%s元，库存：%s，评分：%s，描述：%s\n",
                    thing.title, thing.price, thing.repertory, thing.score, thing.description));
            }
        }
        
        return context.toString();
    }
    
    private List<Thing> getThingInfo() {
        long currentTime = System.currentTimeMillis();
        
        // 检查缓存是否过期
        if (currentTime - lastCacheUpdate > CACHE_DURATION || thingCache.isEmpty()) {
            try {
                QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("status", "1"); // 只获取上架商品
                queryWrapper.orderByDesc("recommend_count");
                queryWrapper.last("LIMIT 50"); // 限制数量，避免上下文过长
                
                thingCache = thingService.list(queryWrapper);
                lastCacheUpdate = currentTime;
                
                logger.info("更新商品缓存，共{}个商品", thingCache.size());
            } catch (Exception e) {
                logger.error("获取商品信息失败", e);
                // 如果获取失败，返回空列表或使用旧缓存
                if (thingCache.isEmpty()) {
                    thingCache = new ArrayList<>();
                }
            }
        }
        
        return thingCache;
    }
}
```

### 5.3.2 前端AI聊天组件

**AI聊天界面实现**：
```vue
<template>
  <div class="ai-chat-container">
    <div class="chat-header">
      <h3>智能客服</h3>
      <a-button @click="clearChat" size="small">清除对话</a-button>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="msg in messages" :key="msg.id" class="message-item">
        <div v-if="msg.type === 'user'" class="user-message">
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
        </div>
        <div v-else class="ai-message">
          <div class="ai-avatar">🤖</div>
          <div class="message-wrapper">
            <div class="message-content" v-html="formatAiMessage(msg.content)"></div>
            <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
          </div>
        </div>
      </div>
      
      <div v-if="loading" class="typing-indicator">
        <div class="ai-avatar">🤖</div>
        <div class="typing-dots">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <a-input
        v-model:value="inputMessage"
        placeholder="请输入您想咨询的问题..."
        @press-enter="sendMessage"
        :disabled="loading"
      />
      <a-button 
        type="primary" 
        @click="sendMessage" 
        :loading="loading"
        :disabled="!inputMessage.trim()"
      >
        发送
      </a-button>
    </div>
    
    <!-- 快捷问题 -->
    <div class="quick-questions" v-if="messages.length === 0">
      <h4>常见问题：</h4>
      <a-tag 
        v-for="question in quickQuestions" 
        :key="question"
        @click="sendQuickQuestion(question)"
        style="cursor: pointer; margin: 4px;"
      >
        {{ question }}
      </a-tag>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { aiChatApi } from '/@/api/ai-chat'

interface Message {
  id: string
  type: 'user' | 'ai'
  content: string
  timestamp: number
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref<HTMLElement>()
const sessionId = ref('')

const quickQuestions = [
  '有什么热门图书推荐？',
  '如何查看订单状态？',
  '支持哪些支付方式？',
  '图书可以退换吗？',
  '有编程类图书吗？',
  '如何联系客服？'
]

// 生成会话ID
const generateSessionId = () => {
  return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

// 发送消息
const sendMessage = async () => {
  const text = inputMessage.value.trim()
  if (!text || loading.value) return
  
  // 添加用户消息
  const userMessage: Message = {
    id: 'user_' + Date.now(),
    type: 'user',
    content: text,
    timestamp: Date.now()
  }
  messages.value.push(userMessage)
  inputMessage.value = ''
  
  // 滚动到底部
  scrollToBottom()
  
  // 发送到AI
  loading.value = true
  try {
    const response = await aiChatApi.chat({
      message: text,
      sessionId: sessionId.value
    })
    
    if (response.code === 200) {
      const aiMessage: Message = {
        id: 'ai_' + Date.now(),
        type: 'ai',
        content: response.data.message,
        timestamp: Date.now()
      }
      messages.value.push(aiMessage)
    } else {
      throw new Error(response.msg || 'AI服务错误')
    }
  } catch (error) {
    console.error('AI聊天错误:', error)
    const errorMessage: Message = {
      id: 'ai_error_' + Date.now(),
      type: 'ai',
      content: '抱歉，我现在无法回答您的问题，请稍后重试。',
      timestamp: Date.now()
    }
    messages.value.push(errorMessage)
    message.error('发送消息失败')
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

// 发送快捷问题
const sendQuickQuestion = (question: string) => {
  inputMessage.value = question
  sendMessage()
}

// 清除对话
const clearChat = () => {
  messages.value = []
  sessionId.value = generateSessionId()
}

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

// 格式化AI消息（支持换行和简单格式）
const formatAiMessage = (content: string) => {
  return content.replace(/\n/g, '<br>')
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

onMounted(() => {
  sessionId.value = generateSessionId()
  
  // 添加欢迎消息
  const welcomeMessage: Message = {
    id: 'welcome',
    type: 'ai',
    content: '您好！我是智能客服助手，很高兴为您服务。我可以帮您推荐图书、查询订单、解答疑问等。请问有什么可以帮助您的吗？',
    timestamp: Date.now()
  }
  messages.value.push(welcomeMessage)
})
</script>

<style scoped lang="less">
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 600px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  background: #fff;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
  
  h3 {
    margin: 0;
    color: #333;
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f5f5;
}

.message-item {
  margin-bottom: 16px;
}

.user-message {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  
  .message-content {
    background: #1890ff;
    color: white;
    padding: 12px 16px;
    border-radius: 18px;
    max-width: 70%;
    word-wrap: break-word;
  }
  
  .message-time {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
  }
}

.ai-message {
  display: flex;
  align-items: flex-start;
  
  .ai-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #52c41a;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
    font-size: 18px;
  }
  
  .message-wrapper {
    flex: 1;
    max-width: 70%;
  }
  
  .message-content {
    background: white;
    padding: 12px 16px;
    border-radius: 18px;
    word-wrap: break-word;
    line-height: 1.5;
  }
  
  .message-time {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
    margin-left: 16px;
  }
}

.typing-indicator {
  display: flex;
  align-items: center;
  
  .ai-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #52c41a;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
    font-size: 18px;
  }
  
  .typing-dots {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: white;
    border-radius: 18px;
    
    span {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #ccc;
      margin: 0 2px;
      animation: typing 1.4s infinite;
      
      &:nth-child(2) {
        animation-delay: 0.2s;
      }
      
      &:nth-child(3) {
        animation-delay: 0.4s;
      }
    }
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.chat-input {
  display: flex;
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  background: white;
  
  .ant-input {
    flex: 1;
    margin-right: 12px;
  }
}

.quick-questions {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  
  h4 {
    margin: 0 0 12px 0;
    color: #666;
    font-size: 14px;
  }
}
</style>
```

这样，我们就完成了智能书城管理系统的主要实现内容，包括用户认证、商品展示、购物车功能和AI智能客服等核心模块。每个模块都提供了完整的前后端实现代码示例，展示了现代Web开发的最佳实践。