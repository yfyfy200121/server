# 基于SpringBoot的智能书城管理系统设计与实现

## 摘要

随着电商的不断发展和人工智能技术的快速崛起，传统图书商店的销售方式正面临着数字化转型的迫切需求。本文设计并实现了一个基于SpringBoot的智能书城管理系统，采用了现代化的前后端分离架构，融合了成熟的生成式人工智能技术，有效降低了客服成本并提升了用户体验。

该管理系统的后端采用SpringBoot框架构建RESTful API服务，利用MyBatis-Plus进行数据持久层设计，使用MySQL存储订单、商品、用户、管理员等重要业务数据，并集成Redis作为缓存层提升系统整体性能。前端使用Vue3框架开发，采用Ant Design Vue组件库构建用户界面，通过Axios与后端进行数据交互。

系统的主要创新点在于集成了火山引擎DeepSeek V3多模态大语言模型，实现了智能客服模块，能够7×24小时为用户提供在线图书咨询服务。此外，系统还具备完整的电商功能，包括用户登录注册、商品浏览搜索、购物车管理、订单跟踪、评论功能等。同时，本系统还创新性地集成了AI分析功能，包括情感分析、智能推荐、销售趋势分析、用户画像分析等，为商家提供了数据驱动的决策支持。

经过全面的功能测试和性能测试验证，系统运行稳定，AI客服响应准确，满足了现代电子商务的基本需求，为智能化电商平台的发展提供了实践经验和技术参考。

**关键词**：智能书城；人工智能；前后端分离；电子商务；SpringBoot

## ABSTRACT

With the continuous development of e-commerce and the rapid rise of artificial intelligence technology, traditional bookstore sales methods are facing urgent digital transformation needs. This paper designs and implements an intelligent bookstore management system based on SpringBoot, adopting a modern front-end and back-end separation architecture, integrating mature generative AI technology to effectively reduce customer service costs and improve user experience.

The backend of the management system uses the SpringBoot framework to build RESTful API services, utilizes MyBatis-Plus for data persistence layer design, uses MySQL to store important business data such as orders, products, users, and administrators, and integrates Redis as a cache layer to improve overall system performance. The frontend is developed using the Vue3 framework, adopts the Ant Design Vue component library to build the user interface, and interacts with the backend through Axios.

The main innovation of the system lies in the integration of the Volcengine DeepSeek V3 multimodal large language model, implementing an intelligent customer service module that can provide online book consultation services for users 24/7. In addition, the system also has complete e-commerce functions, including user login registration, product browsing and search, shopping cart management, order tracking, comment functions, etc. At the same time, this system also innovatively integrates AI analysis functions, including sentiment analysis, intelligent recommendation, sales trend analysis, user profile analysis, etc., providing data-driven decision support for merchants.

After comprehensive functional testing and performance testing verification, the system runs stably, AI customer service responds accurately, meets the basic needs of modern e-commerce, and provides practical experience and technical reference for the development of intelligent e-commerce platforms.

**Keywords**: Smart Bookstore; Artificial Intelligence; Front-end and Back-end Separation; E-commerce; SpringBoot

## 目录

1. 绪论
   - 1.1 课题的研究背景
   - 1.2 国内外发展和现状
   - 1.3 系统研究内容概述
   - 1.4 论文结构

2. 系统相关技术的介绍
   - 2.1 SpringBoot框架
   - 2.2 Vue3前端框架
   - 2.3 数据库技术
   - 2.4 人工智能技术

3. 系统需求分析与可行性研究
   - 3.1 系统功能需求分析
   - 3.2 系统非功能性需求分析
   - 3.3 可行性分析

4. 系统设计
   - 4.1 系统总体架构设计
   - 4.2 数据库设计
   - 4.3 系统接口设计

5. 系统实现
   - 5.1 前台功能模块实现
   - 5.2 后台管理模块实现
   - 5.3 AI智能客服模块实现

6. 系统测试
   - 6.1 测试策略与环境
   - 6.2 功能测试
   - 6.3 性能测试

7. 总结与展望
   - 7.1 系统总结
   - 7.2 未来展望

## 1 绪论

### 1.1 课题的研究背景

随着互联网技术的飞速发展和电子商务的普及，传统图书销售行业正面临着前所未有的挑战和机遇。一方面，线上购书的便利性和选择多样性吸引了越来越多的消费者；另一方面，传统书店需要通过数字化转型来适应市场变化，提升竞争力。

近年来，人工智能技术特别是生成式AI的快速发展，为电商平台的智能化升级提供了新的可能性。客服成本的不断上升促使企业寻求更加高效的客户服务解决方案，而AI客服系统能够提供24小时不间断服务，显著降低人力成本的同时提升服务质量。

在此背景下，开发一个集成了现代Web技术和人工智能技术的智能书城管理系统，不仅能够满足现代消费者的购书需求，还能为书店运营者提供智能化的管理工具，具有重要的现实意义和应用价值。

### 1.2 国内外发展和现状

#### 1.2.1 国外研究现状

国外电商平台在AI技术应用方面起步较早，Amazon、eBay等平台已经成功集成了推荐系统、智能客服等AI功能。Amazon的Alexa语音助手、个性化推荐系统等都是AI在电商领域应用的典型案例。这些系统通过机器学习算法分析用户行为数据，提供精准的商品推荐和个性化服务。

在技术架构方面，国外主流电商平台普遍采用微服务架构，通过云计算平台实现高可用、高扩展性的系统设计。React、Vue.js等现代前端框架与Node.js、Java Spring等后端技术的结合，形成了成熟的前后端分离开发模式。

#### 1.2.2 国内研究现状

国内电商巨头如阿里巴巴、京东、拼多多等在AI技术应用方面也有显著进展。阿里的天猫精灵、京东的JIMI智能客服等都是AI客服系统的成功实践。这些系统不仅能够处理常见的客户咨询，还能够进行情感分析、智能推荐等高级功能。

在图书电商领域，当当网、京东图书等平台已经形成了相对成熟的在线图书销售体系，但在AI智能化程度方面仍有提升空间。特别是在个性化推荐、智能客服交互体验等方面，还需要进一步优化和完善。

### 1.3 系统研究内容概述

本系统的研究内容主要包括以下几个方面：

1. **系统架构设计**：采用前后端分离的现代化架构，后端基于SpringBoot构建RESTful API，前端使用Vue3框架开发响应式用户界面。

2. **数据库设计**：设计合理的数据库表结构，支持用户管理、商品管理、订单管理等核心业务功能，并通过Redis缓存提升系统性能。

3. **AI客服系统**：集成火山引擎DeepSeek V3大语言模型，实现智能问答、商品推荐、情感分析等AI功能。

4. **电商核心功能**：实现完整的电商功能流程，包括用户注册登录、商品浏览搜索、购物车管理、订单处理等。

5. **后台管理系统**：开发管理员后台，支持用户管理、商品管理、订单管理、数据统计等管理功能。

6. **系统测试与优化**：进行全面的功能测试、性能测试，确保系统的稳定性和可用性。

### 1.4 论文结构

本论文共分为7个章节：

第1章为绪论，介绍了课题的研究背景、国内外发展现状、研究内容概述以及论文结构。

第2章为系统相关技术介绍，详细介绍了SpringBoot、Vue3、数据库技术以及人工智能技术等关键技术。

第3章为系统需求分析与可行性研究，分析了系统的功能需求、非功能性需求，并进行了可行性分析。

第4章为系统设计，包括总体架构设计、数据库设计、系统接口设计等。

第5章为系统实现，详细介绍了前台功能模块、后台管理模块以及AI智能客服模块的具体实现。

第6章为系统测试，介绍了测试策略、测试环境以及测试结果。

第7章为总结与展望，总结了系统的特点和创新点，并对未来的发展方向进行了展望。

## 2 系统相关技术的介绍

### 2.1 SpringBoot框架

#### 2.1.1 SpringBoot简介

SpringBoot是由Pivotal团队开发的用于简化Spring应用程序开发的框架。它基于Spring框架，通过自动配置、起步依赖等特性，大大简化了Spring应用的配置和部署过程。SpringBoot的设计理念是"约定优于配置"，开发者可以快速创建独立运行的Spring应用程序。

#### 2.1.2 SpringBoot核心特性

1. **自动配置**：SpringBoot能够根据项目中的依赖自动配置相关的Bean，减少了手动配置的工作量。

2. **起步依赖**：提供了一系列预定义的依赖组合，开发者只需添加相应的起步依赖，就能引入完整的功能模块。

3. **内嵌服务器**：支持内嵌Tomcat、Jetty、Undertow等服务器，应用可以打包成jar文件独立运行。

4. **健康检查**：提供了应用健康检查、指标监控等生产级特性。

5. **无代码生成**：不需要生成代码，也不需要XML配置文件。

#### 2.1.3 在本系统的应用

在本智能书城管理系统中，SpringBoot主要用于：

- 构建RESTful API服务
- 整合MyBatis-Plus进行数据持久化
- 集成Redis缓存中间件
- 实现跨域配置和安全认证
- 整合第三方AI服务接口

### 2.2 Vue3前端框架

#### 2.2.1 Vue3核心特性

Vue3是Vue.js的最新版本，相比Vue2有以下重要改进：

1. **Composition API**：提供了更灵活的组件逻辑组织方式，支持更好的类型推导和代码复用。

2. **性能优化**：通过Proxy重写响应式系统，提升了性能和内存使用效率。

3. **更好的TypeScript支持**：原生支持TypeScript，提供更好的开发体验。

4. **Tree-shaking支持**：更好的摇树优化，减少最终打包体积。

5. **Teleport组件**：允许组件在DOM树中的任意位置渲染内容。

#### 2.2.2 配套技术栈

本系统采用的Vue3生态技术栈包括：

- **Ant Design Vue**：企业级UI组件库，提供丰富的组件和设计语言
- **Vue Router**：官方路由管理器，支持单页应用的路由切换
- **Pinia**：新一代状态管理库，替代Vuex
- **Axios**：基于Promise的HTTP客户端，用于API请求
- **Vite**：新一代前端构建工具，提供极速的开发体验

### 2.3 数据库技术

#### 2.3.1 MySQL数据库

MySQL是目前最流行的开源关系型数据库管理系统之一，具有以下特点：

1. **高性能**：支持高并发读写操作，适合Web应用场景
2. **可靠性**：提供事务支持，确保数据一致性
3. **易用性**：语法简单，易于学习和使用
4. **开源免费**：降低了项目成本
5. **生态丰富**：有完善的工具和文档支持

在本系统中，MySQL用于存储用户信息、商品数据、订单记录等核心业务数据。

#### 2.3.2 Redis缓存

Redis是一个高性能的key-value数据库，常用作缓存中间件：

1. **高性能**：基于内存存储，读写速度极快
2. **数据结构丰富**：支持字符串、列表、集合、哈希等数据类型
3. **持久化支持**：提供RDB和AOF两种持久化方式
4. **高可用**：支持主从复制和集群模式

在本系统中，Redis主要用于：
- 缓存热点商品信息
- 存储用户会话信息
- 缓存AI客服的上下文信息

### 2.4 人工智能技术

#### 2.4.1 大语言模型技术

大语言模型（Large Language Model, LLM）是近年来人工智能领域的重要突破，具有强大的自然语言理解和生成能力。这些模型通过在大规模文本数据上进行预训练，能够理解复杂的语言模式，并生成流畅、连贯的文本回应。

主要特点包括：
1. **多任务能力**：能够处理问答、摘要、翻译等多种任务
2. **上下文理解**：能够理解对话上下文，进行连贯的多轮对话
3. **知识丰富**：包含大量的世界知识和常识
4. **推理能力**：具备一定的逻辑推理和问题解决能力

#### 2.4.2 火山引擎DeepSeek V3

火山引擎DeepSeek V3是字节跳动推出的多模态大语言模型，具有以下优势：

1. **强大的理解能力**：能够准确理解用户意图，提供相关回应
2. **多模态支持**：支持文本、图像等多种输入类型
3. **API接口完善**：提供简单易用的API接口，方便集成
4. **稳定可靠**：基于字节跳动的技术积累，服务稳定性高
5. **中文优化**：对中文语境有特别的优化，适合国内应用场景

#### 2.4.3 在本系统的应用

在智能书城管理系统中，AI技术主要应用于以下场景：

1. **智能客服**：
   - 回答图书相关问题
   - 提供购书建议和推荐
   - 处理订单查询和售后服务

2. **个性化推荐**：
   - 基于用户浏览历史推荐图书
   - 分析用户偏好提供个性化内容
   - 实现智能搜索和分类

3. **内容分析**：
   - 分析用户评论情感倾向
   - 提取关键词和主题标签
   - 生成商品描述和摘要

## 3 系统需求分析与可行性研究

### 3.1 系统功能需求分析

#### 3.1.1 用户功能需求

1. **用户注册与登录**
   - 支持用户注册、登录、注销功能
   - 提供密码重置和账户信息修改
   - 支持第三方登录（预留接口）

2. **商品浏览与搜索**
   - 商品分类浏览，支持多级分类
   - 关键词搜索，支持模糊匹配
   - 商品详情查看，包括图片、价格、评价等
   - 商品对比和收藏功能

3. **购物车与订单管理**
   - 添加商品到购物车，支持数量修改
   - 订单创建、支付、跟踪功能
   - 订单历史查询和状态管理
   - 收货地址管理

4. **AI智能客服**
   - 24小时在线客服咨询
   - 智能商品推荐
   - 订单状态查询
   - 常见问题解答

5. **用户中心**
   - 个人信息管理
   - 订单历史查看
   - 收藏夹管理
   - 评价和反馈

#### 3.1.2 管理员功能需求

1. **用户管理**
   - 用户信息查看和编辑
   - 用户状态管理（启用/禁用）
   - 用户行为分析和统计

2. **商品管理**
   - 商品信息的增删改查
   - 商品分类和标签管理
   - 库存管理和价格调整
   - 商品上下架管理

3. **订单管理**
   - 订单状态跟踪和更新
   - 订单详情查看和编辑
   - 退款和售后处理
   - 订单统计和分析

4. **系统管理**
   - 系统配置和参数设置
   - 日志查看和分析
   - 数据备份和恢复
   - 性能监控和报警

5. **数据分析**
   - 销售数据统计和分析
   - 用户行为分析
   - 商品热度排行
   - 收入和利润分析

### 3.2 系统非功能性需求分析

#### 3.2.1 性能需求

1. **响应时间**
   - 页面加载时间：< 2秒
   - API响应时间：< 500ms
   - 数据库查询时间：< 200ms
   - AI客服响应时间：< 3秒

2. **并发处理能力**
   - 支持同时在线用户：1000+
   - 支持并发请求数：500+ TPS
   - 数据库连接池：50-100个连接

3. **吞吐量**
   - 每日订单处理量：1万+
   - 页面浏览量：10万+ PV
   - API调用量：100万+ 次/天

#### 3.2.2 可靠性需求

1. **系统可用性**：99.5%以上
2. **数据完整性**：支持事务处理，确保数据一致性
3. **故障恢复**：系统故障后能在30分钟内恢复
4. **数据备份**：每日自动备份，保留30天历史数据

#### 3.2.3 安全性需求

1. **用户认证**：支持安全的用户登录认证
2. **数据加密**：敏感数据传输和存储加密
3. **权限控制**：基于角色的访问控制
4. **SQL注入防护**：防止SQL注入攻击
5. **XSS防护**：防止跨站脚本攻击

#### 3.2.4 可维护性需求

1. **代码结构**：模块化设计，易于维护和扩展
2. **文档完整**：提供完整的开发和部署文档
3. **日志记录**：详细的系统日志和错误日志
4. **监控告警**：系统监控和异常告警机制

### 3.3 可行性分析

#### 3.3.1 技术可行性

1. **技术成熟度**：SpringBoot、Vue3、MySQL等技术都已经非常成熟，有大量的成功案例和丰富的社区资源。

2. **AI技术支持**：火山引擎DeepSeek V3提供了稳定的API接口，技术文档完善，集成难度较低。

3. **开发工具**：IntelliJ IDEA、Visual Studio Code等开发工具对相关技术栈支持良好。

4. **部署环境**：支持多种部署方式，包括传统服务器部署和云服务器部署。

#### 3.3.2 经济可行性

1. **开发成本**：采用开源技术栈，降低了软件许可成本。
2. **运营成本**：云服务器成本相对较低，支持弹性扩容。
3. **AI服务成本**：火山引擎API定价合理，符合项目预算。
4. **维护成本**：系统设计考虑了可维护性，降低了后期维护成本。

#### 3.3.3 操作可行性

1. **用户界面**：采用现代化的UI设计，用户体验良好。
2. **学习成本**：界面直观，用户学习成本低。
3. **管理便捷**：提供完善的后台管理功能，管理员操作便捷。
4. **技术支持**：提供详细的使用文档和技术支持。

## 4 系统设计

### 4.1 系统总体架构设计

#### 4.1.1 架构设计原则

1. **分层原则**：采用经典的三层架构模式，表现层、业务逻辑层、数据访问层职责清晰。

2. **松耦合原则**：各模块间依赖关系最小化，通过接口进行交互。

3. **高内聚原则**：相关功能集中在同一模块内，提高代码的可维护性。

4. **可扩展性原则**：架构支持功能扩展和性能扩展，能够适应业务发展需求。

5. **安全性原则**：完善的安全机制和权限控制，保护用户数据和系统安全。

#### 4.1.2 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        用户层                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   用户端    │  │   管理端    │  │   移动端    │          │
│  │  (Vue3)     │  │  (Vue3)     │  │  (响应式)   │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
                       ┌─────────────┐
                       │   网关层     │
                       │   (Nginx)   │
                       └─────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      应用服务层                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   用户服务   │  │   商品服务   │  │   订单服务   │          │
│  │(SpringBoot) │  │(SpringBoot) │  │(SpringBoot) │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │  AI客服服务  │  │   支付服务   │  │   通知服务   │          │
│  │(SpringBoot) │  │   (预留)    │  │   (预留)    │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      数据层                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   MySQL     │  │    Redis    │  │   文件存储   │          │
│  │   数据库     │  │    缓存     │  │    系统     │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      外部服务层                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │火山引擎DeepSeek│ │   支付接口   │  │   物流接口   │          │
│  │   AI服务     │  │   (预留)    │  │   (预留)    │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

#### 4.1.3 技术架构栈

**前端技术栈**：
- Vue 3.2.45 + Composition API
- Ant Design Vue 3.2.20 (UI组件库)
- Vue Router 4.0 (路由管理)
- Pinia 2.0.28 (状态管理)
- Axios 1.2.2 (HTTP客户端)
- Vite 4.0 (构建工具)

**后端技术栈**：
- SpringBoot 2.5.5 (主框架)
- MyBatis-Plus 3.5.2 (ORM框架)
- Spring Security (安全框架)
- Spring Data Redis (Redis集成)
- Maven 3.9 (依赖管理)

**数据库技术**：
- MySQL 8.0 (主数据库)
- Redis 6.0 (缓存数据库)

**AI服务**：
- 火山引擎DeepSeek V3 API

**部署技术**：
- Docker (容器化)
- Nginx (反向代理)
- Linux (服务器操作系统)

### 4.2 数据库设计

#### 4.2.1 设计规则

1. **命名规范**：
   - 表名使用小写字母和下划线
   - 字段名使用驼峰命名法
   - 主键统一命名为id
   - 外键命名为：关联表名_id

2. **数据类型规范**：
   - 主键使用BIGINT AUTO_INCREMENT
   - 字符串类型根据长度选择VARCHAR或TEXT
   - 时间类型使用DATETIME
   - 金额类型使用DECIMAL(10,2)

3. **索引设计**：
   - 为外键创建索引
   - 为查询频繁的字段创建索引
   - 为组合查询创建复合索引

#### 4.2.2 表结构设计

**用户表(user)**
```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `role` varchar(20) DEFAULT 'USER' COMMENT '角色：USER-用户，ADMIN-管理员',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
);
```

**商品分类表(category)**
```sql
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` text COMMENT '分类描述',
  `parentId` bigint DEFAULT '0' COMMENT '父分类ID',
  `level` tinyint DEFAULT '1' COMMENT '分类级别',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parentId`),
  KEY `idx_level` (`level`)
);
```

**商品表(product)**
```sql
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar(200) NOT NULL COMMENT '商品标题',
  `description` text COMMENT '商品描述',
  `cover` varchar(200) DEFAULT NULL COMMENT '封面图片',
  `images` text COMMENT '商品图片(JSON数组)',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `publisher` varchar(100) DEFAULT NULL COMMENT '出版社',
  `isbn` varchar(20) DEFAULT NULL COMMENT 'ISBN号',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `originalPrice` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int DEFAULT '0' COMMENT '库存',
  `sales` int DEFAULT '0' COMMENT '销量',
  `categoryId` bigint NOT NULL COMMENT '分类ID',
  `tags` varchar(200) DEFAULT NULL COMMENT '标签',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`categoryId`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`),
  FULLTEXT KEY `ft_title_description` (`title`,`description`)
);
```

**订单表(order)**
```sql
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `orderNo` varchar(32) NOT NULL COMMENT '订单号',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `totalAmount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `payAmount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `status` tinyint DEFAULT '1' COMMENT '订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消',
  `payType` tinyint DEFAULT NULL COMMENT '支付方式：1-支付宝，2-微信，3-银行卡',
  `payTime` datetime DEFAULT NULL COMMENT '支付时间',
  `deliveryTime` datetime DEFAULT NULL COMMENT '发货时间',
  `finishTime` datetime DEFAULT NULL COMMENT '完成时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`orderNo`),
  KEY `idx_user_id` (`userId`),
  KEY `idx_status` (`status`)
);
```

**订单详情表(order_item)**
```sql
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `orderId` bigint NOT NULL COMMENT '订单ID',
  `productId` bigint NOT NULL COMMENT '商品ID',
  `productTitle` varchar(200) NOT NULL COMMENT '商品标题',
  `productCover` varchar(200) DEFAULT NULL COMMENT '商品封面',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `quantity` int NOT NULL COMMENT '购买数量',
  `totalAmount` decimal(10,2) NOT NULL COMMENT '小计金额',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`orderId`),
  KEY `idx_product_id` (`productId`)
);
```

**购物车表(cart)**
```sql
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `productId` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`userId`,`productId`),
  KEY `idx_user_id` (`userId`)
);
```

**收藏表(favorite)**
```sql
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `productId` bigint NOT NULL COMMENT '商品ID',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`userId`,`productId`),
  KEY `idx_user_id` (`userId`)
);
```

**评价表(review)**
```sql
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `productId` bigint NOT NULL COMMENT '商品ID',
  `orderId` bigint NOT NULL COMMENT '订单ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` text COMMENT '评价内容',
  `images` text COMMENT '评价图片(JSON数组)',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-隐藏，1-显示',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`userId`),
  KEY `idx_product_id` (`productId`),
  KEY `idx_order_id` (`orderId`)
);
```

**AI对话记录表(ai_chat_record)**
```sql
CREATE TABLE `ai_chat_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `sessionId` varchar(64) NOT NULL COMMENT '会话ID',
  `userId` bigint DEFAULT NULL COMMENT '用户ID',
  `userMessage` text NOT NULL COMMENT '用户消息',
  `aiResponse` text NOT NULL COMMENT 'AI回复',
  `messageType` tinyint DEFAULT '1' COMMENT '消息类型：1-文本，2-图片',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`sessionId`),
  KEY `idx_user_id` (`userId`),
  KEY `idx_create_time` (`createTime`)
);
```

### 4.3 系统接口设计

#### 4.3.1 RESTful API设计规范

1. **URL设计规范**：
   - 使用名词表示资源，动词表示操作
   - 使用复数形式表示资源集合
   - 使用层级结构表示资源关系
   - 例如：`GET /api/products` 获取商品列表

2. **HTTP方法规范**：
   - GET：获取资源
   - POST：创建资源
   - PUT：更新资源（完整更新）
   - PATCH：更新资源（部分更新）
   - DELETE：删除资源

3. **状态码规范**：
   - 200：请求成功
   - 201：创建成功
   - 400：请求参数错误
   - 401：未授权
   - 403：禁止访问
   - 404：资源不存在
   - 500：服务器内部错误

4. **响应格式规范**：
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1640995200000
}
```

#### 4.3.2 核心API接口设计

**用户管理接口**
```
POST   /api/auth/register     用户注册
POST   /api/auth/login        用户登录
POST   /api/auth/logout       用户登出
GET    /api/user/profile      获取用户信息
PUT    /api/user/profile      更新用户信息
POST   /api/user/avatar       更新用户头像
```

**商品管理接口**
```
GET    /api/products          获取商品列表
GET    /api/products/{id}     获取商品详情
POST   /api/products          创建商品（管理员）
PUT    /api/products/{id}     更新商品（管理员）
DELETE /api/products/{id}     删除商品（管理员）
GET    /api/categories        获取分类列表
GET    /api/products/search   搜索商品
```

**购物车接口**
```
GET    /api/cart              获取购物车
POST   /api/cart              添加商品到购物车
PUT    /api/cart/{id}         更新购物车项
DELETE /api/cart/{id}         删除购物车项
DELETE /api/cart              清空购物车
```

**订单管理接口**
```
GET    /api/orders            获取订单列表
GET    /api/orders/{id}       获取订单详情
POST   /api/orders            创建订单
PUT    /api/orders/{id}/pay   支付订单
PUT    /api/orders/{id}/cancel 取消订单
PUT    /api/orders/{id}/finish 完成订单
```

**AI客服接口**
```
POST   /api/ai/chat           发送消息给AI客服
GET    /api/ai/chat/history   获取聊天历史
DELETE /api/ai/chat/session   清除会话记录
```

**收藏接口**
```
GET    /api/favorites         获取收藏列表
POST   /api/favorites         添加收藏
DELETE /api/favorites/{id}    取消收藏
```

**评价接口**
```
GET    /api/reviews           获取评价列表
POST   /api/reviews           添加评价
PUT    /api/reviews/{id}      更新评价
DELETE /api/reviews/{id}      删除评价
```

#### 4.3.3 数据传输格式设计

**统一响应格式**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  },
  "timestamp": 1640995200000
}
```

**分页响应格式**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": 1640995200000
}
```

**错误响应格式**
```json
{
  "code": 400,
  "message": "参数错误",
  "data": null,
  "errors": [
    {
      "field": "username",
      "message": "用户名不能为空"
    }
  ],
  "timestamp": 1640995200000
}
```

通过以上系统设计，我们建立了完整的智能书城管理系统架构，包括总体架构设计、数据库设计和API接口设计。这个设计方案既考虑了系统的功能完整性，也兼顾了性能、安全性和可扩展性等非功能性需求。