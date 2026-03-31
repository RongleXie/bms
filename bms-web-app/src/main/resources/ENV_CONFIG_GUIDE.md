# BMS 环境变量配置指南

## 安全配置说明

为防止敏感配置信息泄露，BMS系统已将以下敏感配置改为环境变量方式：

| 配置项 | 环境变量 | 默认值 | 说明 |
|--------|----------|--------|------|
| 数据库密码 | `DB_PASSWORD` | 空 | MySQL数据库连接密码 |
| 数据库用户名 | `DB_USERNAME` | root | MySQL数据库用户名 |
| Redis密码 | `REDIS_PASSWORD` | 空 | Redis连接密码 |
| Redis主机 | `REDIS_HOST` | 127.0.0.1 | Redis服务器地址 |
| Redis端口 | `REDIS_PORT` | 6379 | Redis服务器端口 |
| Druid监控密码 | `DRUID_MONITOR_PASSWORD` | 空 | Druid监控页面密码 |
| Druid监控用户名 | `DRUID_MONITOR_USERNAME` | admin | Druid监控页面用户名 |
| Knife4j密码 | `KNIFE4J_PASSWORD` | 空 | API文档访问密码 |
| Knife4j用户名 | `KNIFE4J_USERNAME` | admin | API文档访问用户名 |

## 配置方式

### Windows 系统

#### 方式一：临时环境变量（命令行）

```powershell
# 设置数据库密码
$env:DB_PASSWORD="your_secure_password"

# 设置Redis密码
$env:REDIS_PASSWORD="your_redis_password"

# 设置其他敏感配置
$env:DRUID_MONITOR_PASSWORD="monitor_password"
$env:KNIFE4J_PASSWORD="doc_password"

# 启动应用
java -jar bms-web-app.jar
```

#### 方式二：永久环境变量（系统设置）

1. 打开"系统属性" → "高级" → "环境变量"
2. 在"系统变量"或"用户变量"中添加：
   - 变量名：`DB_PASSWORD`
   - 变量值：`your_secure_password`

### Linux/Mac 系统

#### 方式一：临时环境变量

```bash
# 设置环境变量
export DB_PASSWORD="your_secure_password"
export REDIS_PASSWORD="your_redis_password"
export DRUID_MONITOR_PASSWORD="monitor_password"
export KNIFE4J_PASSWORD="doc_password"

# 启动应用
java -jar bms-web-app.jar
```

#### 方式二：永久环境变量（~/.bashrc 或 ~/.zshrc）

```bash
# 编辑配置文件
vim ~/.bashrc

# 添加环境变量
export DB_PASSWORD="your_secure_password"
export REDIS_PASSWORD="your_redis_password"
export DRUID_MONITOR_PASSWORD="monitor_password"
export KNIFE4J_PASSWORD="doc_password"

# 使配置生效
source ~/.bashrc
```

#### 方式三：systemd 服务配置

```ini
# /etc/systemd/system/bms.service
[Unit]
Description=BMS Application
After=network.target

[Service]
Type=simple
User=bms
Environment="DB_PASSWORD=your_secure_password"
Environment="REDIS_PASSWORD=your_redis_password"
Environment="DRUID_MONITOR_PASSWORD=monitor_password"
Environment="KNIFE4J_PASSWORD=doc_password"
ExecStart=/usr/bin/java -jar /opt/bms/bms-web-app.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

### Docker 容器部署

#### 方式一：环境变量传递

```bash
docker run -d \
  --name bms \
  -e DB_PASSWORD="your_secure_password" \
  -e REDIS_PASSWORD="your_redis_password" \
  -e DRUID_MONITOR_PASSWORD="monitor_password" \
  -e KNIFE4J_PASSWORD="doc_password" \
  -p 82:82 \
  bms:latest
```

#### 方式二：使用 .env 文件

```bash
# 创建 .env 文件
cat > .env << EOF
DB_PASSWORD=your_secure_password
REDIS_PASSWORD=your_redis_password
DRUID_MONITOR_PASSWORD=monitor_password
KNIFE4J_PASSWORD=doc_password
EOF

# 启动容器
docker run -d --name bms --env-file .env -p 82:82 bms:latest
```

#### 方式三：docker-compose.yml

```yaml
version: '3.8'
services:
  bms:
    image: bms:latest
    ports:
      - "82:82"
    environment:
      - DB_PASSWORD=${DB_PASSWORD}
      - REDIS_PASSWORD=${REDIS_PASSWORD}
      - DRUID_MONITOR_PASSWORD=${DRUID_MONITOR_PASSWORD}
      - KNIFE4J_PASSWORD=${KNIFE4J_PASSWORD}
```

## 开发环境快速配置

创建本地开发环境配置脚本：

### Windows (setup-env.ps1)

```powershell
$env:DB_PASSWORD="123456"
$env:REDIS_PASSWORD="123456"
$env:DRUID_MONITOR_PASSWORD="123456"
$env:KNIFE4J_PASSWORD="123456"
Write-Host "环境变量已设置完成"
```

### Linux/Mac (setup-env.sh)

```bash
#!/bin/bash
export DB_PASSWORD="123456"
export REDIS_PASSWORD="123456"
export DRUID_MONITOR_PASSWORD="123456"
export KNIFE4J_PASSWORD="123456"
echo "环境变量已设置完成"
```

## 安全建议

1. **生产环境**：使用强密码，并定期更换
2. **禁止提交**：不要将 `.env` 文件或包含密码的脚本提交到版本控制
3. **权限控制**：限制环境变量的访问权限
4. **密钥管理**：考虑使用专业的密钥管理服务（如 Vault、AWS Secrets Manager）

## 日志脱敏说明

系统已自动对以下敏感字段进行日志脱敏处理：

| 字段类型 | 示例字段名 | 脱敏方式 |
|----------|-----------|----------|
| 密码类 | password, pwd | 完全隐藏（******） |
| 令牌类 | token, accessToken | 完全隐藏 |
| 密钥类 | secretKey, privateKey | 完全隐藏 |
| 手机号 | phone, mobile | 部分脱敏（138****1234） |
| 身份证 | idCard, idCardNo | 部分脱敏（310***********1234） |
| 邮箱 | email, mail | 部分脱敏（abc***@example.com） |

脱敏处理在以下场景自动生效：
- 操作日志记录
- 异常日志记录
- API请求参数记录

## 验证配置

启动应用后，可通过以下方式验证：

1. 检查日志是否包含明文敏感信息
2. 查看数据库连接是否正常
3. 测试Redis连接是否正常
4. 验证Druid监控和Knife4j文档访问是否需要密码