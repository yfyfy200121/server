# 开发指南

## 开发环境配置

### IDE 推荐配置

#### 后端开发 (IntelliJ IDEA)

1. **安装必要插件**:
   - Lombok Plugin
   - MyBatis X
   - Spring Boot Assistant
   - Maven Helper

2. **代码风格配置**:
   - File → Settings → Editor → Code Style → Java
   - 使用4个空格缩进
   - 设置行长度为120字符

3. **Live Templates**:
   ```java
   // 创建Controller模板
   @RestController
   @RequestMapping("/api/$CLASS_NAME$")
   public class $CLASS_NAME$Controller {
       
       @Autowired
       private $CLASS_NAME$Service $FIELD_NAME$Service;
       
   }
   ```

#### 前端开发 (VS Code)

1. **推荐插件**:
   - Vue Language Features (Volar)
   - TypeScript Vue Plugin (Volar)
   - ESLint
   - Prettier
   - Auto Rename Tag
   - GitLens

2. **配置文件** (`.vscode/settings.json`):
   ```json
   {
     "editor.codeActionsOnSave": {
       "source.fixAll.eslint": true
     },
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode",
     "typescript.preferences.importModuleSpecifier": "relative"
   }
   ```

## 代码规范

### 后端代码规范

#### 1. 命名规范
```java
// 类名：使用大驼峰命名
public class UserController {}

// 方法名：使用小驼峰命名
public void getUserInfo() {}

// 常量：全大写，下划线分隔
public static final String API_BASE_URL = "/api";

// 变量名：使用小驼峰命名
private String userName;
```

#### 2. 注释规范
```java
/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 * 
 * @author yfyfy200121
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求参数
     * @return 登录结果包含用户信息和token
     */
    @PostMapping("/login")
    public APIResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 具体实现...
    }
}
```

#### 3. 异常处理
```java
// 自定义业务异常
public class BusinessException extends RuntimeException {
    private final int code;
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}

// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public APIResponse<?> handleBusinessException(BusinessException e) {
        return APIResponse.error(e.getCode(), e.getMessage());
    }
}
```

### 前端代码规范

#### 1. 组件命名
```vue
<!-- 文件名：UserProfile.vue -->
<template>
  <div class="user-profile">
    <!-- 内容 -->
  </div>
</template>

<script setup lang="ts">
// 组件名使用大驼峰
defineOptions({
  name: 'UserProfile'
})
</script>
```

#### 2. TypeScript 类型定义
```typescript
// types/user.ts
export interface User {
  id: number
  username: string
  email: string
  createdAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  user: User
  token: string
}
```

#### 3. API 调用规范
```typescript
// api/user.ts
import { request } from '@/utils/request'
import type { User, LoginRequest, LoginResponse } from '@/types/user'

export const userApi = {
  // 用户登录
  login: (data: LoginRequest): Promise<LoginResponse> => {
    return request.post('/api/user/login', data)
  },
  
  // 获取用户信息
  getProfile: (): Promise<User> => {
    return request.get('/api/user/profile')
  }
}
```

## Git 工作流

### 分支管理策略

```
master (主分支)
├── develop (开发分支)
│   ├── feature/user-management (功能分支)
│   ├── feature/ai-chat (功能分支)
│   └── bugfix/login-issue (bug修复分支)
└── release/v1.0.0 (发布分支)
```

### 提交信息规范

使用 Conventional Commits 规范：

```bash
# 功能开发
git commit -m "feat: 添加用户登录功能"
git commit -m "feat(api): 实现商品查询接口"

# Bug修复
git commit -m "fix: 修复登录时密码验证问题"
git commit -m "fix(ui): 修复商品列表样式错误"

# 文档更新
git commit -m "docs: 更新API文档"

# 代码重构
git commit -m "refactor: 重构用户服务代码"

# 性能优化
git commit -m "perf: 优化商品查询性能"

# 测试相关
git commit -m "test: 添加用户服务单元测试"
```

### Pull Request 流程

1. **创建功能分支**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/your-feature-name
   ```

2. **开发完成后提交**
   ```bash
   git add .
   git commit -m "feat: your feature description"
   git push origin feature/your-feature-name
   ```

3. **创建 Pull Request**
   - 标题简洁明了
   - 详细描述修改内容
   - 关联相关 Issue
   - 添加截图（如有UI变更）

4. **Code Review**
   - 至少一个reviewer审核
   - 修复review意见
   - 通过所有检查后合并

## 测试策略

### 后端测试

#### 1. 单元测试
```java
@SpringBootTest
@Transactional
public class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testCreateUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        
        // When
        User createdUser = userService.createUser(user);
        
        // Then
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
    }
}
```

#### 2. 集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testUserLogin() {
        // 测试用户登录接口
        LoginRequest request = new LoginRequest("testuser", "password");
        
        ResponseEntity<APIResponse> response = restTemplate.postForEntity(
            "/api/user/login", request, APIResponse.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### 前端测试

#### 1. 组件测试
```typescript
// UserProfile.test.ts
import { mount } from '@vue/test-utils'
import UserProfile from '@/components/UserProfile.vue'

describe('UserProfile', () => {
  it('should render user information correctly', () => {
    const wrapper = mount(UserProfile, {
      props: {
        user: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com'
        }
      }
    })
    
    expect(wrapper.find('.username').text()).toBe('testuser')
    expect(wrapper.find('.email').text()).toBe('test@example.com')
  })
})
```

#### 2. E2E测试
```typescript
// e2e/login.spec.ts
import { test, expect } from '@playwright/test'

test('user can login successfully', async ({ page }) => {
  await page.goto('/login')
  
  await page.fill('[data-testid="username"]', 'testuser')
  await page.fill('[data-testid="password"]', 'password')
  await page.click('[data-testid="login-button"]')
  
  await expect(page).toHaveURL('/dashboard')
})
```

## 数据库开发

### 迁移脚本规范

```sql
-- V1.0.1__Create_user_table.sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 索引优化建议

```sql
-- 复合索引
CREATE INDEX idx_goods_category_status ON goods(category_id, status);

-- 全文索引
CREATE FULLTEXT INDEX ft_goods_title_desc ON goods(title, description);

-- 分析索引使用情况
EXPLAIN SELECT * FROM goods WHERE category_id = 1 AND status = 1;
```

## 性能优化

### 后端优化

#### 1. 数据库查询优化
```java
// 使用分页查询
@Override
public IPage<Goods> getGoodsList(Page<Goods> page, GoodsQuery query) {
    LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
    
    if (StringUtils.isNotBlank(query.getKeyword())) {
        wrapper.like(Goods::getTitle, query.getKeyword())
               .or()
               .like(Goods::getDescription, query.getKeyword());
    }
    
    if (query.getCategoryId() != null) {
        wrapper.eq(Goods::getCategoryId, query.getCategoryId());
    }
    
    return goodsMapper.selectPage(page, wrapper);
}
```

#### 2. Redis缓存使用
```java
@Service
public class GoodsServiceImpl implements GoodsService {
    
    @Cacheable(value = "goods", key = "#id")
    public Goods getGoodsById(Long id) {
        return goodsMapper.selectById(id);
    }
    
    @CacheEvict(value = "goods", key = "#goods.id")
    public void updateGoods(Goods goods) {
        goodsMapper.updateById(goods);
    }
}
```

### 前端优化

#### 1. 组件懒加载
```typescript
// router/index.ts
const routes = [
  {
    path: '/goods',
    component: () => import('@/views/goods/GoodsDetail.vue')
  }
]
```

#### 2. 图片懒加载
```vue
<template>
  <img v-lazy="item.cover" :alt="item.title" />
</template>
```

## 调试技巧

### 后端调试

#### 1. 日志配置
```properties
# logback-spring.xml
logging.level.com.gk.study.mapper=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### 2. 远程调试
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
```

### 前端调试

#### 1. Vue DevTools
- 安装 Vue DevTools 浏览器插件
- 查看组件状态和事件

#### 2. 网络请求调试
```typescript
// request.ts
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(config => {
  console.log('Request:', config)
  return config
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    console.log('Response:', response)
    return response.data
  },
  error => {
    console.error('Error:', error)
    return Promise.reject(error)
  }
)
```

## 最佳实践

### 1. 代码质量
- 使用 SonarQube 进行代码质量检查
- 保持代码覆盖率 > 80%
- 定期进行代码重构

### 2. 安全考虑
- 输入验证和参数校验
- SQL注入防护
- XSS攻击防护
- 敏感信息加密

### 3. 监控和日志
- 关键业务操作记录日志
- 性能指标监控
- 异常告警机制

### 4. 版本管理
- 语义化版本号
- 变更日志维护
- 发布流程标准化

## 团队协作

### 1. 代码审查清单
- [ ] 代码风格符合规范
- [ ] 功能实现正确
- [ ] 异常处理完善
- [ ] 测试用例充分
- [ ] 文档更新及时

### 2. 沟通机制
- 每日站会：同步进度和问题
- 技术分享：定期分享技术心得
- 设计评审：重要功能设计讨论

### 3. 知识管理
- 技术文档维护
- 问题解决方案记录
- 最佳实践总结