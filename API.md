# API 接口文档

## 基础信息

- **基础URL**: `http://localhost:8888`
- **API版本**: v1
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

## 用户管理 API

### 用户登录
- **接口路径**: `POST /api/user/login`
- **请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```
- **响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "token": "jwt_token_here"
  }
}
```

### 用户注册
- **接口路径**: `POST /api/user/register`
- **请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱地址"
}
```

### 获取用户信息
- **接口路径**: `GET /api/user/profile`
- **请求头**: `Authorization: Bearer {token}`

## 商品管理 API

### 获取商品列表
- **接口路径**: `GET /api/goods`
- **查询参数**:
  - `page`: 页码（默认1）
  - `size`: 每页数量（默认10）
  - `category`: 商品分类
  - `keyword`: 搜索关键词

- **响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "title": "商品名称",
        "price": 99.99,
        "cover": "图片URL",
        "description": "商品描述",
        "categoryId": 1,
        "status": 1
      }
    ]
  }
}
```

### 获取商品详情
- **接口路径**: `GET /api/goods/{id}`
- **路径参数**: `id` - 商品ID

### 创建商品
- **接口路径**: `POST /api/goods`
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "title": "商品名称",
  "price": 99.99,
  "cover": "图片URL",
  "description": "商品描述",
  "categoryId": 1
}
```

### 更新商品
- **接口路径**: `PUT /api/goods/{id}`
- **请求头**: `Authorization: Bearer {token}`

### 删除商品
- **接口路径**: `DELETE /api/goods/{id}`
- **请求头**: `Authorization: Bearer {token}`

## 订单管理 API

### 创建订单
- **接口路径**: `POST /api/order`
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "goodsId": 1,
  "count": 2,
  "receiverName": "收货人姓名",
  "receiverPhone": "收货人电话",
  "receiverAddress": "收货地址",
  "remark": "备注信息"
}
```

### 获取订单列表
- **接口路径**: `GET /api/order`
- **请求头**: `Authorization: Bearer {token}`
- **查询参数**:
  - `page`: 页码
  - `size`: 每页数量
  - `status`: 订单状态

### 获取订单详情
- **接口路径**: `GET /api/order/{id}`
- **请求头**: `Authorization: Bearer {token}`

### 更新订单状态
- **接口路径**: `PUT /api/order/{id}/status`
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "status": 2
}
```

## 评论管理 API

### 获取商品评论
- **接口路径**: `GET /api/comment`
- **查询参数**:
  - `thingId`: 商品ID
  - `page`: 页码
  - `size`: 每页数量

### 创建评论
- **接口路径**: `POST /api/comment`
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "thingId": 1,
  "content": "评论内容",
  "rating": 5
}
```

### 删除评论
- **接口路径**: `DELETE /api/comment/{id}`
- **请求头**: `Authorization: Bearer {token}`

## AI 客服 API

### 发送消息
- **接口路径**: `POST /api/ai-chat/message`
- **请求参数**:
```json
{
  "message": "用户消息内容"
}
```

- **响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "response": "AI回复内容",
    "goods": [
      {
        "id": 1,
        "title": "推荐商品",
        "price": 99.99,
        "cover": "图片URL"
      }
    ]
  }
}
```

### 获取聊天历史
- **接口路径**: `GET /api/ai-chat/history`
- **请求头**: `Authorization: Bearer {token}`

## 地址管理 API

### 获取收货地址列表
- **接口路径**: `GET /api/address`
- **请求头**: `Authorization: Bearer {token}`

### 创建收货地址
- **接口路径**: `POST /api/address`
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "name": "收货人姓名",
  "mobile": "手机号码",
  "desc": "详细地址",
  "default": true
}
```

### 更新收货地址
- **接口路径**: `PUT /api/address/{id}`
- **请求头**: `Authorization: Bearer {token}`

### 删除收货地址
- **接口路径**: `DELETE /api/address/{id}`
- **请求头**: `Authorization: Bearer {token}`

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户名或密码错误 |
| 1002 | 用户不存在 |
| 1003 | 用户已存在 |
| 2001 | 商品不存在 |
| 2002 | 商品库存不足 |
| 3001 | 订单不存在 |
| 3002 | 订单状态错误 |
| 4001 | 评论不存在 |
| 5001 | AI服务不可用 |

## 请求示例

### 使用 curl

```bash
# 用户登录
curl -X POST http://localhost:8888/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 获取商品列表
curl -X GET "http://localhost:8888/api/goods?page=1&size=10"

# 创建订单
curl -X POST http://localhost:8888/api/order \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_token_here" \
  -d '{"goodsId":1,"count":2,"receiverName":"张三","receiverPhone":"13800138000","receiverAddress":"北京市朝阳区"}'
```

### 使用 JavaScript (Axios)

```javascript
// 用户登录
const loginResponse = await axios.post('/api/user/login', {
  username: 'testuser',
  password: '123456'
});

// 设置默认请求头
axios.defaults.headers.common['Authorization'] = `Bearer ${loginResponse.data.data.token}`;

// 获取商品列表
const goodsResponse = await axios.get('/api/goods', {
  params: { page: 1, size: 10 }
});

// 创建订单
const orderResponse = await axios.post('/api/order', {
  goodsId: 1,
  count: 2,
  receiverName: '张三',
  receiverPhone: '13800138000',
  receiverAddress: '北京市朝阳区'
});
```

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带 JWT Token
2. 分页查询的页码从 1 开始
3. 所有时间字段使用 ISO 8601 格式
4. 文件上传接口支持 multipart/form-data 格式
5. API 限流：每个IP每分钟最多 60 次请求