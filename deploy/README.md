# 云朵教务社区版 · 容器化部署（deploy/）

一套 `docker compose` 起全部依赖：MySQL 8 + Redis 7 + Spring Boot 后端 + Nginx 托管的前端。
**只需要 4 个必填变量**，不需要微信 / 支付 / 短信 / 地图 / 对象存储 / 监控平台的任何凭据。

## 1. 快速开始

```bash
cd community-edition

# 1) 准备环境变量（.env 已被 .gitignore 忽略，不会进仓库）
cp deploy/.env.example deploy/.env
${EDITOR:-vi} deploy/.env      # 至少改：MYSQL_ROOT_PASSWORD / MYSQL_PASSWORD /
                               #        REDIS_PASSWORD / TOKEN_SECRET / INITIAL_ADMIN_PASSWORD

# 2) 起服务（首次会构建两个镜像）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build

# 3) 看状态（四个服务都应是 healthy）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml ps
```

打开 `http://<宿主机>:${HTTP_PORT:-8080}`，用 `admin` + 你在 `INITIAL_ADMIN_PASSWORD` 里设的口令登录，
首次登录会要求改密。

### 带演示数据启动（春芽艺术工作室）

```bash
docker compose --env-file deploy/.env \
  -f deploy/docker-compose.yml -f deploy/docker-compose.demo.yml up -d --build
```

初始化 SQL **只在 MySQL 数据卷为空的首次启动时执行**；想重新导入请先删卷（见 §5）。

`003_demo_data.sql` 只写数据、不写口令：要能用演示账号（教务主管 / 授课教师）登录，执行

```bash
docker compose ... exec backend sh -c 'echo 略'   # 后端容器里没有 python，故在宿主机执行：
set -a && source scripts/env.local && set +a      # 指向同一个库
export DEMO_PASSWORD='<你自己定的演示口令>'
python scripts/set-demo-passwords.py
```

> 演示账号的用户名就是手机号占位号段：`19900000001`（教务主管）、`19900000002`（授课教师）等，
> 见 `database/003_demo_data.sql` 文件头。

## 2. 这一套里有什么

| 服务 | 镜像 | 对外端口 | 说明 |
|---|---|---|---|
| `admin-web` | 构建（`Dockerfile.admin-web`：Node 构建 → Nginx 托管） | **`${HTTP_PORT:-8080}` → 80** | 唯一对外发布的端口 |
| `backend` | 构建（`Dockerfile.backend`：Maven 构建 → JRE 8 运行） | 仅容器网络内 `8080` | 非 root 用户运行 |
| `mysql` | `mysql:8.0` | **不发布**（仅 `expose`） | 命名卷 `yunduo-community-mysql-data` |
| `redis` | `redis:7-alpine` | **不发布**（仅 `expose`） | 命名卷 `yunduo-community-redis-data`，开 AOF |

- **Nginx 只做两件事**：托管前端静态资源、把 `/api/` 反代到后端（`deploy/nginx/default.conf`）。
  数据库、Redis、后端的其他路径都不经过 Nginx。
- **上传文件**落在命名卷 `yunduo-community-uploads`（容器内 `/data/uploads`）。
- **健康检查**：四个服务都有 `healthcheck`；后端等待 MySQL 与 Redis `service_healthy` 后才启动，
  前端等后端 healthy 后才启动。
- **初始化顺序**：`/docker-entrypoint-initdb.d/` 里按文件名前缀顺序执行
  `01-001-schema.sql` → `02-002-seed.sql`（→ 可选 `03-003-demo-data.sql`），
  由 MySQL 官方镜像保证「只在空数据卷执行一次」。

## 3. 部署验证范围

Compose 编排已完成离线结构与配置检查，真实 Docker 容器端到端验证尚未完成。请在自己的测试环境核对容器健康状态、登录、上传和业务流程后再安排上线。

## 4. 常用操作

```bash
D="docker compose --env-file deploy/.env -f deploy/docker-compose.yml"

$D logs -f backend          # 后端日志
$D restart backend          # 重启后端（不会重跑初始化 SQL）
$D down                     # 停服务，保留数据卷
$D exec mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e 'SHOW DATABASES'
```

## 5. 清理（注意只动本项目的卷）

数据卷名固定带 `yunduo-community-` 前缀，删除前先确认：

```bash
docker volume ls | grep yunduo-community          # 先看清楚
docker compose ... down -v                        # 只删本项目的卷
```

> **不要**执行 `docker volume prune -a` / `docker system prune -a --volumes` 这类全局清理。

## 6. 文件说明

```
deploy/
├── docker-compose.yml          基础编排（mysql / redis / backend / admin-web）
├── docker-compose.demo.yml     叠加演示数据（追加 003 的挂载）
├── .env.example                环境变量模板（只有变量名与虚构值）
├── Dockerfile.backend          后端镜像（多阶段：Maven 构建 → JRE 运行）
├── Dockerfile.admin-web        前端镜像（多阶段：Node 构建 → Nginx 托管）
├── nginx/default.conf          站点配置：静态资源 + /api/ 反代
└── README.md                   本文件
../.dockerignore                构建上下文忽略（node_modules / dist / target / 运行期产物）
```
