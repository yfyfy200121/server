# 智能书城管理系统功能模块详细分析

## 一、前端功能模块分析

### 1.1 用户端功能

#### 1.1.1 首页模块 (Index.vue)
**主要功能**:
- 商品轮播展示
- 热门商品推荐  
- 商品分类导航
- 搜索功能
- AI客服入口

**技术实现**:
```vue
<template>
  <div class="home-page">
    <!-- 轮播图组件 -->
    <a-carousel autoplay>
      <div v-for="banner in banners" :key="banner.id">
        <img :src="banner.image" :alt="banner.title" />
      </div>
    </a-carousel>
    
    <!-- 商品推荐 -->
    <div class="goods-recommendation">
      <h2>热门推荐</h2>
      <goods-card 
        v-for="item in recommendGoods" 
        :key="item.id" 
        :goods="item"
        @click="goToDetail(item.id)"
      />
    </div>
    
    <!-- AI客服 -->
    <ai-chat v-if="showAiChat" @close="showAiChat = false" />
  </div>
</template>
```

#### 1.1.2 商品模块

**商品列表页 (GoodsList.vue)**:
- 商品搜索和筛选
- 分类过滤
- 价格区间筛选
- 排序功能 (价格、销量、评分)
- 分页展示

**商品详情页 (GoodsDetail.vue)**:
- 商品详细信息展示
- 商品图片预览
- 规格选择
- 数量选择
- 加入购物车
- 立即购买
- AI咨询功能
- 商品评价展示

**技术实现**:
```vue
<template>
  <div class="goods-detail">
    <!-- 商品图片 -->
    <div class="goods-images">
      <a-image-preview-group>
        <a-image :src="goods.cover" :alt="goods.title" />
      </a-image-preview-group>
    </div>
    
    <!-- 商品信息 -->
    <div class="goods-info">
      <h1>{{ goods.title }}</h1>
      <div class="price">¥{{ goods.price }}</div>
      <div class="stock">库存: {{ goods.repertory }}</div>
      
      <!-- 购买操作 -->
      <div class="actions">
        <a-button type="primary" @click="buyNow">立即购买</a-button>
        <a-button @click="addToCart">加入购物车</a-button>
        <a-button @click="openAiChat">咨询AI客服</a-button>
      </div>
    </div>
    
    <!-- 商品评价 -->
    <div class="comments">
      <h3>商品评价</h3>
      <comment-list :goods-id="goods.id" />
    </div>
  </div>
</template>
```

#### 1.1.3 订单模块

**订单确认页 (OrderConfirm.vue)**:
- 收货地址选择
- 商品信息确认
- 支付方式选择
- 订单备注
- 费用计算

**订单列表页 (OrderList.vue)**:
- 订单状态筛选
- 订单信息展示
- 订单操作 (支付、取消、确认收货)
- 订单详情查看

#### 1.1.4 用户模块

**用户登录 (Login.vue)**:
- 用户名/邮箱登录
- 密码验证
- 记住登录状态
- 注册跳转

**用户中心 (Profile.vue)**:
- 个人信息管理
- 头像上传
- 密码修改
- 我的订单
- 收货地址管理
- 我的收藏

### 1.2 管理端功能

#### 1.2.1 系统概览 (Overview.vue)
- 数据统计图表
- 用户数量统计
- 订单数量统计
- 商品数量统计
- 系统运行状态

#### 1.2.2 用户管理 (UserManagement.vue)
- 用户列表展示
- 用户信息编辑
- 用户状态管理
- 权限设置
- 用户搜索

#### 1.2.3 商品管理 (ThingManagement.vue)
- 商品列表管理
- 商品信息编辑
- 商品上下架
- 商品图片管理
- 商品分类设置
- 库存管理

**核心实现**:
```vue
<template>
  <div class="thing-management">
    <!-- 搜索和操作栏 -->
    <div class="toolbar">
      <a-input-search 
        v-model:value="searchKeyword"
        placeholder="搜索商品"
        @search="searchThings"
      />
      <a-button type="primary" @click="addThing">添加商品</a-button>
    </div>
    
    <!-- 商品列表 -->
    <a-table 
      :dataSource="thingList"
      :columns="columns"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <!-- 商品图片列 -->
      <template #cover="{ record }">
        <a-image :src="getImageUrl(record.cover)" width="60" />
      </template>
      
      <!-- 状态列 -->
      <template #status="{ record }">
        <a-tag :color="record.status === '0' ? 'green' : 'red'">
          {{ record.status === '0' ? '上架' : '下架' }}
        </a-tag>
      </template>
      
      <!-- 操作列 -->
      <template #action="{ record }">
        <a-button size="small" @click="editThing(record)">编辑</a-button>
        <a-popconfirm title="确认删除?" @confirm="deleteThing(record.id)">
          <a-button size="small" danger>删除</a-button>
        </a-popconfirm>
      </template>
    </a-table>
    
    <!-- 编辑弹窗 -->
    <a-modal v-model:visible="editModalVisible" title="编辑商品" @ok="saveThing">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="商品名称" required>
          <a-input v-model:value="editForm.title" />
        </a-form-item>
        <a-form-item label="商品价格" required>
          <a-input-number v-model:value="editForm.price" />
        </a-form-item>
        <a-form-item label="库存数量" required>
          <a-input-number v-model:value="editForm.repertory" />
        </a-form-item>
        <a-form-item label="商品封面">
          <a-upload 
            :before-upload="beforeUpload"
            :file-list="fileList"
            list-type="picture-card"
          >
            <div>
              <plus-outlined />
              <div>上传图片</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="商品描述">
          <a-textarea v-model:value="editForm.description" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
```

#### 1.2.4 订单管理 (OrderManagement.vue)
- 订单列表查看
- 订单状态更新
- 订单详情查看
- 发货管理
- 退款处理

#### 1.2.5 分类管理 (ClassificationManagement.vue)
- 商品分类树形结构
- 分类增删改查
- 分类排序
- 分类状态管理

## 二、后端功能模块分析

### 2.1 Controller层API接口

#### 2.1.1 用户管理接口 (UserController.java)
```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    // 用户注册
    @PostMapping("/register")
    public APIResponse register(@RequestBody User user) {
        // 1. 验证用户信息
        if (StringUtils.isEmpty(user.username) || StringUtils.isEmpty(user.password)) {
            return APIResponse.error("用户名和密码不能为空");
        }
        
        // 2. 检查用户名是否存在
        if (userService.checkUserExists(user.username)) {
            return APIResponse.error("用户名已存在");
        }
        
        // 3. 创建用户
        User newUser = userService.createUser(user);
        return APIResponse.success(newUser);
    }
    
    // 用户登录
    @PostMapping("/login")
    public APIResponse login(@RequestBody User user) {
        // 1. 验证用户名密码
        User loginUser = userService.validateUser(user.username, user.password);
        if (loginUser == null) {
            return APIResponse.error("用户名或密码错误");
        }
        
        // 2. 生成Token
        String token = jwtUtil.generateToken(loginUser.id);
        loginUser.token = token;
        
        // 3. 更新登录时间
        userService.updateLoginTime(loginUser.id);
        
        return APIResponse.success(loginUser);
    }
    
    // 获取用户信息
    @GetMapping("/info")
    public APIResponse getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserById(userId);
        return APIResponse.success(user);
    }
    
    // 更新用户信息
    @PostMapping("/update")
    public APIResponse updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return success ? APIResponse.success("更新成功") : APIResponse.error("更新失败");
    }
}
```

#### 2.1.2 商品管理接口 (ThingController.java)
```java
@RestController
@RequestMapping("/thing")
public class ThingController {
    
    // 获取商品列表
    @GetMapping("/list")
    public APIResponse getThingList(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false) String c,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        // 1. 构建查询条件
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like("title", keyword)
                       .or()
                       .like("description", keyword);
        }
        
        // 分类筛选
        if (StringUtils.isNotEmpty(c)) {
            queryWrapper.eq("classification_id", c);
        }
        
        // 状态筛选 (只显示上架商品)
        queryWrapper.eq("status", "0");
        
        // 排序
        if ("price_asc".equals(sort)) {
            queryWrapper.orderByAsc("price");
        } else if ("price_desc".equals(sort)) {
            queryWrapper.orderByDesc("price");
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        
        // 2. 分页查询
        Page<Thing> pageable = new Page<>(page, size);
        Page<Thing> result = thingService.page(pageable, queryWrapper);
        
        return APIResponse.success(result);
    }
    
    // 获取商品详情
    @GetMapping("/detail")
    public APIResponse getThingDetail(@RequestParam String id) {
        Thing thing = thingService.getThingWithComments(id);
        if (thing == null) {
            return APIResponse.error("商品不存在");
        }
        
        // 增加浏览量
        thingService.incrementPv(id);
        
        return APIResponse.success(thing);
    }
    
    // 创建商品 (管理员权限)
    @Access(level = AccessLevel.ADMIN)
    @PostMapping("/create")
    @Transactional
    public APIResponse createThing(Thing thing) throws IOException {
        // 1. 处理图片上传
        String coverUrl = saveThingImage(thing);
        if (coverUrl != null) {
            thing.cover = coverUrl;
        }
        
        // 2. 设置创建时间
        thing.createTime = DateUtils.getCurrentTime();
        
        // 3. 保存商品
        boolean success = thingService.save(thing);
        
        // 4. 处理标签关联
        if (thing.tags != null && !thing.tags.isEmpty()) {
            thingTagService.saveThingTags(thing.id, thing.tags);
        }
        
        return success ? APIResponse.success("创建成功") : APIResponse.error("创建失败");
    }
    
    // 更新商品
    @Access(level = AccessLevel.ADMIN)
    @PostMapping("/update")
    @Transactional
    public APIResponse updateThing(Thing thing) throws IOException {
        // 1. 处理图片上传
        String coverUrl = saveThingImage(thing);
        if (coverUrl != null) {
            thing.cover = coverUrl;
        }
        
        // 2. 更新商品信息
        boolean success = thingService.updateById(thing);
        
        // 3. 更新标签关联
        if (thing.tags != null) {
            thingTagService.updateThingTags(thing.id, thing.tags);
        }
        
        // 4. 清除缓存
        redisUtil.delete("thing:detail:" + thing.id);
        redisUtil.delete("thing:list:*");
        
        return success ? APIResponse.success("更新成功") : APIResponse.error("更新失败");
    }
    
    // 图片上传处理
    private String saveThingImage(Thing thing) throws IOException {
        MultipartFile file = thing.getImageFile();
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + 
                            file.getOriginalFilename().substring(
                                file.getOriginalFilename().lastIndexOf("."));
            
            String filePath = uploadPath + File.separator + "image" + File.separator + fileName;
            File destFile = new File(filePath);
            
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            
            file.transferTo(destFile);
            return fileName;
        }
        return null;
    }
}
```

#### 2.1.3 订单管理接口 (OrderController.java)
```java
@RestController
@RequestMapping("/order")
public class OrderController {
    
    // 创建订单
    @PostMapping("/create")
    @Transactional
    public APIResponse createOrder(@RequestBody Order order, HttpServletRequest request) {
        // 1. 获取用户信息
        String token = request.getHeader("Authorization");
        String userId = jwtUtil.getUserIdFromToken(token);
        order.userId = userId;
        
        // 2. 验证商品信息
        Thing thing = thingService.getById(order.thingId);
        if (thing == null) {
            return APIResponse.error("商品不存在");
        }
        
        // 3. 检查库存
        int requestCount = Integer.parseInt(order.count);
        int currentStock = Integer.parseInt(thing.repertory);
        if (currentStock < requestCount) {
            return APIResponse.error("库存不足");
        }
        
        // 4. 生成订单号
        order.orderNumber = generateOrderNumber();
        order.orderTime = DateUtils.getCurrentTime();
        order.status = "0"; // 待支付
        
        // 5. 创建订单
        boolean success = orderService.save(order);
        if (!success) {
            return APIResponse.error("订单创建失败");
        }
        
        // 6. 扣减库存
        thing.repertory = String.valueOf(currentStock - requestCount);
        thingService.updateById(thing);
        
        // 7. 清除商品缓存
        redisUtil.delete("thing:detail:" + thing.id);
        
        return APIResponse.success(order);
    }
    
    // 获取用户订单列表
    @GetMapping("/list")
    public APIResponse getUserOrders(
        HttpServletRequest request,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status
    ) {
        // 1. 获取用户ID
        String token = request.getHeader("Authorization");
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 2. 构建查询条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (StringUtils.isNotEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("order_time");
        
        // 3. 分页查询
        Page<Order> pageable = new Page<>(page, size);
        Page<Order> result = orderService.getOrdersWithDetails(pageable, queryWrapper);
        
        return APIResponse.success(result);
    }
    
    // 更新订单状态
    @PostMapping("/updateStatus")
    public APIResponse updateOrderStatus(@RequestBody Map<String, String> params) {
        String orderId = params.get("orderId");
        String status = params.get("status");
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return APIResponse.error("订单不存在");
        }
        
        order.status = status;
        if ("1".equals(status)) {
            order.payTime = DateUtils.getCurrentTime();
        }
        
        boolean success = orderService.updateById(order);
        return success ? APIResponse.success("更新成功") : APIResponse.error("更新失败");
    }
    
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + 
               (int)(Math.random() * 1000);
    }
}
```

#### 2.1.4 AI客服接口 (AiChatController.java)
```java
@RestController
@RequestMapping("/ai-chat")
public class AiChatController {
    
    @Autowired
    private AiChatService aiChatService;
    
    // AI聊天接口
    @PostMapping("/message")
    public APIResponse aiChat(@RequestBody Map<String, String> params) {
        String userMessage = params.get("message");
        
        if (StringUtils.isEmpty(userMessage)) {
            return APIResponse.error("消息内容不能为空");
        }
        
        try {
            String aiResponse = aiChatService.processAiChat(userMessage);
            
            Map<String, Object> result = new HashMap<>();
            result.put("response", aiResponse);
            result.put("timestamp", System.currentTimeMillis());
            
            return APIResponse.success(result);
        } catch (Exception e) {
            logger.error("AI服务调用失败", e);
            return APIResponse.error("AI服务暂时不可用，请稍后重试");
        }
    }
    
    // 刷新商品知识库
    @Access(level = AccessLevel.ADMIN)
    @PostMapping("/refresh-knowledge")
    public APIResponse refreshKnowledge() {
        try {
            boolean success = aiChatService.refreshGoodsKnowledge();
            return success ? APIResponse.success("知识库刷新成功") : 
                           APIResponse.error("知识库刷新失败");
        } catch (Exception e) {
            logger.error("知识库刷新失败", e);
            return APIResponse.error("知识库刷新失败");
        }
    }
}
```

### 2.2 Service层业务逻辑

#### 2.2.1 用户服务 (UserServiceImpl.java)
```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User createUser(User user) {
        // 1. 密码加密
        user.password = passwordEncoder.encode(user.password);
        
        // 2. 设置默认值
        user.role = String.valueOf(User.NormalUser);
        user.status = "1"; // 正常状态
        user.score = "0";
        user.createTime = DateUtils.getCurrentTime();
        
        // 3. 保存用户
        this.save(user);
        
        // 4. 清除敏感信息
        user.password = null;
        
        return user;
    }
    
    @Override
    public User validateUser(String username, String password) {
        // 1. 查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);
        
        if (user == null) {
            return null;
        }
        
        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.password)) {
            return null;
        }
        
        // 3. 检查用户状态
        if (!"1".equals(user.status)) {
            throw new BusinessException("用户已被禁用");
        }
        
        // 4. 清除敏感信息
        user.password = null;
        
        return user;
    }
    
    @Override
    public boolean checkUserExists(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.count(queryWrapper) > 0;
    }
}
```

#### 2.2.2 商品服务 (ThingServiceImpl.java)
```java
@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private CommentService commentService;
    
    @Override
    public Thing getThingWithComments(String id) {
        // 1. 尝试从缓存获取
        String cacheKey = "thing:detail:" + id;
        String cachedData = redisUtil.get(cacheKey);
        
        if (StringUtils.isNotEmpty(cachedData)) {
            Thing thing = JSON.parseObject(cachedData, Thing.class);
            // 获取最新评论
            thing.comments = commentService.getCommentsByThingId(id);
            return thing;
        }
        
        // 2. 从数据库获取
        Thing thing = this.getById(id);
        if (thing == null) {
            return null;
        }
        
        // 3. 获取评论
        thing.comments = commentService.getCommentsByThingId(id);
        
        // 4. 缓存商品信息（不包含评论）
        Thing cacheData = JSON.parseObject(JSON.toJSONString(thing), Thing.class);
        cacheData.comments = null;
        redisUtil.set(cacheKey, JSON.toJSONString(cacheData), 300); // 5分钟过期
        
        return thing;
    }
    
    @Override
    public void incrementPv(String id) {
        // 1. 更新数据库
        Thing thing = this.getById(id);
        if (thing != null) {
            int currentPv = StringUtils.isNotEmpty(thing.pv) ? 
                           Integer.parseInt(thing.pv) : 0;
            thing.pv = String.valueOf(currentPv + 1);
            this.updateById(thing);
            
            // 2. 更新缓存
            String cacheKey = "thing:detail:" + id;
            redisUtil.delete(cacheKey);
        }
    }
    
    @Override
    public List<Thing> getRecommendThings(int limit) {
        String cacheKey = "thing:recommend:" + limit;
        String cachedData = redisUtil.get(cacheKey);
        
        if (StringUtils.isNotEmpty(cachedData)) {
            return JSON.parseArray(cachedData, Thing.class);
        }
        
        // 基于浏览量推荐
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "0")
                   .orderByDesc("pv")
                   .orderByDesc("score")
                   .last("LIMIT " + limit);
        
        List<Thing> recommendThings = this.list(queryWrapper);
        
        // 缓存推荐结果
        redisUtil.set(cacheKey, JSON.toJSONString(recommendThings), 600); // 10分钟过期
        
        return recommendThings;
    }
}
```

#### 2.2.3 AI服务 (AiChatServiceImpl.java)
```java
@Service
public class AiChatServiceImpl implements AiChatService {
    
    private static final String AI_API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";
    private static final String AI_API_KEY = "your-api-key";
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private ThingService thingService;
    
    @Override
    public String processAiChat(String userMessage) {
        try {
            // 1. 获取商品知识库
            String goodsKnowledge = getGoodsKnowledge();
            
            // 2. 构建AI请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-v3");
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是智能书城的AI客服助手。基于以下商品信息回答用户问题，要亲切友好，专业准确。\n\n商品信息:\n" + goodsKnowledge);
            messages.add(systemMessage);
            
            // 用户消息
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", userMessage);
            messages.add(userMessageMap);
            
            requestBody.put("messages", messages);
            
            // 3. 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + AI_API_KEY);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 4. 调用AI API
            ResponseEntity<Map> response = restTemplate.postForEntity(AI_API_URL, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    return message.get("content");
                }
            }
            
            return "抱歉，我暂时无法回答您的问题，请稍后重试。";
            
        } catch (Exception e) {
            logger.error("AI服务调用失败", e);
            throw new RuntimeException("AI服务调用失败", e);
        }
    }
    
    @Override
    public boolean refreshGoodsKnowledge() {
        try {
            // 1. 获取所有上架商品
            QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "0");
            List<Thing> things = thingService.list(queryWrapper);
            
            // 2. 构建知识库内容
            StringBuilder knowledge = new StringBuilder();
            for (Thing thing : things) {
                knowledge.append("商品ID: ").append(thing.id).append("\n");
                knowledge.append("商品名称: ").append(thing.title).append("\n");
                knowledge.append("商品价格: ").append(thing.price).append("元\n");
                knowledge.append("库存数量: ").append(thing.repertory).append("\n");
                knowledge.append("商品描述: ").append(thing.description).append("\n");
                knowledge.append("商品评分: ").append(thing.score).append("\n");
                knowledge.append("---\n");
            }
            
            // 3. 缓存知识库
            String cacheKey = "ai:goods:knowledge";
            redisUtil.set(cacheKey, knowledge.toString(), 3600); // 1小时过期
            
            return true;
        } catch (Exception e) {
            logger.error("刷新商品知识库失败", e);
            return false;
        }
    }
    
    private String getGoodsKnowledge() {
        String cacheKey = "ai:goods:knowledge";
        String knowledge = redisUtil.get(cacheKey);
        
        if (StringUtils.isEmpty(knowledge)) {
            // 如果缓存中没有，则刷新知识库
            refreshGoodsKnowledge();
            knowledge = redisUtil.get(cacheKey);
        }
        
        return StringUtils.isNotEmpty(knowledge) ? knowledge : "暂无商品信息";
    }
}
```

## 三、数据流程分析

### 3.1 用户购买流程
```
用户浏览商品 → 商品详情页 → 加入购物车/立即购买 → 确认订单 → 选择支付方式 → 完成支付 → 订单完成
```

### 3.2 AI客服交互流程
```
用户发起咨询 → 前端AiChat组件 → 后端AiChatController → AiChatService → 火山引擎API → 返回AI回复 → 前端展示
```

### 3.3 商品管理流程
```
管理员登录 → 商品管理页面 → 添加/编辑商品 → 图片上传 → 保存商品信息 → 更新缓存 → 前端展示更新
```

这个功能模块分析为你的论文提供了详细的技术实现说明，可以作为系统实现章节的重要参考。