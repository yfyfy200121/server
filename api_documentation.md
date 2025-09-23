# 智能书城管理系统 - API接口文档

## 接口概览

本文档详细描述了智能书城管理系统的所有API接口，包括用户管理、商品管理、订单管理、AI客服等核心功能。

### 基础信息
- **基础URL**: `http://localhost:9100/api`
- **认证方式**: JWT Token (在请求头中添加 `Authorization: Bearer {token}`)
- **响应格式**: JSON
- **编码格式**: UTF-8

### 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": "响应数据"
}
```

## 1. AI智能客服接口

### 1.1 发送消息给AI客服
**接口地址**: `GET /ai-chat/message`

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| message | String | 是 | 用户发送的消息内容 |
| sessionId | String | 否 | 会话ID，用于多轮对话 |

**请求示例**:
```http
GET /api/ai-chat/message?message=推荐一些编程书籍&sessionId=user123
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "根据您的需求，我为您推荐以下几本优秀的编程书籍：\n\n1. 《数据结构与算法分析》- 深入浅出地讲解了经典算法\n2. 《C++多线程编程实战》- 适合进阶学习\n3. 《算法精粹》- 计算机科学经典问题解析\n\n这些书籍都有很高的用户评价，您可以点击查看详情：/detail?id=1"
}
```

### 1.2 刷新商品缓存
**接口地址**: `GET /ai-chat/refresh-cache`

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "商品信息缓存已刷新",
  "data": "成功加载 25 件商品信息"
}
```

### 1.3 测试AI服务连接
**接口地址**: `GET /ai-chat/test-connection`

**请求参数**: 无

**响应示例**:
```json
{
  "apiKeyConfigured": true,
  "apiKeyLength": 36,
  "apiUrl": "https://ark.cn-beijing.volces.com/api/v3/chat/completions",
  "thingCount": 25,
  "maskedApiKey": "4df2***caef"
}
```

## 2. 用户管理接口

### 2.1 用户注册
**接口地址**: `POST /user/register`

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱地址",
  "mobile": "手机号码"
}
```

### 2.2 用户登录
**接口地址**: `POST /user/login`

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": "1",
      "username": "testuser",
      "nickname": "测试用户",
      "role": "1"
    }
  }
}
```

### 2.3 获取用户信息
**接口地址**: `GET /user/info`

**请求头**: 
```
Authorization: Bearer {token}
```

### 2.4 更新用户信息
**接口地址**: `POST /user/update`

**请求参数**:
```json
{
  "nickname": "新昵称",
  "email": "新邮箱",
  "mobile": "新手机号"
}
```

## 3. 商品管理接口

### 3.1 获取商品列表
**接口地址**: `GET /thing/list`

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| keyword | String | 否 | 搜索关键词 |
| c | String | 否 | 分类ID |
| tag | String | 否 | 标签 |
| sort | String | 否 | 排序方式(price/score/time) |
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页数量，默认10 |

**请求示例**:
```http
GET /api/thing/list?keyword=Java&c=1&sort=score&page=1&size=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 25,
    "records": [
      {
        "id": 1,
        "title": "数据结构与算法分析",
        "cover": "/upload/cover1.jpg",
        "price": 89.00,
        "score": 4.8,
        "description": "深入浅出地讲解了经典算法...",
        "classification": {
          "id": 1,
          "title": "计算机"
        },
        "comments": [
          {
            "id": 1,
            "content": "很好的书籍",
            "score": 5,
            "username": "用户1"
          }
        ]
      }
    ]
  }
}
```

### 3.2 获取商品详情
**接口地址**: `GET /thing/detail`

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

### 3.3 创建商品（管理员）
**接口地址**: `POST /thing/create`

**请求参数**:
```json
{
  "title": "商品标题",
  "cover": "封面图片URL",
  "description": "商品描述",
  "price": 99.00,
  "repertory": 100,
  "classificationId": 1,
  "tag": "编程,算法"
}
```

### 3.4 更新商品（管理员）
**接口地址**: `POST /thing/update`

### 3.5 删除商品（管理员）
**接口地址**: `POST /thing/delete`

**请求参数**:
```json
{
  "id": 1
}
```

## 4. 订单管理接口

### 4.1 创建订单
**接口地址**: `POST /order/create`

**请求参数**:
```json
{
  "thingId": 1,
  "count": 2,
  "receiverName": "收货人姓名",
  "receiverPhone": "收货人电话",
  "receiverAddress": "收货地址"
}
```

### 4.2 获取用户订单列表
**接口地址**: `GET /order/userOrderList`

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| status | String | 否 | 订单状态筛选 |
| page | Integer | 否 | 页码 |
| size | Integer | 否 | 每页数量 |

### 4.3 支付订单
**接口地址**: `POST /order/payOrder`

**请求参数**:
```json
{
  "orderId": 1,
  "paymentMethod": "alipay"
}
```

### 4.4 取消订单
**接口地址**: `POST /order/cancelUserOrder`

**请求参数**:
```json
{
  "orderId": 1
}
```

### 4.5 获取所有订单（管理员）
**接口地址**: `GET /order/list`

## 5. 分类管理接口

### 5.1 获取分类列表
**接口地址**: `GET /classification/list`

### 5.2 创建分类（管理员）
**接口地址**: `POST /classification/create`

**请求参数**:
```json
{
  "title": "分类名称",
  "description": "分类描述"
}
```

## 6. 评论管理接口

### 6.1 获取商品评论
**接口地址**: `GET /comment/list`

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| thingId | Long | 是 | 商品ID |

### 6.2 创建评论
**接口地址**: `POST /comment/create`

**请求参数**:
```json
{
  "thingId": 1,
  "content": "评论内容",
  "score": 5
}
```

## 7. 收藏管理接口

### 7.1 添加收藏
**接口地址**: `POST /thingCollect/create`

### 7.2 取消收藏
**接口地址**: `POST /thingCollect/delete`

### 7.3 获取用户收藏列表
**接口地址**: `GET /thingCollect/getUserCollectList`

## 8. 心愿单管理接口

### 8.1 添加到心愿单
**接口地址**: `POST /thingWish/create`

### 8.2 获取用户心愿单
**接口地址**: `GET /thingWish/getUserWishList`

## 9. 地址管理接口

### 9.1 获取用户地址列表
**接口地址**: `GET /address/list`

### 9.2 创建地址
**接口地址**: `POST /address/create`

**请求参数**:
```json
{
  "name": "收货人姓名",
  "mobile": "手机号",
  "description": "详细地址",
  "def": "1"
}
```

### 9.3 更新地址
**接口地址**: `POST /address/update`

### 9.4 删除地址
**接口地址**: `POST /address/delete`

## 10. 系统管理接口

### 10.1 获取轮播图
**接口地址**: `GET /banner/list`

### 10.2 获取公告列表
**接口地址**: `GET /notice/list`

### 10.3 获取广告列表
**接口地址**: `GET /ad/list`

### 10.4 系统概览（管理员）
**接口地址**: `GET /overview/info`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userCount": 1256,
    "thingCount": 89,
    "orderCount": 567,
    "todayOrderCount": 23,
    "todayUserCount": 15,
    "totalAmount": 125689.50
  }
}
```

## 11. 文件上传接口

### 11.1 上传文件
**接口地址**: `POST /upload`

**请求参数**: 
- Content-Type: multipart/form-data
- file: 上传的文件

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "/upload/20241223/image.jpg"
}
```

## 错误码说明

| 错误码 | 描述 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 使用示例

### JavaScript/Axios 示例
```javascript
// 配置axios基础URL和拦截器
const api = axios.create({
  baseURL: 'http://localhost:9100/api',
  timeout: 10000
});

// 请求拦截器 - 添加token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 获取商品列表
const getThingList = async (params) => {
  try {
    const response = await api.get('/thing/list', { params });
    return response.data;
  } catch (error) {
    console.error('获取商品列表失败:', error);
    throw error;
  }
};

// AI客服对话
const sendAiMessage = async (message) => {
  try {
    const response = await api.get('/ai-chat/message', {
      params: { message }
    });
    return response.data;
  } catch (error) {
    console.error('AI客服调用失败:', error);
    throw error;
  }
};
```

### Python/requests 示例
```python
import requests

# 基础配置
BASE_URL = 'http://localhost:9100/api'
headers = {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer your-token-here'
}

# 获取商品列表
def get_thing_list(keyword=None, category=None):
    url = f'{BASE_URL}/thing/list'
    params = {}
    if keyword:
        params['keyword'] = keyword
    if category:
        params['c'] = category
    
    response = requests.get(url, params=params, headers=headers)
    return response.json()

# AI客服对话
def send_ai_message(message):
    url = f'{BASE_URL}/ai-chat/message'
    params = {'message': message}
    
    response = requests.get(url, params=params, headers=headers)
    return response.json()

# 使用示例
if __name__ == '__main__':
    # 搜索Java相关书籍
    books = get_thing_list(keyword='Java')
    print(f"找到 {books['data']['total']} 本Java相关书籍")
    
    # 与AI客服对话
    ai_response = send_ai_message('推荐一些Java编程书籍')
    print(f"AI回复: {ai_response['data']}")
```

## 注意事项

1. **认证**: 除了登录、注册等公开接口外，其他接口都需要在请求头中携带有效的JWT Token
2. **权限**: 管理员接口需要管理员权限（role=3）
3. **限流**: API接口有频率限制，建议合理控制请求频率
4. **缓存**: 商品列表等数据有缓存，更新后可能需要等待缓存刷新
5. **文件上传**: 支持的文件类型包括图片(jpg,png,gif)和文档(pdf,doc,docx)
6. **跨域**: 前端调用时注意配置正确的CORS设置

## 更新日志

### v1.0.0 (2024-12-23)
- 初始版本发布
- 完整的用户管理、商品管理、订单管理功能
- AI智能客服集成
- 支持文件上传和图片展示

### v1.1.0 (计划中)
- 新增智能推荐接口
- 优化AI客服响应速度
- 增加数据统计分析接口
- 支持更多文件类型上传