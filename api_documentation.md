# 智能书城管理系统 API 接口文档

## 一、API 概述

### 1.1 基本信息
- **项目名称**: 智能书城管理系统
- **API版本**: v1.0
- **基础URL**: `http://localhost:9100/api`
- **数据格式**: JSON
- **字符编码**: UTF-8

### 1.2 通用响应格式

#### 成功响应
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    // 实际返回数据
  }
}
```

#### 错误响应
```json
{
  "code": 500,
  "msg": "错误信息描述",
  "data": null
}
```

### 1.3 状态码说明
| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 1.4 认证方式

系统使用Token认证方式，需要在请求头中添加：
```
TOKEN: your_token_here
```

## 二、用户管理接口

### 2.1 用户登录

**接口地址**: `POST /user/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "role": "2",
      "avatar": "/avatar/admin.png",
      "mobile": "13800138000",
      "email": "admin@example.com"
    }
  }
}
```

### 2.2 用户注册

**接口地址**: `POST /user/register`

**请求参数**:
```json
{
  "username": "newuser",
  "password": "123456",
  "nickname": "新用户",
  "mobile": "13800138001",
  "email": "newuser@example.com"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| nickname | string | 否 | 昵称 |
| mobile | string | 否 | 手机号 |
| email | string | 否 | 邮箱 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "注册成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "nickname": "新用户"
  }
}
```

### 2.3 获取用户信息

**接口地址**: `GET /user/info`

**请求头**: 需要Token认证

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "role": "2",
    "status": "1",
    "avatar": "/avatar/admin.png",
    "mobile": "13800138000",
    "email": "admin@example.com",
    "createTime": "2024-01-01 10:00:00"
  }
}
```

### 2.4 更新用户信息

**接口地址**: `POST /user/update`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "nickname": "新昵称",
  "mobile": "13800138002",
  "email": "newemail@example.com",
  "avatar": "/avatar/new.png"
}
```

### 2.5 用户列表（管理员）

**接口地址**: `GET /user/list`

**权限要求**: 管理员权限

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "管理员",
        "role": "2",
        "status": "1",
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

## 三、商品管理接口

### 3.1 商品列表

**接口地址**: `GET /thing/list`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| sort | string | 否 | 排序方式：recommend(推荐)、new(最新)、price_asc(价格升序)、price_desc(价格降序) |
| c | string | 否 | 分类ID |
| tag | string | 否 | 标签 |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 50,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "title": "Spring Boot实战",
        "cover": "/image/book1.jpg",
        "description": "深入学习Spring Boot框架的实战教程",
        "price": "89.00",
        "status": "1",
        "pv": "1256",
        "recommendCount": "89",
        "wishCount": "45",
        "collectCount": "67",
        "classificationId": 1,
        "repertory": "100",
        "createTime": "2024-01-01 10:00:00",
        "tags": ["Java", "Spring", "后端开发"]
      }
    ]
  }
}
```

### 3.2 商品详情

**接口地址**: `GET /thing/detail`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | string | 是 | 商品ID |

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "title": "Spring Boot实战",
    "cover": "/image/book1.jpg",
    "description": "深入学习Spring Boot框架的实战教程...",
    "price": "89.00",
    "status": "1",
    "pv": "1256",
    "recommendCount": "89",
    "wishCount": "45",
    "collectCount": "67",
    "classificationId": 1,
    "repertory": "100",
    "createTime": "2024-01-01 10:00:00",
    "classification": {
      "id": 1,
      "title": "编程技术"
    },
    "tags": ["Java", "Spring", "后端开发"]
  }
}
```

### 3.3 创建商品（管理员）

**接口地址**: `POST /thing/create`

**权限要求**: 管理员权限

**请求参数**:
```json
{
  "title": "Vue.js实战指南",
  "description": "从入门到精通的Vue.js开发教程",
  "price": "79.00",
  "classificationId": 2,
  "repertory": "50",
  "status": "1",
  "tags": ["JavaScript", "Vue", "前端开发"]
}
```

**文件上传**: 支持图片文件上传作为商品封面

### 3.4 更新商品（管理员）

**接口地址**: `POST /thing/update`

**权限要求**: 管理员权限

**请求参数**:
```json
{
  "id": 1,
  "title": "Spring Boot实战（第二版）",
  "description": "更新后的描述",
  "price": "99.00",
  "repertory": "80"
}
```

### 3.5 删除商品（管理员）

**接口地址**: `POST /thing/delete`

**权限要求**: 管理员权限

**请求参数**:
```json
{
  "ids": "1,2,3"
}
```

### 3.6 增加心愿数

**接口地址**: `POST /thing/wish`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "thingId": "1"
}
```

### 3.7 增加收藏数

**接口地址**: `POST /thing/collect`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "thingId": "1"
}
```

## 四、分类管理接口

### 4.1 分类列表

**接口地址**: `GET /classification/list`

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "title": "编程技术",
      "cover": "/image/category1.jpg",
      "description": "各种编程语言和技术类图书",
      "status": "1",
      "createTime": "2024-01-01 10:00:00"
    },
    {
      "id": 2,
      "title": "前端开发",
      "cover": "/image/category2.jpg",
      "description": "前端技术相关图书",
      "status": "1",
      "createTime": "2024-01-01 10:00:00"
    }
  ]
}
```

### 4.2 创建分类（管理员）

**接口地址**: `POST /classification/create`

**权限要求**: 管理员权限

**请求参数**:
```json
{
  "title": "数据库技术",
  "description": "数据库相关技术图书",
  "status": "1"
}
```

### 4.3 更新分类（管理员）

**接口地址**: `POST /classification/update`

**权限要求**: 管理员权限

### 4.4 删除分类（管理员）

**接口地址**: `POST /classification/delete`

**权限要求**: 管理员权限

## 五、订单管理接口

### 5.1 创建订单

**接口地址**: `POST /order/create`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "thingId": 1,
  "count": "2",
  "addressId": 1
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| thingId | int | 是 | 商品ID |
| count | string | 是 | 购买数量 |
| addressId | int | 是 | 收货地址ID |

**响应示例**:
```json
{
  "code": 200,
  "msg": "订单创建成功",
  "data": {
    "id": 1,
    "orderNumber": "2024010112345",
    "thingId": 1,
    "count": "2",
    "status": "1",
    "orderTime": "2024-01-01 12:34:56",
    "thing": {
      "id": 1,
      "title": "Spring Boot实战",
      "price": "89.00",
      "cover": "/image/book1.jpg"
    },
    "address": {
      "id": 1,
      "name": "张三",
      "mobile": "13800138000",
      "description": "北京市朝阳区xxx街道xxx号"
    }
  }
}
```

### 5.2 订单列表

**接口地址**: `GET /order/list`

**请求头**: 需要Token认证

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 否 | 订单状态：1(待支付)、2(已支付)、3(已发货)、4(已完成)、5(已取消) |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 25,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "orderNumber": "2024010112345",
        "thingId": 1,
        "count": "2",
        "status": "2",
        "orderTime": "2024-01-01 12:34:56",
        "payTime": "2024-01-01 12:40:00",
        "thing": {
          "id": 1,
          "title": "Spring Boot实战",
          "price": "89.00",
          "cover": "/image/book1.jpg"
        },
        "totalPrice": "178.00"
      }
    ]
  }
}
```

### 5.3 订单详情

**接口地址**: `GET /order/detail`

**请求头**: 需要Token认证

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | string | 是 | 订单ID |

### 5.4 更新订单状态

**接口地址**: `POST /order/update`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "id": 1,
  "status": "2"
}
```

**状态说明**:
- 1: 待支付
- 2: 已支付
- 3: 已发货
- 4: 已完成
- 5: 已取消

### 5.5 取消订单

**接口地址**: `POST /order/cancel`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "id": 1
}
```

## 六、地址管理接口

### 6.1 地址列表

**接口地址**: `GET /address/list`

**请求头**: 需要Token认证

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "mobile": "13800138000",
      "description": "北京市朝阳区xxx街道xxx号",
      "def": "1",
      "createTime": "2024-01-01 10:00:00"
    }
  ]
}
```

### 6.2 创建地址

**接口地址**: `POST /address/create`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "name": "李四",
  "mobile": "13800138001",
  "description": "上海市浦东新区xxx路xxx号",
  "def": "0"
}
```

### 6.3 更新地址

**接口地址**: `POST /address/update`

**请求头**: 需要Token认证

### 6.4 删除地址

**接口地址**: `POST /address/delete`

**请求头**: 需要Token认证

## 七、AI客服接口

### 7.1 AI聊天

**接口地址**: `GET /ai-chat/message`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| message | string | 是 | 用户输入的消息 |
| sessionId | string | 否 | 会话ID，用于维持上下文 |

**请求示例**:
```
GET /ai-chat/message?message=推荐一些Java编程的书籍&sessionId=session123
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "response": "为您推荐几本优秀的Java编程书籍：\n\n1. 《Spring Boot实战》- 深入学习Spring Boot框架\n2. 《Java核心技术》- Java基础知识全面覆盖\n3. 《设计模式之美》- 提升代码设计能力",
    "goods": [
      {
        "id": 1,
        "title": "Spring Boot实战",
        "price": "89.00",
        "cover": "/image/book1.jpg",
        "description": "深入学习Spring Boot框架的实战教程"
      },
      {
        "id": 5,
        "title": "Java核心技术",
        "price": "129.00",
        "cover": "/image/book5.jpg",
        "description": "Java基础知识全面覆盖"
      }
    ]
  }
}
```

### 7.2 测试AI连接

**接口地址**: `GET /ai-chat/test-connection`

**响应示例**:
```json
{
  "code": 200,
  "msg": "AI服务连接正常",
  "data": {
    "status": "connected",
    "model": "deepseek-chat",
    "timestamp": "2024-01-01 12:00:00"
  }
}
```

## 八、标签管理接口

### 8.1 标签列表

**接口地址**: `GET /tag/list`

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "title": "Java",
      "createTime": "2024-01-01 10:00:00"
    },
    {
      "id": 2,
      "title": "Spring",
      "createTime": "2024-01-01 10:00:00"
    }
  ]
}
```

### 8.2 创建标签（管理员）

**接口地址**: `POST /tag/create`

**权限要求**: 管理员权限

### 8.3 删除标签（管理员）

**接口地址**: `POST /tag/delete`

**权限要求**: 管理员权限

## 九、评论管理接口

### 9.1 评论列表

**接口地址**: `GET /comment/list`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| thingId | string | 否 | 商品ID |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 15,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "content": "这本书写得很好，推荐！",
        "score": "5",
        "createTime": "2024-01-01 10:00:00",
        "thingId": 1,
        "user": {
          "id": 3,
          "nickname": "读书爱好者",
          "avatar": "/avatar/user3.png"
        }
      }
    ]
  }
}
```

### 9.2 创建评论

**接口地址**: `POST /comment/create`

**请求头**: 需要Token认证

**请求参数**:
```json
{
  "thingId": 1,
  "content": "这本书内容很丰富，值得推荐！",
  "score": "5"
}
```

### 9.3 删除评论

**接口地址**: `POST /comment/delete`

**权限要求**: 评论作者或管理员

## 十、公告管理接口

### 10.1 公告列表

**接口地址**: `GET /notice/list`

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "title": "系统维护通知",
      "content": "系统将于今晚进行维护，请提前保存数据",
      "createTime": "2024-01-01 10:00:00"
    }
  ]
}
```

### 10.2 创建公告（管理员）

**接口地址**: `POST /notice/create`

**权限要求**: 管理员权限

## 十一、广告管理接口

### 11.1 广告列表

**接口地址**: `GET /ad/list`

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "image": "/image/ad1.jpg",
      "link": "/goods/detail/1",
      "createTime": "2024-01-01 10:00:00"
    }
  ]
}
```

### 11.2 创建广告（管理员）

**接口地址**: `POST /ad/create`

**权限要求**: 管理员权限

## 十二、数据统计接口

### 12.1 概览数据（管理员）

**接口地址**: `GET /overview/count`

**权限要求**: 管理员权限

**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "userCount": 156,
    "thingCount": 89,
    "orderCount": 245,
    "todayOrderCount": 12,
    "todayUserCount": 8,
    "revenue": "12580.00"
  }
}
```

### 12.2 用户访问统计

**接口地址**: `GET /overview/user`

**权限要求**: 管理员权限

### 12.3 商品销售统计

**接口地址**: `GET /overview/thing`

**权限要求**: 管理员权限

## 十三、错误处理

### 13.1 参数验证错误

**场景**: 请求参数不符合要求

**响应示例**:
```json
{
  "code": 400,
  "msg": "参数验证失败：用户名不能为空",
  "data": null
}
```

### 13.2 权限不足错误

**场景**: 无权限访问接口

**响应示例**:
```json
{
  "code": 403,
  "msg": "无操作权限",
  "data": null
}
```

### 13.3 资源不存在错误

**场景**: 请求的资源不存在

**响应示例**:
```json
{
  "code": 404,
  "msg": "商品不存在",
  "data": null
}
```

### 13.4 服务器错误

**场景**: 服务器内部错误

**响应示例**:
```json
{
  "code": 500,
  "msg": "服务器内部错误",
  "data": null
}
```

## 十四、接口测试示例

### 14.1 使用curl测试

#### 登录接口
```bash
curl -X POST "http://localhost:9100/api/user/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

#### 获取商品列表
```bash
curl -X GET "http://localhost:9100/api/thing/list?keyword=Spring&page=1&size=10"
```

#### 创建订单（需要Token）
```bash
curl -X POST "http://localhost:9100/api/order/create" \
  -H "Content-Type: application/json" \
  -H "TOKEN: your_token_here" \
  -d '{
    "thingId": 1,
    "count": "2",
    "addressId": 1
  }'
```

### 14.2 使用Postman测试

1. **设置环境变量**
   - baseUrl: http://localhost:9100/api
   - token: 登录后获取的token

2. **测试步骤**
   - 首先调用登录接口获取token
   - 将token设置到环境变量或请求头
   - 依次测试各个接口

### 14.3 前端调用示例

#### JavaScript/Axios
```javascript
// 登录
const login = async (username, password) => {
  try {
    const response = await axios.post('/api/user/login', {
      username,
      password
    });
    return response.data;
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
};

// 获取商品列表
const getGoodsList = async (params = {}) => {
  try {
    const response = await axios.get('/api/thing/list', { params });
    return response.data;
  } catch (error) {
    console.error('获取商品列表失败:', error);
    throw error;
  }
};

// 创建订单
const createOrder = async (orderData) => {
  try {
    const response = await axios.post('/api/order/create', orderData, {
      headers: {
        'TOKEN': localStorage.getItem('token')
      }
    });
    return response.data;
  } catch (error) {
    console.error('创建订单失败:', error);
    throw error;
  }
};
```

## 十五、更新日志

### v1.0.0 (2024-01-01)
- 完成用户管理、商品管理、订单管理基础功能
- 集成AI客服功能
- 添加权限控制和Token认证
- 支持文件上传功能

### 后续版本计划
- v1.1.0: 添加支付接口集成
- v1.2.0: 增加推荐算法接口
- v1.3.0: 添加实时通知功能
- v2.0.0: 重构为微服务架构

---

本文档详细描述了智能书城管理系统的所有API接口，包括请求参数、响应格式、错误处理等，为前端开发和第三方集成提供了完整的技术参考。