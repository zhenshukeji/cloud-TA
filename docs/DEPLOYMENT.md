# 部署文档（Deployment Guide）

> 源码与 Docker 部署说明。备份、恢复与升级见 [OPERATIONS.md](OPERATIONS.md)，产品介绍见根 [README](../README.md)。

## 1. 环境要求

| 组件 | 版本 | 说明 |
|---|---|---|
| JDK | 8（后端按 JDK 8 编译运行） | 前端构建不需要 JDK |
| Maven | 3.6+ | 仅源码构建后端时需要；`-o` 离线模式要求本机已有依赖缓存，首次构建需联网 |
| Node.js | 22 或 24（其他 16+ 版本未测） | 构建/开发前端；Node ≥17 需 `NODE_OPTIONS=--openssl-legacy-provider`（旧版 webpack 的哈希算法），生产构建已验证 |
| MySQL | 8.0+（需窗口函数与 STORED 生成列） | 数据库名必须以 `yunduo_community` 开头 |
| Redis | 5.x+ | 建议使用独立逻辑库（默认 12）与独立实例 |

端口约定：后端默认 `8080`（环境变量 `SERVER_PORT` 可改）；前端为静态产物，由 Nginx 或任意静态服务器托管，`/api/` 反代到后端。

## 2. 部署前必读

1. 使用社区版专用数据库账号；将数据库、Redis 口令及 `TOKEN_SECRET`、`INITIAL_ADMIN_PASSWORD` 全部替换为自己的值，勿使用模板占位值。
2. 数据库初始化顺序固定：`database/001_schema.sql` → `database/002_seed_system.sql`（→ 可选 `003_demo_data.sql`，**生产环境不要导入**）。
3. 后端启动守卫（`CommunityStartupGuard`）会硬校验必需环境变量与 `TOKEN_SECRET` 强度，缺项或弱密钥会**拒绝启动**——这是设计行为，不是故障。
4. `INITIAL_ADMIN_PASSWORD` 只在「库中 admin 尚无口令」的首次启动生效；之后修改口令走系统内「个人中心」。

### 必要配置检查

| 配置 | 要求 |
|---|---|
| `TOKEN_SECRET` | 用 `openssl rand -base64 48` 等方式生成随机密钥，至少 32 位且含至少 8 种字符；不要沿用模板值 |
| `INITIAL_ADMIN_PASSWORD` | 首次启动设置，至少 10 位且含至少两类字符；首次登录后修改，不得等于登录账号 |
| MySQL / Redis | 使用专用数据库和账号，配置正确地址与口令；数据库名以 `yunduo_community` 开头；Redis 采用独立实例或逻辑库及键前缀 |
| `UPLOAD_ROOT` | 使用可写绝对路径，生产环境持久化；文件与数据库分别备份 |
| 访问与密钥 | 不直接向公网暴露 MySQL、Redis 和管理诊断接口；不要将本地环境文件、口令或备份提交仓库 |
| 演示数据 | 仅在测试环境执行 `003_demo_data.sql`；真实业务库不要导入演示数据 |

短信、支付等第三方服务并非社区版启动条件。若自行加入访问统计，请明确告知收集内容并提供关闭方式；统计不应收集业务输入或师生个人资料。

## 3. 方式一：源码部署（推荐首次部署用）

### 3.1 后端

```bash
# 0) 准备数据库（库名前缀 yunduo_community）
mysql -h<host> -P<port> -u<user> -p \
  -e "CREATE DATABASE yunduo_community DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci"
mysql -h<host> -P<port> -u<user> -p yunduo_community < database/001_schema.sql
mysql -h<host> -P<port> -u<user> -p yunduo_community < database/002_seed_system.sql

# 1) 打包（首次需联网拉取 Maven 依赖）
cd backend && mvn clean package -DskipTests
# 产物：backend/target/yunduo-community-admin-api.jar

# 2) 配置环境变量（全部必需，缺一拒绝启动；生成方式见本页配置检查）
export MYSQL_HOST=127.0.0.1 MYSQL_PORT=3306
export MYSQL_DATABASE=yunduo_community
export MYSQL_USER=community MYSQL_PASSWORD='<你的数据库口令>'
export REDIS_HOST=127.0.0.1 REDIS_PORT=6379 REDIS_PASSWORD='<你的Redis口令>'
export JX_REDIS_DB=12 JX_CACHE_NAME=yunduo_community
export TOKEN_SECRET='<openssl rand -base64 48 生成>'
export INITIAL_ADMIN_PASSWORD='<首次启动用，openssl rand -base64 18 生成>'
export UPLOAD_ROOT=/data/community/uploads   # 上传/导出持久目录，务必用绝对路径
export SERVER_PORT=8080

# 3) 启动
java -jar target/yunduo-community-admin-api.jar
# 日志出现 Started Application 且无 ERROR 即成功；首次启动会自动写入 admin 口令哈希
```

### 3.2 前端

```bash
cd admin-web
npm ci                     # 依赖由 package-lock.json 锁定；legacy-peer-deps 已由 .npmrc 固化
export NODE_OPTIONS=--openssl-legacy-provider
npm run build:prod         # 产物：admin-web/dist/
```

### 3.3 Nginx 托管与反代

参照 `deploy/nginx/default.conf`：静态根指向 `dist/`，关键规则是 `^~ /api/` 反代到后端并去掉 `/api` 前缀（`^~` 不可省，否则静态资源正则会抢走 API 请求造成偶发 404）。`/profile/` 需指向后端的上传目录或单独托管。

## 4. 方式二：Docker Compose（编排文件已提供，容器端到端验证进行中）

```bash
cd deploy
cp .env.example .env       # 按注释填写（数据库口令、TOKEN_SECRET 等）
docker compose --env-file .env -f docker-compose.yml up -d
docker compose -f docker-compose.yml ps        # 四个服务应全部 healthy
# 演示数据（可选）：docker compose --env-file .env -f docker-compose.yml \
#   -f docker-compose.demo.yml up -d
```

> Docker 容器端到端验证尚未完成；编排说明与验证范围见 [deploy/README.md](../deploy/README.md)。

## 5. 已有库升级

从旧版本升级时按顺序执行 `database/migrations/` 下的迁移脚本（当前仅 `001_course_time_account_unique.sql`：为课时账户表补「同学员同课程唯一键」，防止并发报读产生双账户）。脚本头部有幂等口径与前置自查 SQL；迁移前先做备份（`scripts/backup-db.py`），**不要用初始化脚本替代迁移，也不要重跑 002 覆盖已有口令**。详见 [运维手册](OPERATIONS.md)。

## 6. 部署后验证

```bash
# 登录冒烟：admin + 你设置的 INITIAL_ADMIN_PASSWORD 登录成功
# 并依次确认：首页数据加载 → 新增教师/课程/班级/学员 → 报读 → 排课 → 点名扣课时余额递减
```

## 7. 常见问题

| 现象 | 原因与处理 |
|---|---|
| 启动即退出，日志出现「缺少环境变量/令牌强度不足」 | 启动守卫按设计拒绝启动；对照本页必要配置检查补齐后重试 |
| 登录页能开但登录报 500 | 数据库/Redis 未连通或口令错误；检查环境变量与网络 |
| 首次启动后 admin 登录失败 | `INITIAL_ADMIN_PASSWORD` 只在首次生效；如错过，用运维方式重置（见 OPERATIONS.md） |
| 前端构建报 OpenSSL 错误 | 未设置 `NODE_OPTIONS=--openssl-legacy-provider`（Node ≥17） |
| 上传文件 500 | `UPLOAD_ROOT` 指向的目录不可写；使用绝对路径并确认权限 |
