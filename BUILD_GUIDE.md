# BMS v1.0.0 构建指南

## 构建物状态

| 组件 | 状态 | 文件位置 |
|------|------|----------|
| 后端 JAR | ✅ 已构建 | `bms-web-app/target/bms-web-app-3.0.0.jar` (148MB) |
| 前端 Dist | ⏳ 待构建 | `bms-admin-web/dist/` |

---

## 后端构建

### 已构建产物

```bash
bms-web-app/target/bms-web-app-3.0.0.jar
```

### 重新构建命令

```bash
mvn clean install -DskipTests
```

### 启动后端

```bash
java -jar bms-web-app/target/bms-web-app-3.0.0.jar
```

---

## 前端构建

### 构建命令

```bash
cd bms-admin-web
npm install
npm run build
```

### 构建产物

```bash
bms-admin-web/dist/
```

### 开发模式启动

```bash
cd bms-admin-web
npm run dev
```

---

## 发布包内容

### 建议打包结构

```
bms-v1.0.0-release/
├── bms-web-app-3.0.0.jar          # 后端可执行JAR
├── bms-admin-web-dist.zip         # 前端构建产物
├── sql/
│   ├── bms_init.sql               # 数据库初始化
│   ├── bms_menu_permission.sql    # 菜单权限
│   └── bms_test_data.sql          # 测试数据
├── docs/
│   ├── RELEASE_NOTES.md           # 发布说明
│   ├── 部署手册.md                 # 部署文档
│   └── 用户手册.md                 # 用户手册
└── README.md                      # 项目说明
```

---

## 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 后端运行环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存服务 |
| Nginx | 1.20+ | 前端部署（生产环境） |

---

## 部署步骤

1. **数据库初始化**
   ```bash
   mysql -u root -p < sql/bms_init.sql
   mysql -u root -p bms < sql/bms_menu_permission.sql
   ```

2. **配置修改**
   - 修改 `application.properties` 中的数据库连接信息
   - 修改 `application.properties` 中的 Redis 连接信息

3. **启动后端**
   ```bash
   java -jar bms-web-app-3.0.0.jar
   ```

4. **部署前端**
   - 开发环境: `npm run dev`
   - 生产环境: 使用 Nginx 托管 `dist/` 目录

---

## 访问信息

| 服务 | 地址 |
|------|------|
| 前端（开发） | http://localhost:81 |
| 后端 API | http://localhost:82 |
| API 文档 | http://localhost:82/doc.html |
| 默认账号 | superAdmin / 123456 |