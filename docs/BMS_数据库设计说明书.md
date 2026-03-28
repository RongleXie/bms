# 博客管理系统（BMS）数据库设计说明书

**文档版本**: V2.0.0
**编写日期**: 2026-03-26
**项目名称**: 博客管理系统 (Blog Management System)
**数据库类型**: MySQL 8.0+

---

## 目录

1. [概述](#1-概述)
2. [数据库设计原则](#2-数据库设计原则)
3. [数据库架构](#3-数据库架构)
4. [核心业务表设计](#4-核心业务表设计)
5. [系统基础表设计](#5-系统基础表设计)
6. [索引设计](#6-索引设计)
7. [数据字典](#7-数据字典)
8. [安全设计](#8-安全设计)

---

## 1. 概述

### 1.1 文档目的

本文档详细描述博客管理系统（BMS）的数据库设计，包括表结构、字段定义、索引策略、数据关系等，为开发人员提供数据库开发和维护的参考依据。

### 1.2 系统概述

BMS 是一个基于 Spring Boot 3.x + Vue 3.x 开发的博客管理系统，采用前后端分离架构，支持文章管理、分类标签、评论互动、媒体资源管理等核心功能。

### 1.3 技术栈

| 组件 | 技术 |
|------|------|
| 数据库 | MySQL 8.0+ |
| ORM框架 | MyBatis-Plus |
| 连接池 | HikariCP |
| 缓存 | Redis |
| 字符集 | utf8mb4 |

---

## 2. 数据库设计原则

### 2.1 命名规范

- **表名**: 大写字母，下划线分隔，如 `BMS_ARTICLE`
- **字段名**: 大写字母，下划线分隔，如 `CREATE_TIME`
- **主键**: 统一使用 `ID`
- **外键**: 关联表名简写 + `_ID`，如 `CATEGORY_ID`
- **索引**: `IDX_表名_字段名` 或 `UK_表名_字段名`（唯一索引）
- **全文索引**: `FT_表名_字段名`

### 2.2 字段规范

- **主键**: VARCHAR(32)，使用雪花算法生成
- **状态字段**: VARCHAR(20)，使用枚举值字符串
- **删除标志**: VARCHAR(255)，默认值 `NOT_DELETE`，支持逻辑删除
- **时间字段**: DATETIME 类型
- **扩展字段**: TEXT 类型，存储 JSON 格式扩展信息

### 2.3 通用字段

每张业务表均包含以下通用字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| ID | VARCHAR(32) | 主键ID |
| EXT_JSON | TEXT | 扩展信息JSON |
| DELETE_FLAG | VARCHAR(255) | 删除标志 |
| CREATE_TIME | DATETIME | 创建时间 |
| CREATE_USER | VARCHAR(32) | 创建人ID |
| UPDATE_TIME | DATETIME | 更新时间 |
| UPDATE_USER | VARCHAR(32) | 更新人ID |

---

## 3. 数据库架构

### 3.1 模块划分

```
BMS 数据库
├── BMS 核心业务模块 (BMS_*)
│   ├── 文章管理 (BMS_ARTICLE, BMS_ARTICLE_TAG, BMS_ARTICLE_VERSION)
│   ├── 分类管理 (BMS_CATEGORY)
│   ├── 标签管理 (BMS_TAG)
│   ├── 评论管理 (BMS_COMMENT)
│   └── 媒体管理 (BMS_MEDIA)
│
├── 系统管理模块 (SYS_*)
│   ├── 用户管理 (SYS_USER, SYS_USER_EXT, SYS_USER_PASSWORD)
│   ├── 组织机构 (SYS_ORG, SYS_ORG_EXT)
│   ├── 职位管理 (SYS_POSITION)
│   ├── 角色权限 (SYS_ROLE, SYS_RESOURCE, SYS_RELATION)
│   └── 用户组 (SYS_GROUP)
│
├── 开发工具模块 (DEV_*)
│   ├── 配置管理 (DEV_CONFIG)
│   ├── 字典管理 (DEV_DICT)
│   ├── 文件管理 (DEV_FILE)
│   ├── 日志管理 (DEV_LOG)
│   ├── 定时任务 (DEV_JOB)
│   └── 消息通知 (DEV_MESSAGE, DEV_SMS, DEV_EMAIL, DEV_PUSH)
│
├── 客户端模块 (CLIENT_*)
│   └── C端用户 (CLIENT_USER, CLIENT_USER_EXT, CLIENT_USER_PASSWORD, CLIENT_RELATION)
│
└── 认证模块 (AUTH_*)
    └── 三方登录 (AUTH_THIRD_USER)
```

### 3.2 ER 关系图

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│ BMS_ARTICLE │──┬────│BMS_CATEGORY │       │   BMS_TAG   │
└─────────────┘  │    └─────────────┘       └─────────────┘
       │         │                                │
       │         │    ┌─────────────┐            │
       │         └───►│BMS_ARTICLE_ │◄───────────┘
       │              │    TAG      │
       │              └─────────────┘
       │
       │              ┌─────────────┐
       └─────────────►│ BMS_COMMENT │
                      └─────────────┘
                             │
                      ┌─────────────┐
                      │  BMS_MEDIA  │
                      └─────────────┘
```

---

## 4. 核心业务表设计

### 4.1 文章表 (BMS_ARTICLE)

**表说明**: 存储文章基本信息，是博客系统的核心实体。

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| ID | VARCHAR(32) | NOT NULL | - | 主键ID |
| TITLE | VARCHAR(200) | NOT NULL | - | 文章标题 |
| SUMMARY | VARCHAR(500) | NULL | - | 文章摘要 |
| CONTENT | LONGTEXT | NULL | - | 文章内容（Markdown格式） |
| COVER_IMAGE | VARCHAR(255) | NULL | - | 封面图片URL |
| CATEGORY_ID | VARCHAR(32) | NOT NULL | - | 分类ID |
| AUTHOR_ID | VARCHAR(32) | NOT NULL | - | 作者ID |
| STATUS | VARCHAR(20) | NULL | DRAFT | 状态：DRAFT-草稿, PUBLISHED-已发布, SCHEDULED-待发布 |
| VIEW_COUNT | INT | NULL | 0 | 阅读量 |
| LIKE_COUNT | INT | NULL | 0 | 点赞数 |
| COMMENT_COUNT | INT | NULL | 0 | 评论数 |
| IS_TOP | TINYINT | NULL | 0 | 是否置顶：0-否, 1-是 |
| IS_RECOMMEND | TINYINT | NULL | 0 | 是否推荐：0-否, 1-是 |
| ALLOW_COMMENT | TINYINT | NULL | 1 | 允许评论：0-否, 1-是 |
| PUBLISH_TIME | DATETIME | NULL | - | 发布时间 |
| SCHEDULED_PUBLISH_TIME | DATETIME | NULL | - | 计划发布时间（定时发布） |
| SEO_KEYWORDS | VARCHAR(255) | NULL | - | SEO关键词 |
| SEO_DESCRIPTION | VARCHAR(500) | NULL | - | SEO描述 |
| SORT_CODE | INT | NULL | 0 | 排序码 |
| EXT_JSON | TEXT | NULL | - | 扩展信息JSON |
| DELETE_FLAG | VARCHAR(255) | NULL | NOT_DELETE | 删除标志 |
| CREATE_TIME | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| CREATE_USER | VARCHAR(32) | NULL | - | 创建人ID |
| UPDATE_TIME | DATETIME | NULL | ON UPDATE | 更新时间 |
| UPDATE_USER | VARCHAR(32) | NULL | - | 更新人ID |

**索引设计**:
- `PK_BMS_ARTICLE`: 主键索引 (ID)
- `IDX_ARTICLE_CATEGORY`: 分类索引 (CATEGORY_ID)
- `IDX_ARTICLE_AUTHOR`: 作者索引 (AUTHOR_ID)
- `IDX_ARTICLE_STATUS`: 状态索引 (STATUS)
- `IDX_ARTICLE_PUBLISH_TIME`: 发布时间索引 (PUBLISH_TIME)
- `IDX_ARTICLE_TOP`: 置顶索引 (IS_TOP, PUBLISH_TIME)
- `IDX_ARTICLE_RECOMMEND`: 推荐索引 (IS_RECOMMEND, PUBLISH_TIME)
- `IDX_ARTICLE_SCHEDULED_PUBLISH`: 定时发布索引 (STATUS, SCHEDULED_PUBLISH_TIME)
- `FT_ARTICLE_TITLE_CONTENT`: 全文索引 (TITLE, CONTENT) - ngram解析器
- `FT_ARTICLE_SUMMARY`: 全文索引 (SUMMARY) - ngram解析器

---

### 4.2 文章版本历史表 (BMS_ARTICLE_VERSION)

**表说明**: 存储文章的历史版本，支持版本对比和回滚。

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| ID | VARCHAR(32) | NOT NULL | - | 主键ID |
| ARTICLE_ID | VARCHAR(32) | NOT NULL | - | 文章ID |
| VERSION_NUMBER | INT | NOT NULL | - | 版本号 |
| TITLE | VARCHAR(200) | NOT NULL | - | 文章标题快照 |
| CONTENT | LONGTEXT | NULL | - | 文章内容快照 |
| SUMMARY | VARCHAR(500) | NULL | - | 文章摘要快照 |
| CHANGE_SUMMARY | VARCHAR(500) | NULL | - | 变更摘要 |
| STATUS | VARCHAR(20) | NULL | ACTIVE | 状态：ACTIVE-活跃, ARCHIVED-已归档 |
| DELETE_FLAG | VARCHAR(255) | NULL | NOT_DELETE | 删除标志 |
| CREATE_TIME | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| CREATE_USER | VARCHAR(32) | NULL | - | 创建人ID |

**索引设计**:
- `PK_BMS_ARTICLE_VERSION`: 主键索引 (ID)
- `IDX_VERSION_ARTICLE`: 文章索引 (ARTICLE_ID)
- `IDX_VERSION_NUMBER`: 版本号索引 (ARTICLE_ID, VERSION_NUMBER)
- `UK_ARTICLE_VERSION`: 唯一索引 (ARTICLE_ID, VERSION_NUMBER)

---

### 4.3 文章分类表 (BMS_CATEGORY)

**表说明**: 存储文章分类信息，支持树形结构。

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| ID | VARCHAR(32) | NOT NULL | - | 主键ID |
| PARENT_ID | VARCHAR(32) | NULL | 0 | 父分类ID |
| NAME | VARCHAR(50) | NOT NULL | - | 分类名称 |
| CODE | VARCHAR(50) | NULL | - | 分类编码 |
| DESCRIPTION | VARCHAR(255) | NULL | - | 分类描述 |
| ICON | VARCHAR(100) | NULL | - | 分类图标 |
| SORT_CODE | INT | NULL | 0 | 排序码 |
| STATUS | VARCHAR(20) | NULL | ENABLE | 状态：ENABLE-启用, DISABLED-禁用 |
| EXT_JSON | TEXT | NULL | - | 扩展信息JSON |
| DELETE_FLAG | VARCHAR(255) | NULL | NOT_DELETE | 删除标志 |
| CREATE_TIME | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| CREATE_USER | VARCHAR(32) | NULL | - | 创建人ID |
| UPDATE_TIME | DATETIME | NULL | ON UPDATE | 更新时间 |
| UPDATE_USER | VARCHAR(32) | NULL | - | 更新人ID |

**索引设计**:
- `PK_BMS_CATEGORY`: 主键索引 (ID)
- `IDX_CATEGORY_PARENT`: 父分类索引 (PARENT_ID)
- `IDX_CATEGORY_STATUS`: 状态索引 (STATUS)
- `UK_CATEGORY_CODE`: 唯一索引 (CODE)

---

### 4.4 文章标签表 (BMS_TAG)

**表说明**: 存储文章标签信息。

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| ID | VARCHAR(32) | NOT NULL | - | 主键ID |
| NAME | VARCHAR(50) | NOT NULL | - | 标签名称 |
| CODE | VARCHAR(50) | NULL | - | 标签编码 |
| COLOR | VARCHAR(20) | NULL | - | 标签颜色 |
| DESCRIPTION | VARCHAR(255) | NULL | - | 标签描述 |
| SORT_CODE | INT | NULL | 0 | 排序码 |
| STATUS | VARCHAR(20) | NULL | ENABLE | 状态：ENABLE-启用, DISABLED-禁用 |
| EXT_JSON | TEXT | NULL | - | 扩展信息JSON |
| DELETE_FLAG | VARCHAR(255) | NULL | NOT_DELETE | 删除标志 |
| CREATE_TIME | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| CREATE_USER | VARCHAR(32) | NULL | - | 创建人ID |
| UPDATE_TIME | DATETIME | NULL | ON UPDATE | 更新时间 |
| UPDATE_USER | VARCHAR(32) | NULL | - | 更新人ID |

**索引设计**:
- `PK_BMS_TAG`: 主键索引 (ID)
- `UK_TAG_CODE`: 唯一索引 (CODE)
- `IDX_TAG_STATUS`: 状态索引 (STATUS)

---

### 4.5 文章标签关联表 (BMS_ARTICLE_TAG)

**表说明**: 文章与标签的多对多关联表。

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| ID | VARCHAR(32) | NOT NULL | - | 主键ID |
| ARTICLE_ID | VARCHAR(32) | NOT NULL | - | 文章ID |
| TAG_ID | VARCHAR(32) | NOT NULL | - | 标签ID |
| CREATE_TIME | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

**索引设计**:
- `PK_BMS_ARTICLE_TAG`: 主键索引 (ID)
- `UK_ARTICLE_TAG`: 唯一索引 (ARTICLE_ID, TAG_ID)
- `IDX_ARTICLE_TAG_ARTICLE`: 文章索引 (ARTICLE_ID)
- `IDX_ARTICLE_TAG_TAG`: 标签索引 (TAG_ID)

---

### 4.6 评论表 (BMS_COMMENT)

**表说明**: 存储文章评论，支持嵌套回复。

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| ID | VARCHAR(32) | NOT NULL | - | 主键ID |
| ARTICLE_ID | VARCHAR(32) | NOT NULL | - | 文章ID |
| PARENT_ID | VARCHAR(32) | NULL | 0 | 父评论ID（0为顶级评论） |
| REPLY_USER_ID | VARCHAR(32) | NULL | - | 回复用户ID |
| REPLY_USER_NAME | VARCHAR(50) | NULL | - | 回复用户昵称 |
| NICKNAME | VARCHAR(50) | NOT NULL | - | 评论者昵称 |
| EMAIL | VARCHAR(100) | NULL | - | 评论者邮箱 |
| WEBSITE | VARCHAR(255) | NULL | - | 评论者网站 |
| CONTENT | VARCHAR(1000) | NOT NULL | - | 评论内容 |
| STATUS | VARCHAR(20) | NULL | PENDING | 状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝 |
| IP_ADDRESS | VARCHAR(50) | NULL | - | IP地址 |
| USER_AGENT | VARCHAR(500) | NULL | - | 用户代理 |
| LIKE_COUNT | INT | NULL | 0 | 点赞数 |
| EXT_JSON | TEXT | NULL | - | 扩展信息JSON |
| DELETE_FLAG | VARCHAR(255) | NULL | NOT_DELETE | 删除标志 |
| CREATE_TIME | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| CREATE_USER | VARCHAR(32) | NULL | - | 创建人ID |
| UPDATE_TIME | DATETIME | NULL | ON UPDATE | 更新时间 |
| UPDATE_USER | VARCHAR(32) | NULL | - | 更新人ID |

**索引设计**:
- `PK_BMS_COMMENT`: 主键索引 (ID)
- `IDX_COMMENT_ARTICLE`: 文章索引 (ARTICLE_ID)
- `IDX_COMMENT_PARENT`: 父评论索引 (PARENT_ID)
- `IDX_COMMENT_STATUS`: 状态索引 (STATUS)
- `IDX_COMMENT_CREATE_TIME`: 创建时间索引 (CREATE_TIME)

---

### 4.7 媒体资源表 (BMS_MEDIA)

**表说明**: 存储上传的媒体文件信息。

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| ID | VARCHAR(32) | NOT NULL | - | 主键ID |
| FILE_NAME | VARCHAR(255) | NOT NULL | - | 文件名称 |
| ORIGINAL_NAME | VARCHAR(255) | NULL | - | 原始文件名 |
| FILE_PATH | VARCHAR(500) | NOT NULL | - | 文件路径 |
| FILE_URL | VARCHAR(500) | NULL | - | 文件访问URL |
| FILE_SIZE | BIGINT | NULL | 0 | 文件大小（字节） |
| FILE_TYPE | VARCHAR(50) | NULL | - | 文件类型：IMAGE/VIDEO/AUDIO/DOCUMENT/OTHER |
| MIME_TYPE | VARCHAR(100) | NULL | - | MIME类型 |
| FILE_EXT | VARCHAR(20) | NULL | - | 文件扩展名 |
| THUMBNAIL_URL | VARCHAR(500) | NULL | - | 缩略图URL |
| WIDTH | INT | NULL | - | 图片宽度 |
| HEIGHT | INT | NULL | - | 图片高度 |
| DURATION | INT | NULL | - | 音视频时长（秒） |
| UPLOAD_USER | VARCHAR(32) | NULL | - | 上传用户ID |
| DOWNLOAD_COUNT | INT | NULL | 0 | 下载次数 |
| STATUS | VARCHAR(20) | NULL | ENABLE | 状态：ENABLE-启用, DISABLED-禁用 |
| EXT_JSON | TEXT | NULL | - | 扩展信息JSON |
| DELETE_FLAG | VARCHAR(255) | NULL | NOT_DELETE | 删除标志 |
| CREATE_TIME | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| CREATE_USER | VARCHAR(32) | NULL | - | 创建人ID |
| UPDATE_TIME | DATETIME | NULL | ON UPDATE | 更新时间 |
| UPDATE_USER | VARCHAR(32) | NULL | - | 更新人ID |

**索引设计**:
- `PK_BMS_MEDIA`: 主键索引 (ID)
- `IDX_MEDIA_TYPE`: 类型索引 (FILE_TYPE)
- `IDX_MEDIA_UPLOAD_USER`: 上传用户索引 (UPLOAD_USER)
- `IDX_MEDIA_CREATE_TIME`: 创建时间索引 (CREATE_TIME)

---

## 5. 系统基础表设计

### 5.1 系统用户表 (SYS_USER)

**表说明**: 存储B端（管理端）用户信息。

| 主要字段 | 类型 | 说明 |
|----------|------|------|
| ID | VARCHAR(20) | 主键ID |
| ACCOUNT | VARCHAR(255) | 账号 |
| PASSWORD | VARCHAR(255) | 密码（加密存储） |
| NAME | VARCHAR(255) | 姓名 |
| NICKNAME | VARCHAR(255) | 昵称 |
| PHONE | VARCHAR(255) | 手机号（SM4加密） |
| EMAIL | VARCHAR(255) | 邮箱 |
| ORG_ID | VARCHAR(20) | 组织ID |
| POSITION_ID | VARCHAR(20) | 职位ID |
| USER_STATUS | VARCHAR(255) | 用户状态 |
| ... | ... | 其他个人信息字段 |

### 5.2 系统资源表 (SYS_RESOURCE)

**表说明**: 存储菜单、按钮等资源信息，支持树形结构。

| 主要字段 | 类型 | 说明 |
|----------|------|------|
| ID | VARCHAR(20) | 主键ID |
| PARENT_ID | VARCHAR(20) | 父级ID |
| TITLE | VARCHAR(255) | 资源标题 |
| NAME | VARCHAR(255) | 资源名称 |
| CODE | VARCHAR(255) | 资源编码 |
| CATEGORY | VARCHAR(255) | 分类：MENU/BUTTON |
| MENU_TYPE | VARCHAR(255) | 菜单类型：MENU/CATALOG/IFRAME/LINK |
| PATH | VARCHAR(255) | 路由路径 |
| COMPONENT | VARCHAR(255) | 组件路径 |
| MODULE | VARCHAR(20) | 所属模块 |
| SORT_CODE | INT | 排序码 |
| VISIBLE | VARCHAR(10) | 是否可见 |

### 5.3 系统关系表 (SYS_RELATION)

**表说明**: 存储各种关联关系（角色-资源、角色-权限等）。

| 主要字段 | 类型 | 说明 |
|----------|------|------|
| ID | VARCHAR(20) | 主键ID |
| OBJECT_ID | VARCHAR(255) | 对象ID |
| TARGET_ID | VARCHAR(255) | 目标ID |
| CATEGORY | VARCHAR(255) | 分类：SYS_ROLE_HAS_RESOURCE/SYS_ROLE_HAS_PERMISSION |
| EXT_JSON | LONGTEXT | 扩展信息JSON |

### 5.4 开发配置表 (DEV_CONFIG)

**表说明**: 存储系统配置项。

| 主要字段 | 类型 | 说明 |
|----------|------|------|
| ID | VARCHAR(20) | 主键ID |
| CONFIG_KEY | VARCHAR(255) | 配置键 |
| CONFIG_VALUE | LONGTEXT | 配置值 |
| CATEGORY | VARCHAR(255) | 分类 |
| REMARK | VARCHAR(255) | 备注 |
| SORT_CODE | INT | 排序码 |

### 5.5 开发字典表 (DEV_DICT)

**表说明**: 存储系统字典数据，支持树形结构。

| 主要字段 | 类型 | 说明 |
|----------|------|------|
| ID | VARCHAR(20) | 主键ID |
| PARENT_ID | VARCHAR(20) | 父级ID |
| DICT_LABEL | VARCHAR(255) | 字典文字 |
| DICT_VALUE | VARCHAR(255) | 字典值 |
| DICT_COLOR | VARCHAR(255) | 字典颜色 |
| CODE | VARCHAR(255) | 编码 |
| CATEGORY | VARCHAR(255) | 分类：FRM/BIZ |

---

## 6. 索引设计

### 6.1 索引策略

1. **主键索引**: 所有表均使用 ID 作为主键
2. **外键索引**: 所有外键字段建立普通索引
3. **状态索引**: 状态字段建立索引，优化查询过滤
4. **时间索引**: 时间字段建立索引，支持时间范围查询
5. **全文索引**: 文章标题、内容、摘要建立全文索引，支持中文分词

### 6.2 全文索引配置

```sql
-- 文章全文索引（使用 ngram 解析器支持中文）
ALTER TABLE BMS_ARTICLE
ADD FULLTEXT INDEX FT_ARTICLE_TITLE_CONTENT (TITLE, CONTENT)
WITH PARSER ngram;

ALTER TABLE BMS_ARTICLE
ADD FULLTEXT INDEX FT_ARTICLE_SUMMARY (SUMMARY)
WITH PARSER ngram;
```

### 6.3 全文搜索示例

```sql
-- 自然语言搜索
SELECT * FROM BMS_ARTICLE
WHERE MATCH(TITLE, CONTENT) AGAINST('搜索关键词' IN NATURAL LANGUAGE MODE);

-- 布尔模式搜索（支持 +必须包含 -排除 *通配符）
SELECT * FROM BMS_ARTICLE
WHERE MATCH(TITLE, CONTENT) AGAINST('+Java +Spring' IN BOOLEAN MODE);

-- 查询扩展搜索
SELECT * FROM BMS_ARTICLE
WHERE MATCH(TITLE, CONTENT) AGAINST('博客' WITH QUERY EXPANSION);
```

---

## 7. 数据字典

### 7.1 文章状态 (ARTICLE_STATUS)

| 值 | 名称 | 说明 |
|----|------|------|
| DRAFT | 草稿 | 文章未发布 |
| PUBLISHED | 已发布 | 文章已公开发布 |
| SCHEDULED | 待发布 | 定时发布中 |
| WITHDRAWN | 已撤回 | 文章已撤回 |

### 7.2 通用状态 (COMMON_STATUS)

| 值 | 名称 | 说明 |
|----|------|------|
| ENABLE | 启用 | 正常可用 |
| DISABLED | 禁用 | 已禁用 |

### 7.3 评论状态 (COMMENT_STATUS)

| 值 | 名称 | 说明 |
|----|------|------|
| PENDING | 待审核 | 新评论待审核 |
| APPROVED | 已通过 | 审核通过 |
| REJECTED | 已拒绝 | 审核拒绝 |

### 7.4 删除标志 (DELETE_FLAG)

| 值 | 名称 | 说明 |
|----|------|------|
| NOT_DELETE | 未删除 | 正常记录 |
| DELETED | 已删除 | 已逻辑删除 |

---

## 8. 安全设计

### 8.1 敏感字段加密

以下字段采用 SM4 国密算法加密存储：

- 用户密码 (PASSWORD)
- 手机号码 (PHONE)
- 证件号码 (ID_CARD_NUMBER)
- 紧急联系人电话 (EMERGENCY_PHONE)

### 8.2 密码策略

| 策略 | 配置 |
|------|------|
| 默认密码 | 123456 |
| 最小长度 | 6位 |
| 最大长度 | 20位 |
| 复杂度 | 可配置 REG0-REG5 |
| 有效期 | 30天 |
| 历史密码限制 | 禁止使用最近3次密码 |
| 弱密码检测 | 启用 |

### 8.3 登录安全

| 策略 | 配置 |
|------|------|
| 连续失败次数 | 5次 |
| 失败统计时长 | 5分钟 |
| 锁定时长 | 5分钟 |
| 验证码开关 | 可配置 |

---

## 附录

### A. 表清单汇总

| 序号 | 表名 | 说明 | 所属模块 |
|------|------|------|----------|
| 1 | BMS_ARTICLE | 文章表 | BMS核心 |
| 2 | BMS_ARTICLE_VERSION | 文章版本表 | BMS核心 |
| 3 | BMS_ARTICLE_TAG | 文章标签关联表 | BMS核心 |
| 4 | BMS_CATEGORY | 分类表 | BMS核心 |
| 5 | BMS_TAG | 标签表 | BMS核心 |
| 6 | BMS_COMMENT | 评论表 | BMS核心 |
| 7 | BMS_MEDIA | 媒体资源表 | BMS核心 |
| 8 | SYS_USER | 系统用户表 | 系统管理 |
| 9 | SYS_USER_EXT | 用户扩展表 | 系统管理 |
| 10 | SYS_USER_PASSWORD | 用户密码表 | 系统管理 |
| 11 | SYS_ORG | 组织表 | 系统管理 |
| 12 | SYS_POSITION | 职位表 | 系统管理 |
| 13 | SYS_ROLE | 角色表 | 系统管理 |
| 14 | SYS_RESOURCE | 资源表 | 系统管理 |
| 15 | SYS_RELATION | 关系表 | 系统管理 |
| 16 | DEV_CONFIG | 配置表 | 开发工具 |
| 17 | DEV_DICT | 字典表 | 开发工具 |
| 18 | DEV_FILE | 文件表 | 开发工具 |
| 19 | DEV_LOG | 日志表 | 开发工具 |
| 20 | DEV_JOB | 定时任务表 | 开发工具 |
| 21 | CLIENT_USER | C端用户表 | 客户端 |
| 22 | AUTH_THIRD_USER | 三方用户表 | 认证 |

### B. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| V1.0.0 | 2026-03-24 | 初始版本，核心表设计 | BMS团队 |
| V2.0.0 | 2026-03-26 | 新增版本历史、定时发布、全文搜索功能 | BMS团队 |

---

**文档结束**