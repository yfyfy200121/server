# API接口文档

## 1. 接口概述

### 1.1 基本信息
- **服务地址**：http://localhost:9100/api
- **协议**：HTTP/HTTPS
- **数据格式**：JSON
- **编码格式**：UTF-8

### 1.2 通用响应格式
```json
{
    "code": 200,
    "msg": "success",
    "data": {}
}
```

### 1.3 状态码说明
- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权访问
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

## 2. 用户管理接口

### 2.1 用户注册
**接口地址**：`POST /user/register`

**请求参数**：
```json
{
    "username": "testuser",
    "password": "123456",
    "nickname": "测试用户",
    "mobile": "13888888888",
    "email": "test@example.com"
}
```

**响应示例**：
```json
{
    "code": 200,
    "msg": "注册成功",
    "data": {
        "id": "1",
        "username": "testuser",
        "nickname": "测试用户",
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

### 2.2 用户登录
**接口地址**：`POST /user/login`

**请求参数**：
```json
{
    "username": "testuser",
    "password": "123456"
}
```

**响应示例**：
```json
{
    "code": 200,
    "msg": "登录成功",
    "data": {
        "id": "1",
        "username": "testuser",
        "nickname": "测试用户",
        "role": "1",
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

## 3. 商品管理接口

### 3.1 商品列表查询
**接口地址**：`GET /thing/list`

**请求参数**：
- `keyword` (可选): 搜索关键词
- `sort` (可选): 排序方式 (recommend/new/hot)
- `c` (可选): 分类ID
- `tag` (可选): 标签ID

**响应示例**：
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": [
        {
            "id": 1,
            "title": "数据结构与算法分析",
            "cover": "cover1.jpg",
            "price": "89.00",
            "repertory": "100",
            "score": "4.8",
            "classification": {
                "id": 2,
                "title": "计算机"
            }
        }
    ]
}
```

### 3.2 商品详情查询
**接口地址**：`GET /thing/detail`

**请求参数**：
- `id`: 商品ID

**响应示例**：
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": {
        "id": 1,
        "title": "数据结构与算法分析",
        "cover": "cover1.jpg",
        "description": "详细描述...",
        "price": "89.00",
        "repertory": "100",
        "score": "4.8",
        "pv": "1250",
        "classification": {
            "id": 2,
            "title": "计算机"
        },
        "comments": [
            {
                "id": 1,
                "content": "很好的书",
                "commentTime": "2024-01-01 10:00:00",
                "user": {
                    "nickname": "读者1"
                }
            }
        ]
    }
}
```

## 4. 订单管理接口

### 4.1 创建订单
**接口地址**：`POST /order/create`

**请求参数**：
```json
{
    "thingId": "1",
    "count": "2",
    "receiverName": "张三",
    "receiverAddress": "北京市朝阳区",
    "receiverPhone": "13888888888",
    "remark": "请尽快发货"
}
```

**响应示例**：
```json
{
    "code": 200,
    "msg": "订单创建成功",
    "data": {
        "id": "1",
        "orderNumber": "ORDER20240101001",
        "status": "1",
        "orderTime": "2024-01-01 10:00:00"
    }
}
```

### 4.2 订单支付
**接口地址**：`POST /order/payOrder`

**请求参数**：
```json
{
    "orderId": "1"
}
```

**响应示例**：
```json
{
    "code": 200,
    "msg": "支付成功",
    "data": {
        "orderId": "1",
        "payTime": "2024-01-01 10:05:00",
        "status": "2"
    }
}
```

### 4.3 用户订单列表
**接口地址**：`GET /order/userOrderList`

**请求参数**：
- `userId`: 用户ID
- `status` (可选): 订单状态

**响应示例**：
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": [
        {
            "id": "1",
            "orderNumber": "ORDER20240101001",
            "count": "2",
            "status": "2",
            "orderTime": "2024-01-01 10:00:00",
            "thing": {
                "title": "数据结构与算法分析",
                "cover": "cover1.jpg",
                "price": "89.00"
            }
        }
    ]
}
```

## 5. AI客服接口

### 5.1 AI对话
**接口地址**：`GET /ai-chat/message`

**请求参数**：
- `message`: 用户消息内容
- `sessionId` (可选): 会话ID

**响应示例**：
```json
{
    "code": 200,
    "msg": "success",
    "data": "您好！我是智能客服助手。根据您的问题，我推荐以下几本关于数据结构的好书：《数据结构与算法分析》、《算法精粹》等。这些书籍都有详细的内容介绍和良好的用户评价。"
}
```

### 5.2 刷新商品缓存
**接口地址**：`GET /ai-chat/refresh-cache`

**响应示例**：
```json
{
    "code": 200,
    "msg": "商品信息缓存已刷新",
    "data": "成功加载 6 件商品信息"
}
```

## 6. 评论管理接口

### 6.1 添加评论
**接口地址**：`POST /comment/create`

**请求参数**：
```json
{
    "content": "这本书很不错，内容详细",
    "thingId": "1",
    "userId": "1"
}
```

**响应示例**：
```json
{
    "code": 200,
    "msg": "评论成功",
    "data": {
        "id": "1",
        "content": "这本书很不错，内容详细",
        "commentTime": "2024-01-01 10:00:00"
    }
}
```

### 6.2 获取商品评论
**接口地址**：`GET /comment/list`

**请求参数**：
- `thingId`: 商品ID

**响应示例**：
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": [
        {
            "id": "1",
            "content": "这本书很不错，内容详细",
            "commentTime": "2024-01-01 10:00:00",
            "likeCount": "5",
            "user": {
                "nickname": "读者1",
                "avatar": "avatar1.jpg"
            }
        }
    ]
}
```

## 7. 收藏管理接口

### 7.1 添加收藏
**接口地址**：`POST /thingCollect/create`

**请求参数**：
```json
{
    "thingId": "1",
    "userId": "1"
}
```

### 7.2 取消收藏
**接口地址**：`POST /thingCollect/delete`

**请求参数**：
- `id`: 收藏记录ID

### 7.3 用户收藏列表
**接口地址**：`GET /thingCollect/getUserCollectList`

**请求参数**：
- `userId`: 用户ID

## 8. 管理员接口

### 8.1 用户管理
**接口地址**：`GET /user/list` (需要管理员权限)

**请求参数**：
- `keyword` (可选): 搜索关键词

### 8.2 商品管理
**接口地址**：
- `POST /thing/create` - 创建商品
- `POST /thing/update` - 更新商品
- `POST /thing/delete` - 删除商品

### 8.3 订单管理
**接口地址**：
- `GET /order/list` - 订单列表
- `POST /order/update` - 更新订单状态

## 9. 系统监控接口

### 9.1 操作日志
**接口地址**：`GET /opLog/list`

### 9.2 错误日志
**接口地址**：`GET /errorLog/list`

### 9.3 访问统计
**接口地址**：`GET /overview/count`

**响应示例**：
```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "userCount": 150,
        "thingCount": 200,
        "orderCount": 350,
        "todayOrderCount": 25
    }
}
```

## 10. 文件上传接口

### 10.1 图片上传
**接口地址**：`POST /upload/image`

**请求参数**：
- `file`: 图片文件 (multipart/form-data)

**响应示例**：
```json
{
    "code": 200,
    "msg": "上传成功",
    "data": {
        "url": "/upload/images/20240101/abc123.jpg",
        "filename": "abc123.jpg"
    }
}
```

## 11. 权限控制

### 11.1 权限级别
- `@Access(AccessLevel.LOGIN)`: 需要登录
- `@Access(AccessLevel.ADMIN)`: 需要管理员权限
- `@Access(AccessLevel.DEMO)`: 演示用户权限

### 11.2 Token验证
所有需要权限的接口都需要在请求头中携带Token：
```
Authorization: Bearer {token}
```

## 12. 错误处理

### 12.1 参数验证错误
```json
{
    "code": 400,
    "msg": "参数错误：用户名不能为空",
    "data": null
}
```

### 12.2 权限验证错误
```json
{
    "code": 401,
    "msg": "请先登录",
    "data": null
}
```

### 12.3 业务逻辑错误
```json
{
    "code": 500,
    "msg": "库存不足",
    "data": null
}
```