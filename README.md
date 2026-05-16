
# 本地生活服务平台

一个符合中国一线互联网企业标准的本地生活服务平台（类似美团/饿了么）单体应用项目。

## 项目技术栈

### 后端技术
- **核心框架**: Spring Boot 3.2.x + Spring Cloud 2023.0.0
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **消息队列**: Kafka 2.8+
- **ORM框架**: MyBatis-Plus 3.5.5
- **安全框架**: Spring Security + JWT
- **API文档**: Knife4j (Swagger)

### 前端技术
- **框架**: Vue.js 3 + Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **构建工具**: Vite

## 项目结构

```
local-life-service-platform/
├── backend/                           # 后端代码
│   ├── src/main/java/com/example/life/
│   │   ├── controller/               # REST API控制器
│   │   ├── service/                   # 业务逻辑层
│   │   │   └── impl/                  # 服务实现
│   │   ├── mapper/                    # 数据访问层
│   │   ├── entity/                    # 数据库实体
│   │   ├── dto/                       # 数据传输对象
│   │   │   ├── request/               # 请求DTO
│   │   │   └── response/              # 响应DTO
│   │   ├── config/                    # 配置类
│   │   ├── security/                  # 安全相关
│   │   └── common/                    # 通用工具
│   ├── src/main/resources/
│   │   ├── mapper/                    # MyBatis映射文件
│   │   ├── db/                        # 数据库脚本
│   │   └── application.yml            # 应用配置
│   └── pom.xml                        # Maven配置
├── frontend/                          # 前端代码
│   ├── src/
│   │   ├── views/                     # 页面组件
│   │   ├── components/                # 通用组件
│   │   ├── stores/                    # 状态管理
│   │   ├── api/                       # API接口
│   │   ├── router/                    # 路由配置
│   │   ├── App.vue                    # 根组件
│   │   └── main.ts                    # 入口文件
│   ├── index.html
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
├── docs/                              # 文档目录
└── docker-compose.yml                 # Docker配置
```

## 功能模块

### 核心业务功能

1. **用户管理模块**
   - 用户注册、登录、权限管理
   - JWT Token认证

2. **商家管理模块**
   - 商家入驻、资质审核
   - 店铺信息管理、营业状态控制

3. **商品管理模块**
   - 商品分类、属性管理
   - 上下架操作、库存控制

4. **订单配送系统**
   - 订单创建、支付集成
   - 配送状态跟踪、异常处理

5. **骑手调度模块**
   - 骑手注册、认证
   - 接单管理、位置追踪

6. **评价系统**
   - 评分、评论发布
   - 商家回复、评价管理

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Kafka 2.8+

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

### Docker启动

```bash
docker-compose up -d
```

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/me` - 获取当前用户

### 商家接口
- `GET /api/merchant/list` - 获取商家列表
- `GET /api/merchant/{id}` - 获取商家详情
- `GET /api/merchant/category/{category}` - 按分类获取商家
- `POST /api/merchant/register` - 商家入驻

### 商品接口
- `GET /api/product/{id}` - 获取商品详情
- `GET /api/product/merchant/{merchantId}` - 获取商家商品
- `POST /api/product/merchant/{merchantId}` - 创建商品

### 订单接口
- `POST /api/order/create` - 创建订单
- `GET /api/order/{id}` - 获取订单详情
- `GET /api/order/user` - 获取用户订单

### 骑手接口
- `POST /api/rider/register` - 骑手注册
- `PUT /api/rider/{riderId}/location` - 更新位置
- `POST /api/rider/{riderId}/order/{orderId}/accept` - 接单

### 评价接口
- `POST /api/review/create` - 创建评价
- `GET /api/review/merchant/{merchantId}` - 获取商家评价
- `PUT /api/review/merchant/{merchantId}/{reviewId}/reply` - 回复评价

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin | 管理员 |
| user001 | admin | 普通用户 |
| merchant001 | admin | 商家 |
| rider001 | admin | 骑手 |

## 项目特性

### 技术特性
- 企业级单体应用架构
- 完整的用户认证与权限控制
- Redis缓存热点数据
- Kafka异步处理任务
- RESTful API设计风格

### 业务特性
- 完整的商品上下架流程
- 库存实时管理系统
- 订单状态机管理
- 骑手位置实时更新
- 附近商家查询算法

## License

- v1.0.1
