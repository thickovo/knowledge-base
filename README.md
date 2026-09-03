```
<div align="center">
# 知识库管理系统
一个支持登录注册、文档管理（无限级目录树 / 全文搜索 / 标签筛选）、标签管理及 AI 智能查询的全栈项目。

</div>
---
## 功能特性
- **认证授权**：基于 JWT + Spring Security，Token 写入 Redis 实现登录态管理
- **文档管理**：无限级目录树，支持文档层级嵌套
- **标签系统**：文档与标签多对多关联，灵活分类
- **检索能力**：全文关键字搜索 + 标签筛选
- **AI 智能查询**：基于硅基流动 Function Calling，支持通过自然语言查询文档列表
- **前后端分离**：Vue 3 单页应用，RESTful 接口对接
- **接口自动化测试**：Python + Pytest + Requests 完成核心接口测试覆盖

---
## 技术栈
### 后端
| 技术              | 版本     |
| ----------------- | -------- |
| Spring Boot       | 2.7.6    |
| Spring Security   | 5.x      |
| MyBatis‑Plus      | 3.5.9    |
| JWT (jjwt)        | 0.11.x   |
| Redis             | 5+       |
| MySQL             | 8.0      |
| 硅基流动 API      | Function Calling |
| JDK               | 8+（推荐 17） |

### 前端
| 技术          | 说明                  |
| ------------- | --------------------- |
| Vue 3         | Composition API       |
| Element Plus  | UI 组件库             |
| Vue Router 4  | 路由 + 鉴权守卫       |
| Axios         | HTTP 请求封装         |
| Vite          | 构建工具              |

### 测试
| 工具 | 说明 |
|------|------|
| Python | 脚本运行环境 |
| Pytest | 测试框架 |
| Requests | HTTP 请求库 |

---
## 项目结构
```text
knowledge-base-project/
├── knowledge-base/                              # Spring Boot 后端
│   ├── src/main/java/com/gao/knowledgebase/
│   │   ├── common/                              # 统一响应 Result
│   │   ├── config/                              # Security / JWT Filter 配置
│   │   ├── controller/                          # 接口层
│   │   ├── dto/                                 # 请求 / 响应 DTO
│   │   ├── entity/                              # 数据库实体
│   │   ├── mapper/                              # MyBatis‑Plus Mapper
│   │   ├── service/                             # 业务层
│   │   └── utils/                               # JwtUtils 等工具类
│   └── src/main/resources/
│       ├── application.properties
│       ├── db/
│       │   └── schema.sql
│       └── static/
├── knowledge-base-frontend/                     # Vue 3 前端
│   ├── src/
│   │   ├── api/                                 # axios 封装与接口定义
│   │   ├── components/                          # 通用组件
│   │   ├── router/                              # 路由 + 鉴权守卫
│   │   ├── utils/                               # token 读写等工具
│   │   └── views/                               # 页面
│   ├── package.json
│   └── vite.config.js
└── test_api.py                                  # pytest接口自动化测试脚本
```

------

## 环境要求

| 工具   | 版本要求      |
| ------ | ------------- |
| JDK    | 8+（推荐 17） |
| Maven  | 3.6+          |
| MySQL  | 5.7+ / 8.x    |
| Redis  | 5+            |
| Node   | 16+           |
| npm    | 8+            |
| Python | 3.8+          |

------

## 快速开始

### 1. 创建数据库

```
CREATE DATABASE knowledge_base DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 初始化表结构

执行项目中的 SQL 脚本：

```
knowledge-base/src/main/resources/db/schema.sql
```

### 3. 修改后端配置

编辑 `knowledge-base/src/main/resources/application.properties`：

```
spring.datasource.url=jdbc:mysql://localhost:3306/knowledge_base?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的密码
spring.redis.host=127.0.0.1
spring.redis.port=6379
# 硅基流动 API 配置（需自行注册申请）
ai.model.api-key=${SILICONFLOW_API_KEY}
ai.model.model-name=Qwen/Qwen2.5-7B-Instruct
ai.model.base-url=https://api.siliconflow.cn/v1/chat/completions
```

### 4. 启动 Redis

- Windows：双击 `redis-server.exe`，或执行 `redis-server redis.windows.conf`
- macOS：`brew services start redis`
- Linux：`sudo systemctl start redis`

### 5. 启动后端

```
cd knowledge-base
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

### 6. 启动前端

```
cd knowledge-base-frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`。

------

## 默认账号

| 用户名 | 密码   | 角色   |
| ------ | ------ | ------ |
| admin  | 123456 | 管理员 |

> ⚠️ **安全提示**：上线部署前请务必修改默认密码，并删除默认账号或重置密码。

------

## 端口清单

| 服务  | 端口 |
| ----- | ---- |
| 后端  | 8080 |
| 前端  | 5173 |
| MySQL | 3306 |
| Redis | 6379 |

------

## 🧪 接口测试

使用 Python + Pytest + Requests 对核心接口进行了自动化测试，覆盖登录、文档 CRUD、标签管理、AI 对话、权限校验等场景。

> ⚠️ 运行测试前，请确保后端服务已经启动。

```
pip install requests pytest
pytest test_api.py -v
```

------

## 接口文档

> 所有接口前缀：`/api`，除 `/api/user/register`、`/api/user/login` 外均需在 Header 中携带 `Authorization: Bearer <token>`。

### 统一响应格式

```
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| code | 含义     |
| ---- | -------- |
| 200  | 成功     |
| 0    | 业务失败 |
| 401  | 未登录   |
| 403  | 无权限   |

### 用户模块

| 方法 | 路径                 | 说明                 |
| ---- | -------------------- | -------------------- |
| POST | `/api/user/register` | 注册                 |
| POST | `/api/user/login`    | 登录，返回 JWT       |
| GET  | `/api/user/me`       | 获取当前登录用户信息 |

**登录请求示例**：

```
{
  "username": "admin",
  "password": "123456"
}
```

**登录响应示例**：

```
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 文档模块

| 方法   | 路径                        | 说明                                         |
| ------ | --------------------------- | -------------------------------------------- |
| POST   | `/api/document/create`      | 创建文档（title, content, parentId, tagIds） |
| GET    | `/api/document/list`        | 文档列表（parentId, tagId, keyword）         |
| GET    | `/api/document/{id}`        | 文档详情                                     |
| PUT    | `/api/document/update`      | 更新文档（id, title, content）               |
| DELETE | `/api/document/delete/{id}` | 删除文档                                     |

### 标签模块

| 方法   | 路径                   | 说明     |
| ------ | ---------------------- | -------- |
| POST   | `/api/tag/create`      | 创建标签 |
| GET    | `/api/tag/list`        | 标签列表 |
| PUT    | `/api/tag/update`      | 更新标签 |
| DELETE | `/api/tag/delete/{id}` | 删除标签 |

### AI 对话模块

| 方法 | 路径      | 说明                               |
| ---- | --------- | ---------------------------------- |
| GET  | `/api/ai` | AI 对话 / 智能查询，参数 `message` |

------

## AI 辅助开发说明

- 前端 Vue 3 代码由 **Claude 3.5 Sonnet** 辅助生成，作者在其基础上完成了接口适配、路由配置和样式调整。

- 后端 Spring Boot 代码由作者**独立手写完成**，包括：数据库设计、接口开发、JWT 认证、权限控制、目录树、标签系统、全文搜索、AI 智能查询（Function Calling）。

- 前后端联调由作者独立完成。

  > 该模式用于在实际工作中合理利用 AI 工具提升前端开发效率，同时保证后端核心逻辑的稳定性与可控性。

------

## 开发计划 / Roadmap

-  文档 Markdown 实时预览
-  文档版本历史与回滚
-  文档协作与权限细分（读 / 写 / 私有 / 共享）
-  全文检索升级为 Elasticsearch
-  接入 OSS / MinIO 实现文件附件上传
-  单元测试与接口测试覆盖完善

------

## 常见问题

- **启动后端报错 `Redis connection refused`**：请确认 Redis 已启动，并检查 `application.properties` 中的 host / port。
- **跨域问题**：后端已配置 CORS，如仍报错请检查前端 `vite.config.js` 中的代理设置。
- **JWT 401**：Token 失效或未携带，前端会在拦截器中自动跳转登录页。
- **pytest执行失败**：确认后端服务正常启动，核对脚本内访问地址、账号密码。

------

## 贡献指南

欢迎提交 Issue 与 Pull Request。提交前请：

1. Fork 本仓库，新建特性分支
2. 保持代码风格统一，后端遵循阿里巴巴 Java 开发手册
3. 提交信息清晰描述变更内容
4. 涉及接口变动请同步更新本 README 和测试脚本

------

## 作者

**幼幼的小熊** 🧸

- GitHub：https://github.com/thickovo
- 项目仅用于学习与技术交流，欢迎 Star ⭐
