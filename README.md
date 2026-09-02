- # 知识库管理系统

  > 基于 SpringBoot + Vue3 实现的全栈知识库管理系统，支持账号鉴权、无限级文档目录、标签管理、全文检索，集成大模型 Function‑Calling 实现 AI 自然语言查询知识库。

  ## 📖 项目简介

  本项目是一套**前后端分离**的个人知识库管理系统，主要用于知识文档的整理、分类、检索。 后端全部业务逻辑独立开发，使用 JWT + Redis 完成会话鉴权；前端基于 Vue3 搭建，接入硅基流动大模型 API，实现自然语言直接查询内部文档，适合作为 Java 全栈学习 / 面试演示项目。

  ## ✨ 功能特性

  - 🔐 **身份认证授权**：JWT + SpringSecurity，Redis 维护登录会话状态
  - 📂 **无限级文档目录树**：支持文档多层级嵌套管理
  - 🏷️ **标签管理**：文档与标签多对多关联，实现灵活分类
  - 🔍 **检索能力**：关键词全文检索 + 标签条件筛选
  - 🤖 **AI 智能查询**：基于硅基流动 Function Calling，自然语言检索内部文档
  - 🌐 **前后端分离架构**：Vue3 SPA，遵循 RESTful API 接口规范

  ## 🛠️ 技术栈

  ### 后端

  | 技术            | 版本             |
  | --------------- | ---------------- |
  | Spring Boot     | 2.7.6            |
  | Spring Security | 5.x              |
  | MyBatis‑Plus    | 3.5.9            |
  | JJWT            | 0.11.x           |
  | Redis           | 5+               |
  | MySQL           | 8.0              |
  | JDK             | 8+（推荐 17）    |
  | 硅基流动 API    | Function Calling |

  ### 前端

  | 技术         | 说明                 |
  | ------------ | -------------------- |
  | Vue 3        | Composition API 语法 |
  | Element Plus | UI组件库             |
  | Vue Router 4 | 路由、路由鉴权守卫   |
  | Axios        | 请求封装、请求拦截器 |
  | Vite         | 项目构建工具         |

  ## 📁 项目目录结构

  ```
  knowledge-base-project/
  ├── knowledge-base/                              # SpringBoot 后端工程
  │   ├── src/main/java/com/gao/knowledgebase/
  │   │   ├── common/                              # 全局统一返回封装、公共常量
  │   │   ├── config/                              # Security、JWT过滤器、跨域配置
  │   │   ├── controller/                          # HTTP 接口控制器
  │   │   ├── dto/                                 # 请求/响应数据传输对象
  │   │   ├── entity/                              # 数据库实体类
  │   │   ├── mapper/                              # MyBatis‑Plus Mapper接口
  │   │   ├── service/                             # 业务逻辑层
  │   │   └── utils/                               # Jwt工具、通用工具类
  │   └── src/main/resources/
  │       ├── application.properties               # 项目配置文件
  │       ├── db/
  │       │   └── schema.sql                       # 数据库建表脚本
  │       └── static/
  └── knowledge-base-frontend/                     # Vue3 前端工程
      ├── src/
      │   ├── api/                                 # axios封装、接口请求定义
      │   ├── components/                          # 公共业务组件
      │   ├── router/                              # 路由配置与鉴权守卫
      │   ├── utils/                               # token处理、通用工具
      │   └── views/                               # 页面视图组件
      ├── package.json
      └── vite.config.js
  ```

  ## 💻 环境依赖

  | 工具    | 版本要求     |
  | ------- | ------------ |
  | JDK     | 8+（推荐17） |
  | Maven   | 3.6+         |
  | MySQL   | 5.7 / 8.x    |
  | Redis   | 5.0+         |
  | Node.js | 16+          |
  | npm     | 8+           |

  ## 🚀 快速部署运行

  > 前置条件：本地已启动 MySQL、Redis 服务

  ### 1. 创建数据库

  执行 SQL 创建数据库：

  ```
  CREATE DATABASE knowledge_base DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

  ### 2. 初始化数据表

  导入后端项目脚本文件： `knowledge-base/src/main/resources/db/schema.sql`

  ### 3. 修改后端配置文件

  ```
  knowledge-base/src/main/resources/application.properties
  # 数据库配置
  spring.datasource.url=jdbc:mysql://localhost:3306/knowledge_base?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
  spring.datasource.username=root
  spring.datasource.password=你的数据库密码
  
  # Redis配置
  spring.redis.host=127.0.0.1
  spring.redis.port=6379
  
  # 硅基流动大模型配置，自行前往平台申请api‑key
  ai.model.api-key=${SILICONFLOW_API_KEY}
  ai.model.model-name=Qwen/Qwen2.5-7B-Instruct
  ai.model.base-url=https://api.siliconflow.cn/v1/chat/completions
  ```

  ### 4. 启动 Redis

  ```
  # Windows
  redis-server.exe redis.windows.conf
  
  # macOS
  brew services start redis
  
  # Linux
  sudo systemctl start redis
  ```

  ### 5. 启动后端服务

  ```
  cd knowledge-base
  mvn spring-boot:run
  ```

  后端访问地址：`http://127.0.0.1:8080`

  ### 6. 启动前端服务

  ```
  cd knowledge-base-frontend
  npm install
  npm run dev
  ```

  前端访问地址：`http://127.0.0.1:5173`

  ## 🔑 默认测试账号

  | 用户名 | 密码   | 角色   |
  | ------ | ------ | ------ |
  | admin  | 123456 | 管理员 |

  > ⚠️ **重要安全提醒**：正式环境部署务必修改默认账号密码，不要直接使用该账号上线。

  ## 📡 端口占用一览

  | 服务           | 端口 |
  | -------------- | ---- |
  | SpringBoot后端 | 8080 |
  | Vite前端       | 5173 |
  | MySQL          | 3306 |
  | Redis          | 6379 |

  ## 📮 API接口说明

  > 接口统一前缀：`/api` 除登录、注册接口外，其余接口请求头必须携带：`Authorization: Bearer {token}`

  ### 统一返回格式

  ```
  {
    "code": 200,
    "message": "success",
    "data": {}
  }
  ```

  状态码说明

  | code | 含义               |
  | ---- | ------------------ |
  | 200  | 请求成功           |
  | 0    | 业务逻辑异常       |
  | 401  | 未登录 / Token失效 |
  | 403  | 权限不足           |

  ### 用户模块

  | 请求方式 | 接口地址             | 接口描述              |
  | -------- | -------------------- | --------------------- |
  | POST     | `/api/user/register` | 用户注册              |
  | POST     | `/api/user/login`    | 用户登录，返回JWT令牌 |
  | GET      | `/api/user/me`       | 获取当前登录用户信息  |

  登录请求示例：

  ```
  {
    "username":"admin",
    "password":"123456"
  }
  ```

  登录响应示例：

  ```
  {
    "code":200,
    "message":"success",
    "data":"eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

  ### 文档模块

  | 请求方式 | 接口地址                    | 接口描述                                       |
  | -------- | --------------------------- | ---------------------------------------------- |
  | POST     | `/api/document/create`      | 创建文档                                       |
  | GET      | `/api/document/list`        | 获取文档列表，支持parentId、tagId、keyword过滤 |
  | GET      | `/api/document/{id}`        | 获取文档详情                                   |
  | PUT      | `/api/document/update`      | 更新文档内容                                   |
  | DELETE   | `/api/document/delete/{id}` | 删除指定文档                                   |

  ### 标签模块

  | 请求方式 | 接口地址               | 接口描述     |
  | -------- | ---------------------- | ------------ |
  | POST     | `/api/tag/create`      | 创建标签     |
  | GET      | `/api/tag/list`        | 获取全部标签 |
  | PUT      | `/api/tag/update`      | 修改标签     |
  | DELETE   | `/api/tag/delete/{id}` | 删除标签     |

  ### AI查询模块

  | 请求方式 | 接口地址  | 接口描述                |
  | -------- | --------- | ----------------------- |
  | GET      | `/api/ai` | AI智能查询，入参message |

  ## 🤖 AI开发说明

  - 前端Vue3基础代码由 Claude 3.5 Sonnet 辅助生成；本人完成接口对接、路由守卫、页面样式调优。
  - **后端全部核心业务逻辑为手写实现**：包含数据库表设计、鉴权逻辑、无限级目录树、标签多对多、全文检索、Function‑Calling调用大模型。
  - 前后端联调、接口调试全部手动完成。

  > 项目演示如何合理使用AI辅助前端编码，后端核心业务逻辑保证可控、可阅读、可维护。

  ## 📋 后续开发计划 Roadmap

  -  Markdown文档实时预览渲染
  -  文档历史版本记录与回滚
  -  细粒度权限控制（私有文档、共享文档、读写权限）
  -  全文检索替换为 Elasticsearch
  -  接入对象存储 OSS/MinIO，支持附件上传
  -  补充单元测试、接口自动化测试

  ## ❓ 常见问题排查

  1. **Redis connection refused**

     > Redis服务未正常启动，检查本机Redis服务状态以及配置文件中host、port参数。

  2. **前端跨域报错**

     > 后端已全局配置CORS跨域；如果依然出现跨域，检查前端 vite.config.js 的代理配置。

  3. **接口返回401**

     > Token过期、请求头没有携带Authorization；前端拦截器会自动跳转到登录页面。

  ## 🤝 参与贡献

  欢迎提交 Issue 和 Pull Request，参与项目优化：

  1. Fork 当前仓库，新建功能分支开发
  2. Java代码遵循《阿里巴巴Java开发手册》编码规范
  3. commit提交描述清晰说明改动内容
  4. 修改接口逻辑时，请同步更新本README接口文档

  ## 🧑‍💻 项目作者

  **幼幼的小熊** 🧸

  - GitHub：https://github.com/thickovo
  - 本项目仅供学习交流使用，欢迎Star ⭐
