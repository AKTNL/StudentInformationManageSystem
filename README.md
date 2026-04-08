# 学生信息管理系统

## 项目简介

这是一个基于前后端分离架构的学生信息管理系统，采用 Spring Boot + Vue 3 技术栈实现。系统支持学生信息管理、课程管理、选课管理、文件上传等功能。

## 技术栈

### 后端
- **框架**: Spring Boot 3.5.8
- **数据库**: MySQL + MyBatis
- **缓存**: Redis
- **服务发现**: Nacos
- **文件存储**: MinIO
- **搜索**: Elasticsearch
- **认证**: JWT
- **WebSocket**: 实时通信
- **工具**: Hutool

### 前端
- **框架**: Vue 3 + Vite
- **UI组件**: Element Plus
- **路由**: Vue Router
- **网络**: Axios
- **PDF预览**: vue-pdf-embed

## 项目结构

```
StudentInformationManageSystem/
├── student-manage-system-backend/     # 后端服务
│   ├── src/main/java/com/student/
│   │   ├── controller/                 # 控制器层
│   │   │   ├── AuthController.java    # 认证控制器
│   │   │   ├── StudentController.java # 学生控制器
│   │   │   ├── CourseController.java  # 课程控制器
│   │   │   ├── CourseSelectionController.java # 选课控制器
│   │   │   ├── ClassController.java   # 班级控制器
│   │   │   ├── FileController.java    # 文件控制器
│   │   │   ├── UserController.java    # 用户控制器
│   │   │   └── CapthaController.java  # 验证码控制器
│   │   ├── service/                   # 服务层
│   │   ├── entity/                    # 实体类
│   │   ├── utils/                     # 工具类
│   │   └── websocket/                 # WebSocket
│   └── src/main/resources/            # 配置文件
│
└── student-manage-system-frontend/    # 前端应用
    └── student-frontend/
        ├── src/
        │   ├── api/                   # API 接口
        │   ├── views/                 # 页面视图
        │   ├── router/                # 路由配置
        │   ├── utils/                 # 工具函数
        │   ├── components/            # 组件
        │   └── styles/                # 样式
        └── package.json
```

## 功能模块

### 1. 用户认证
- 登录/登出
- JWT token 认证
- 验证码生成

### 2. 学生管理
- 学生信息增删改查
- 学生列表展示

### 3. 课程管理
- 课程信息管理
- 课程列表

### 4. 选课管理
- 学生选课
- 选课记录查询

### 5. 班级管理
- 班级信息管理

### 6. 文件管理
- 文件上传 (MinIO)
- 文件预览 (PDF)

### 7. 实时通信
- WebSocket 消息推送

## 快速开始

### 后端启动

1. 确保已安装 JDK 21 和 Maven

2. 配置数据库连接 (application.yml)

3. 编译运行:
```bash
cd student-manage-system-backend
mvn clean install
mvn spring-boot:run
```

### 前端启动

1. 确保已安装 Node.js

2. 安装依赖:
```bash
cd student-manage-system-frontend/student-frontend
npm install
```

3. 启动开发服务器:
```bash
npm run dev
```

4. 构建生产版本:
```bash
npm run build
```

## API 接口

| 模块 | 路径 | 说明 |
|------|------|------|
| 认证 | /api/auth/login | 登录 |
| 学生 | /api/student/* | 学生管理 |
| 课程 | /api/course/* | 课程管理 |
| 选课 | /api/selection/* | 选课管理 |
| 班级 | /api/class/* | 班级管理 |
| 文件 | /api/file/* | 文件上传 |

## 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_db
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
  nacos:
    discovery:
      server-addr: 127.0.0.1:8848
```

### 前端配置 (vite.config.js)

配置 API 请求地址和代理转发。

## 环境要求

- JDK 21+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+
- Nacos (可选)
- MinIO (可选)
- Elasticsearch 8.x (可选)

## 许可证

MIT License