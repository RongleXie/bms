<div align="center">
    <h1>BMS 博客管理系统</h1>
    <p>基于 Snowy v3.6.1 框架开发的博客内容管理平台</p>
</div>

<p align="center">
    <a href="https://github.com/RongleXie/bms">
        <img src="https://img.shields.io/badge/vue-3-blue.svg" alt="Vue 3">
    </a>
    <a href="https://github.com/RongleXie/bms">
        <img src="https://img.shields.io/badge/spring--boot-3-green.svg" alt="Spring Boot 3">
    </a>
    <a href="https://github.com/RongleXie/bms">
        <img src="https://img.shields.io/badge/mybatis--plus-3-blue.svg" alt="MyBatis-Plus">
    </a>
    <a href="https://github.com/RongleXie/bms">
        <img src="https://img.shields.io/badge/vite-5-green.svg" alt="Vite 5">
    </a>
    <a href="https://github.com/RongleXie/bms/blob/master/LICENSE">
        <img src="https://img.shields.io/badge/license-Apache%202-red" alt="License">
    </a>
</p>

---

## 项目介绍

BMS（Blog Management System）是一个功能完善的博客内容管理系统，基于小诺开源框架 Snowy v3.6.1 开发。系统集成了国密加解密插件，采用前后端分离架构，支持文章管理、分类标签、评论审核、媒体库等核心功能。

### 核心特性

- **文章管理**：支持文章发布、编辑、删除、搜索、分类、标签
- **Markdown编辑器**：集成Markdown编辑器，支持实时预览
- **定时发布**：支持设置文章定时发布时间
- **版本历史**：自动保存文章编辑历史，支持版本对比与回滚
- **全文搜索**：基于MySQL FULLTEXT索引的高效全文搜索
- **分类标签**：灵活的文章分类与标签管理
- **评论管理**：评论审核、回复、删除功能
- **媒体库**：图片/视频/附件上传与管理
- **系统管理**：用户、角色、权限、菜单管理
- **安全特性**：国密SM2/SM3/SM4算法支持

### 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 + Vite 5 |
| UI组件库 | Ant Design Vue 4 |
| 状态管理 | Pinia |
| 后端框架 | Spring Boot 3 |
| ORM框架 | MyBatis-Plus 3 |
| 权限认证 | Sa-Token |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis 6.0+ |
| 安全加密 | 国密 SM2/SM3/SM4 |

---

## 快速开始

### 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | Java运行环境 |
| Node.js | 18+ | 前端运行环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存服务 |
| Maven | 3.6+ | 项目构建 |

### 启动后端

```bash
# 1. 导入数据库
# 执行 .planning/sql/bms_init.sql 初始化数据库

# 2. 修改数据库配置
# 编辑 bms-web-app/src/main/resources/application-dev.yml
# 配置 MySQL 和 Redis 连接信息

# 3. 编译项目
mvn clean install -DskipTests

# 4. 启动后端服务
# 运行 BmsApplication.java 或使用命令
java -jar bms-web-app/target/bms-web-app.jar
```

后端默认运行在 `http://localhost:82`

### 启动前端

```bash
# 进入前端目录
cd bms-admin-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端默认运行在 `http://localhost:81`

### 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| superAdmin | 123456 | 超级管理员 |

---

## 项目结构

```
bms/
├── bms-common/              # 公共模块
├── bms-plugin-api/          # 插件API
│   ├── bms-plugin-auth-api  # 认证API
│   ├── bms-plugin-biz-api   # 业务API
│   ├── bms-plugin-client-api# 客户端API
│   ├── bms-plugin-dev-api   # 开发工具API
│   ├── bms-plugin-gen-api   # 代码生成API
│   ├── bms-plugin-mobile-api# 移动端API
│   └── bms-plugin-sys-api   # 系统API
├── bms-plugin/              # 插件实现
│   ├── bms-plugin-auth      # 认证插件
│   ├── bms-plugin-biz       # 业务插件（文章、分类、标签、评论等）
│   ├── bms-plugin-client    # 客户端插件
│   ├── bms-plugin-dev       # 开发工具插件
│   ├── bms-plugin-gen       # 代码生成插件
│   ├── bms-plugin-mobile    # 移动端插件
│   └── bms-plugin-sys       # 系统插件
├── bms-web-app/             # 主启动模块
├── bms-admin-web/           # 前端Vue3项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── components/      # 公共组件
│   │   ├── views/           # 页面视图
│   │   ├── router/          # 路由配置
│   │   └── store/           # 状态管理
│   └── public/              # 静态资源
└── .planning/               # 项目规划文档
    ├── docs/                # 设计文档
    ├── sql/                 # SQL脚本
    └── prototype/           # 原型页面
```

---

## 版本历史

### V1.0.0 (2026-03-25)

首次正式发布，完成核心功能开发：

- ✅ 文章管理：发布、编辑、删除、搜索、分类、标签
- ✅ 分类标签：分类管理、标签管理
- ✅ 评论管理：评论审核、回复、删除
- ✅ 媒体库：图片/视频/附件上传管理
- ✅ 系统管理：用户、角色、权限、菜单

### V2.0.0 (开发中)

功能增强版本：

- ✅ Markdown编辑器集成
- ✅ 文章定时发布
- ✅ 文章版本历史
- ✅ 全文搜索优化
- ⏳ 安全修复（进行中）
- ⏳ 性能优化（进行中）

---

## 功能截图

> 待补充系统截图

---

## 文档

| 文档 | 说明 |
|------|------|
| [PRD文档](./.planning/docs/PRD.md) | 产品需求文档 |
| [功能清单](./.planning/docs/功能清单.md) | 功能模块清单 |
| [数据库设计](./.planning/docs/数据库设计.md) | 数据库表结构设计 |
| [测试报告](./.planning/docs/测试报告.md) | 系统测试报告 |
| [综合审查报告](./.planning/docs/M4.5_综合审查报告.md) | 代码审查报告 |
| [构建指南](./BUILD_GUIDE.md) | 项目构建指南 |
| [发布说明](./RELEASE_NOTES.md) | 版本发布说明 |

---

## 开源协议

本项目基于 Apache License 2.0 协议开源。

---

## 致谢

本项目基于 [Snowy](https://gitee.com/xiaonuobase/snowy) 框架开发，感谢小诺开源团队提供优秀的快速开发平台。