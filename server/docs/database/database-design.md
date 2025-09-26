# 数据库设计文档

## 1. 数据库概述

### 1.1 设计原则
- **规范化原则**：遵循第三范式，减少数据冗余
- **性能优化原则**：合理设计索引，优化查询性能
- **扩展性原则**：预留扩展字段，支持业务发展
- **数据完整性原则**：完善的约束和关联关系

### 1.2 技术选型
- **数据库**：MySQL 8.0.33
- **字符集**：UTF-8
- **存储引擎**：InnoDB
- **事务支持**：ACID特性完整支持

## 2. 数据库架构

### 2.1 物理架构
```
主数据库 (MySQL 8.0)
├── 用户数据
├── 商品数据  
├── 订单数据
├── 系统数据
└── 日志数据

缓存层 (Redis) - 可扩展
├── 商品信息缓存
├── 用户会话缓存
└── AI服务缓存
```

### 2.2 逻辑架构
```
base_zxsc 数据库
├── 用户模块表
│   ├── b_user (用户表)
│   └── b_address (地址表)
├── 商品模块表
│   ├── b_thing (商品表)
│   ├── b_classification (分类表)
│   ├── b_tag (标签表)
│   └── b_thing_tag (商品标签关联表)
├── 交易模块表
│   ├── b_order (订单表)
│   ├── b_thing_collect (商品收藏表)
│   └── b_thing_wish (商品心愿单表)
├── 内容模块表
│   ├── b_comment (评论表)
│   ├── b_banner (轮播图表)
│   └── b_ad (广告表)
├── 系统模块表
│   ├── b_notice (通知表)
│   ├── b_op_log (操作日志表)
│   ├── b_error_log (错误日志表)
│   └── b_visit_data (访问统计表)
```

## 3. 核心表结构设计

### 3.1 用户管理模块

#### b_user (用户表)
```sql
CREATE TABLE `b_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(255) COMMENT '用户名',
    `password` VARCHAR(255) COMMENT '密码',
    `nickname` VARCHAR(255) COMMENT '昵称',
    `mobile` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `description` TEXT COMMENT '个人描述',
    `role` VARCHAR(20) COMMENT '角色(1:普通用户,2:演示用户,3:管理员)',
    `status` VARCHAR(10) COMMENT '状态(0:禁用,1:启用)',
    `score` VARCHAR(20) COMMENT '积分',
    `avatar` VARCHAR(255) COMMENT '头像',
    `token` VARCHAR(255) COMMENT '登录令牌',
    `create_time` VARCHAR(30) COMMENT '创建时间',
    `push_email` VARCHAR(100) COMMENT '推送邮箱',
    `push_switch` VARCHAR(10) COMMENT '推送开关',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    KEY `idx_mobile` (`mobile`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
```

#### b_address (地址表)
```sql
CREATE TABLE `b_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `name` VARCHAR(100) COMMENT '收货人姓名',
    `mobile` VARCHAR(20) COMMENT '收货人电话',
    `description` VARCHAR(500) COMMENT '详细地址',
    `def` VARCHAR(10) COMMENT '是否默认地址(0:否,1:是)',
    `create_time` VARCHAR(30) COMMENT '创建时间',
    `user_id` VARCHAR(20) COMMENT '用户ID',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收货地址表';
```

### 3.2 商品管理模块

#### b_thing (商品表)
```sql
CREATE TABLE `b_thing` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `title` VARCHAR(255) COMMENT '商品标题',
    `cover` VARCHAR(255) COMMENT '商品封面图',
    `description` LONGTEXT COMMENT '商品描述',
    `price` VARCHAR(20) COMMENT '价格',
    `status` VARCHAR(10) COMMENT '状态(0:下架,1:上架)',
    `create_time` VARCHAR(30) COMMENT '创建时间',
    `repertory` VARCHAR(20) COMMENT '库存数量',
    `score` VARCHAR(10) COMMENT '评分',
    `pv` VARCHAR(20) COMMENT '浏览量',
    `recommend_count` VARCHAR(20) COMMENT '推荐数',
    `wish_count` VARCHAR(20) COMMENT '心愿数',
    `collect_count` VARCHAR(20) COMMENT '收藏数',
    `classification_id` BIGINT COMMENT '分类ID',
    PRIMARY KEY (`id`),
    KEY `idx_classification_id` (`classification_id`),
    KEY `idx_status` (`status`),
    KEY `idx_price` (`price`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品表';
```

#### b_classification (分类表)
```sql
CREATE TABLE `b_classification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `title` VARCHAR(255) COMMENT '分类名称',
    `create_time` VARCHAR(30) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品分类表';
```

#### b_tag (标签表)
```sql
CREATE TABLE `b_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `title` VARCHAR(255) COMMENT '标签名称',
    `create_time` VARCHAR(30) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='标签表';
```

#### b_thing_tag (商品标签关联表)
```sql
CREATE TABLE `b_thing_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `thing_id` BIGINT COMMENT '商品ID',
    `tag_id` BIGINT COMMENT '标签ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `thing_tag` (`thing_id`, `tag_id`),
    KEY `idx_thing_id` (`thing_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品标签关联表';
```

### 3.3 订单管理模块

#### b_order (订单表)
```sql
CREATE TABLE `b_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_number` VARCHAR(50) COMMENT '订单号',
    `count` VARCHAR(20) COMMENT '商品数量',
    `status` VARCHAR(20) COMMENT '订单状态',
    `order_time` VARCHAR(30) COMMENT '下单时间',
    `receiver_name` VARCHAR(20) COMMENT '收货人姓名',
    `receiver_address` VARCHAR(255) COMMENT '收货地址',
    `receiver_phone` VARCHAR(20) COMMENT '收货人电话',
    `remark` VARCHAR(255) COMMENT '订单备注',
    `user_id` VARCHAR(20) COMMENT '用户ID',
    `thing_id` VARCHAR(20) COMMENT '商品ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `order_number` (`order_number`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_thing_id` (`thing_id`),
    KEY `idx_status` (`status`),
    KEY `idx_order_time` (`order_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';
```

### 3.4 评论模块

#### b_comment (评论表)
```sql
CREATE TABLE `b_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `content` VARCHAR(1000) COMMENT '评论内容',
    `comment_time` VARCHAR(30) COMMENT '评论时间',
    `like_count` VARCHAR(20) COMMENT '点赞数',
    `user_id` VARCHAR(20) COMMENT '用户ID',
    `thing_id` VARCHAR(20) COMMENT '商品ID',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_thing_id` (`thing_id`),
    KEY `idx_comment_time` (`comment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评论表';
```

### 3.5 系统管理模块

#### b_op_log (操作日志表)
```sql
CREATE TABLE `b_op_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `re_ip` VARCHAR(100) COMMENT '请求IP',
    `re_time` VARCHAR(30) COMMENT '请求时间',
    `re_url` VARCHAR(255) COMMENT '请求URL',
    `re_method` VARCHAR(10) COMMENT '请求方法',
    `re_content` VARCHAR(1000) COMMENT '请求内容',
    `access_time` VARCHAR(10) COMMENT '访问时长',
    `re_ua` VARCHAR(500) COMMENT '用户代理',
    PRIMARY KEY (`id`),
    KEY `idx_re_ip` (`re_ip`),
    KEY `idx_re_time` (`re_time`),
    KEY `idx_re_url` (`re_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';
```

## 4. 数据关系设计

### 4.1 主要关联关系

```
用户表 (b_user)
├── 1:N → 地址表 (b_address)
├── 1:N → 订单表 (b_order)
├── 1:N → 评论表 (b_comment)
├── 1:N → 收藏表 (b_thing_collect)
└── 1:N → 心愿单表 (b_thing_wish)

商品表 (b_thing)
├── N:1 → 分类表 (b_classification)
├── N:N → 标签表 (b_tag) [通过 b_thing_tag]
├── 1:N → 订单表 (b_order)
├── 1:N → 评论表 (b_comment)
├── 1:N → 收藏表 (b_thing_collect)
└── 1:N → 心愿单表 (b_thing_wish)
```

### 4.2 关联关系约束

- **外键约束**：保证数据引用完整性
- **级联操作**：删除商品时级联删除相关标签关联
- **索引优化**：关联字段建立适当索引

## 5. 性能优化设计

### 5.1 索引策略

#### 主键索引
- 所有表都有自增主键ID
- 使用BIGINT类型支持大数据量

#### 唯一索引
- 用户名、订单号等业务唯一字段
- 防止重复数据，提高查询效率

#### 普通索引
- 外键字段：user_id, thing_id等
- 查询频繁字段：status, create_time等
- 组合索引：thing_id + tag_id

### 5.2 查询优化

#### 分页查询优化
```sql
-- 优化前（深分页性能差）
SELECT * FROM b_thing ORDER BY id DESC LIMIT 10000, 20;

-- 优化后（使用子查询）
SELECT * FROM b_thing WHERE id <= (
    SELECT id FROM b_thing ORDER BY id DESC LIMIT 10000, 1
) ORDER BY id DESC LIMIT 20;
```

#### 关联查询优化
```sql
-- 商品评论关联查询
SELECT t.title, c.content, u.nickname 
FROM b_thing t
JOIN b_comment c ON t.id = c.thing_id
JOIN b_user u ON c.user_id = u.id
WHERE t.status = '1';
```

### 5.3 数据分区策略（可扩展）

#### 时间分区
- 日志表按月分区
- 订单表按年分区
- 提高大数据量查询性能

#### 哈希分区
- 用户表按用户ID哈希分区
- 分散热点数据访问

## 6. 数据安全设计

### 6.1 敏感数据处理

#### 密码安全
- 密码单向加密存储
- 使用强加密算法
- 定期密码更新策略

#### 个人信息保护
- 手机号部分掩码显示
- 邮箱地址脱敏处理
- 地址信息权限控制

### 6.2 数据备份策略

#### 定期备份
- 每日全量备份
- 实时增量备份
- 异地容灾备份

#### 恢复测试
- 定期恢复测试
- 制定应急预案
- 数据一致性校验

## 7. 扩展性设计

### 7.1 垂直扩展

#### 表结构预留
- 预留扩展字段
- 支持新功能添加
- 向下兼容保证

#### 数据类型优化
- 合理选择数据类型
- 避免过度设计
- 性能与存储平衡

### 7.2 水平扩展

#### 分库分表策略
- 按业务模块分库
- 按数据量分表
- 分布式事务处理

#### 读写分离
- 主从复制配置
- 读写请求分离
- 负载均衡策略