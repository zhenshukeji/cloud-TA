# 运维手册：备份、恢复、升级

面向用 Docker Compose（`deploy/`）或本地方式部署社区版的运维同学。
所有命令里的 `<...>` 都要换成你自己的值；**不要**把口令写进 shell 历史或提交进仓库。

---

## 1. 数据都在哪

| 数据 | 位置 | 需要备份吗 |
|---|---|---|
| 业务数据 | MySQL 库（默认 `yunduo_community`） | **要** |
| 上传文件（学员导入、课程封面等） | 本地目录 `UPLOAD_ROOT`（Compose 里是命名卷 `yunduo-community-uploads`） | **要** |
| 登录态 / 失败计数 / 短时锁 | Redis（逻辑库默认 12） | **不用** —— 丢了只会让所有人重新登录 |
| 配置与密钥 | `deploy/.env`（或 `scripts/env.local`） | **要，但要单独保管**（含密钥，不要和数据放一起） |

## 2. 备份

### 2.1 数据库（逻辑备份，最稳）

```bash
# Compose 部署
docker compose --env-file deploy/.env -f deploy/docker-compose.yml exec -T mysql \
  mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers \
  "$MYSQL_DATABASE" > backup-$(date +%Y%m%d-%H%M).sql

# 本地部署
mysqldump -h<host> -P<port> -u<user> -p --single-transaction --routines --triggers \
  yunduo_community > backup-$(date +%Y%m%d-%H%M).sql
```

`--single-transaction` 保证 InnoDB 表的一致性快照，不需要停服。

### 2.2 无 mysqldump 客户端时的备份脚本

`scripts/backup-db.py` 用纯 Python（pymysql）产出等价的逻辑备份（结构 + 全量数据，
已正确排除 `bd_class_record.active_slot`、`bd_course_time.active_account` 等 STORED 生成列——
MySQL 8 拒绝向生成列显式写值，直接 `SELECT *` 回放会失败）：

```bash
set -a && . scripts/env.local && set +a
python scripts/backup-db.py --out backup-$(date +%Y%m%d-%H%M).sql
```

恢复：在**独立空库**上回放备份文件（`mysql -h<host> -P<port> -u<user> -p <库名> < backup.sql`），
恢复后按 §3 的行数/不变量对账。

### 2.3 上传目录

```bash
# Compose：从一个临时容器里把命名卷打包出来
docker run --rm -v yunduo-community-uploads:/data -v "$PWD":/backup alpine \
  tar czf /backup/uploads-$(date +%Y%m%d).tar.gz -C /data .

# 本地部署
tar czf uploads-$(date +%Y%m%d).tar.gz -C "$UPLOAD_ROOT" .
```

### 2.4 备份保留建议

- 至少保留 **7 份日备 + 4 份周备**；
- 每次升级前**额外**做一份，并单独命名（如 `pre-upgrade-<版本>`）；
- 备份文件里的 `mysql` dump 含业务数据，**不要**放到公开的位置。

## 3. 恢复

**顺序很重要：先库、后文件。**

```bash
# 1) 停应用（避免恢复期间写入）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml stop backend admin-web

# 2) 恢复数据库
docker compose --env-file deploy/.env -f deploy/docker-compose.yml exec -T mysql \
  mysql -uroot -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE" < backup-YYYYMMDD-HHMM.sql

# 3) 恢复上传目录
docker run --rm -v yunduo-community-uploads:/data -v "$PWD":/backup alpine \
  tar xzf /backup/uploads-YYYYMMDD.tar.gz -C /data

# 4) 起应用
docker compose --env-file deploy/.env -f deploy/docker-compose.yml start backend admin-web
```

恢复后自检：用一个已知账号登录 → 打开「学员管理」能看到学员 → 打开「上课记录」能看到历史点名 →
打开一个学员的「课次信息」看到剩余课时不为 0 且与历史一致。

## 4. 升级

```bash
# 1) 备份（见第 2 节），记录当前版本
# 2) 拉新代码
git pull

# 3) 重新构建并启动（提前安排维护窗口，不能视为零停机滚动发布）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build

# 4) 看日志与健康状态
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs -f backend
docker compose --env-file deploy/.env -f deploy/docker-compose.yml ps   # 都应是 healthy

# 5) 清浏览器缓存后重新登录
```

### 4.1 数据库脚本会不会重复执行？

- `database/001_schema.sql` 用的是 `CREATE TABLE IF NOT EXISTS`，可安全重复执行；
- `database/002_seed_system.sql` 每条 `INSERT` 都带显式主键与 `ON DUPLICATE KEY UPDATE` 自赋值子句，
  重复执行是 **no-op**；
- `database/003_demo_data.sql`（演示数据）是**先删自己的固定主键区间再插入**，重复执行结果一致 ——
  但**不要**在有真实数据的库上执行它。

> 注意：Docker 部署时，初始化脚本只在 **MySQL 数据卷为空的首次启动** 执行；
> 升级**不会**重跑它们。需要补数据结构时请手工执行对应脚本。

### 4.2 升级后「所有人都要重新登录」的两种情况

1. 改了 `TOKEN_SECRET`（令牌签名变了，旧令牌自然失效）；
2. 从更早的社区版升级到本版：登录令牌的 Redis key 增加了**应用级前缀**
   （`<REDIS_CACHE_NAME>_login_tokens:<uuid>`），旧 key 在 TTL（默认 5 小时）内不再被读取。

这是预期行为，不需要处置；想立刻让旧 key 消失，可等 TTL 或由运维清理
`login_tokens:*` 前缀的 key（**清理前先确认这些 key 属于本应用**）。

## 5. 常见故障

| 症状 | 先看哪里 |
|---|---|
| 后端容器反复重启 | `logs -f backend`；启动守卫会明确打印「缺少哪个环境变量」或「哪个密钥不达标」 |
| 前端能打开但所有接口 401 | `TOKEN_SECRET` 是否换过；浏览器是否还带着旧令牌（清缓存重登） |
| 列表页查不到数据 | 是否**先进入了校区**（机构管理员登录后需要选校区）；或者列表按校区过滤而数据归属不对 |
| 导出/下载报错 | `UPLOAD_ROOT` 是否可写；下载目录是否被清理策略删掉 |
| 「课时不足，无法扣减」 | 这是业务拦截，不是故障：该学员该课程的剩余课时已为 0 |
| 「不可重复点名」 | 同一班级同一时段已经点过名；改时段或先纠正已有记录 |

## 6. 升级前检查清单

- [ ] 已做数据库 + 上传目录备份，并**验证过备份文件能打开**
- [ ] 已记录当前版本（`git rev-parse --short HEAD`）与镜像标签
- [ ] 已确认 `deploy/.env` 里的密钥是自建值，且不在任何仓库里
- [ ] 已知悉「升级后需重新登录」的两种情况（4.2）
- [ ] 升级窗口内没有正在进行的点名校对（避免并发写）
