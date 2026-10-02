# 使用者配置更新清单（User Setup Checklist）

> 首次部署前**逐项核对**本清单。带 🔴 的项不替换会造成安全风险或启动失败；
> 带 🟡 的项不影响启动，但属于「拿默认值上线」的坏味道。
> 配置方法：环境变量（见 [DEPLOYMENT.md](DEPLOYMENT.md) §3.1）或 `scripts/env.local`（本地验证用，不会进仓库）。

## 1. 🔴 必须替换的默认值

| # | 配置项 | 模板默认值 | 为什么必须改 | 生成/检查方法 |
|---|---|---|---|---|
| 1 | `TOKEN_SECRET` | 形状合规哑值 | 签名登录令牌；用默认值等于让任何人可伪造会话 | `openssl rand -base64 48`；启动守卫强制 ≥32 位、≥8 种字符、非占位符 |
| 2 | `MYSQL_PASSWORD` | `change-me` | 数据库口令 | 建库时为社区版单独建账号并授权（不要用 root 跑生产） |
| 3 | `REDIS_PASSWORD` | `change-me` | Redis 口令 | `CONFIG SET requirepass` 或实例自带口令 |
| 4 | `INITIAL_ADMIN_PASSWORD` | `replace-me-...` | 初始 admin 口令；要求 ≥10 位、≥2 类字符、不得等于 `admin` | `openssl rand -base64 18`；**只在首次启动生效**，登录后请再改一次 |

## 2. 🔴 必须确认的运行项

| # | 配置项 | 说明 |
|---|---|---|
| 5 | `MYSQL_HOST/PORT/DATABASE/USER` | 库名必须以 `yunduo_community` 开头；生产建议专用账号最小授权 |
| 6 | `REDIS_HOST/PORT` + `JX_REDIS_DB` | 建议**独立实例**；共用实例时必须用独立逻辑库（默认 12），避免与其他系统互相清键 |
| 7 | `UPLOAD_ROOT` | 上传/导出持久目录；**用绝对路径**（如 `/data/community/uploads`）并确认写权限；Docker 部署时挂载持久卷 |
| 8 | `SERVER_PORT` | 后端端口（默认 8080）；只暴露前端与 Nginx，不要把后端端口直接暴露公网 |

## 3. 🟡 建议核对

| # | 配置项 | 说明 |
|---|---|---|
| 9 | `JX_CACHE_NAME` | Redis key 前缀（默认 `yunduo_community`）；共用 Redis 实例时保持独立前缀防串键 |
| 10 | 演示数据 | `003_demo_data.sql` 仅用于本地演示，**生产库不要导入**；已导入演示环境的请确认演示账号（academic/teacher 等）已停用或改密 |
| 11 | Swagger / Druid 控制台 | 社区版三份 profile 已默认关闭；如自行打开请确保只在内网可见 |
| 12 | Nginx `client_max_body_size` | 上传接口的文件大小限制按需调整 |
| 13 | 登录密码前端 RSA 密钥对 | `admin-web/src/utils/jsencrypt.js` 内置的是 **RuoYi 上游公开的测试密钥对**（所有 RuoYi 项目相同，公开无泄露风险，仅用于传输混淆非加密）。如需独立密钥对：`openssl genrsa` 生成后替换同文件中的 `publicKey`/`privateKey` 两常量（前后端同源替换） |

## 4. 🟡 百度统计（可选功能）

管理后台 `admin-web/public/index.html` 内置了一段百度统计脚本（站点 ID `7e5c2d39...`），打开后台即向百度上报页面访问。部署方三选一：

1. **保留**：在百度统计后台把你的域名加入该站点，或替换成你自己的站点 ID：
   ```html
   hm.src = "https://hm.baidu.com/hm.js?<你的站点ID>";
   ```
   替换后重新执行 `npm run build:prod`；
2. **整段删除**：删除 `public/index.html` 中「百度统计」注释起的 `<script>...</script>` 段并重新构建；
3. **不管**：脚本会向作者团队的统计站点上报后台 PV（不含任何业务数据与输入内容）。

## 5. 🔴 上线前最后一遍（ Checklist 摘要）

- [ ] 4 个红色默认值全部替换（§1）
- [ ] 数据库只执行了 001 + 002（没有 003 演示数据）
- [ ] `UPLOAD_ROOT` 为绝对路径且可写
- [ ] 后端端口未直接暴露公网；Nginx 反代生效（前端可访问、`/api/` 通、`/profile/` 通）
- [ ] 首次登录 admin 后：修改口令 → 进入校区 → 核对权限 → 建真实业务账号
- [ ] 已配置定时备份（`scripts/backup-db.py` + crontab，见 OPERATIONS.md §2）
- [ ] 已阅读 `SECURITY.md` 的已知漏洞披露（197 条存量，以构建工具链为主）并确认接受
