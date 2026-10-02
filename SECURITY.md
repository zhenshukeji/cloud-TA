# 安全策略与漏洞报告

## 1. 报告渠道

**请不要用公开 Issue 报告安全问题。** 请通过以下任一方式私下联系维护者：

- 邮件：见 `COMMERCIAL-LICENSING.md` 中的联系方式（标题请以 `[SECURITY]` 开头）
- 或使用托管平台提供的「Private vulnerability reporting / 私密安全问题」功能

请在报告中尽量包含：受影响版本或提交、复现步骤、影响范围（越权 / 数据泄露 / 拒绝服务 / 其他）、
以及你判断的严重级别。**不要**附带真实机构、家长、学员或交易数据。

## 2. 我们的处理约定

| 阶段 | 约定 |
|---|---|
| 确认收到 | 尽力在 5 个工作日内回复 |
| 初步评估 | 与你确认复现与影响面，必要时请你补充信息 |
| 修复与验证 | 修复后请你复核；拒绝复现的也会给出理由 |
| 公开 | 修复发布后再公开细节；如需 CVE / 公告会与你协商署名 |

## 3. 明确不在范围内

- 需要**物理接触**或已拿到服务器 root／数据库直连才能利用的问题；
- 依赖组件自身的漏洞（请报给上游；本仓库会在 `THIRD_PARTY_NOTICES.md` 里维护组件清单）；
- 部署方自己造成的配置问题（例如把 MySQL / Redis 端口暴露到公网、把 `.env` 提交进仓库、
  沿用示例哑值不改）。
- **Do not** 在生产实例上做压测、扫描或越权测试。

## 4. 已经内建的安全设计（便于评估「这是不是设计如此」）

- **口令**：只从环境变量注入，源码与 SQL 里没有任何默认口令；初始管理员口令有强度硬校验
  （长度 ≥ 10、≥ 2 类字符、不等于登录账号、不在弱口令清单），不达标**拒绝启动**。
- **令牌**：JWT（`TOKEN_SECRET`，强度硬校验：长度 ≥ 32、不同字符 ≥ 8、拒绝占位符串）+ Redis 登录态；
  令牌 key 带应用级前缀（多应用共用 Redis 时不会互相覆盖）。登录失败超过阈值会临时锁定账号。
- **鉴权形态**：接口级 `@PreAuthorize`；**权限拒绝返回 `HTTP 200` + `body.code=403`**，
  客户端必须判 `body.code`（只看 HTTP 状态码会把「拒绝」当成成功）。
- **租户隔离**：机构 / 校区归属一律由服务端从登录态推导，**请求体里伪造 `mecId`/`campusId` 不生效**。
- **暴露面**：默认关闭 Swagger 与 Druid 控制台；Docker 部署只发布前端端口，MySQL / Redis 不对外发布；
  上传文件落本地目录，不使用任何第三方对象存储。
- **上传**：文件名与路径做合法性校验；上传根目录由 `UPLOAD_ROOT` 指定。
- **数据导出**：导出接口需要对应权限；导出文件写入本地 `download` 目录后由下载接口读取，可带 `delete=true` 即时清理。

## 5. 部署方自查清单（部署后建议逐条确认）

1. `TOKEN_SECRET` 是**自己**用 `openssl rand -base64 48` 生成的随机值，不是示例里的形状合规哑值；
2. `INITIAL_ADMIN_PASSWORD` 已改，且首次登录后立刻改密；
3. MySQL / Redis 端口没有映射到公网；
4. `deploy/.env` / `scripts/env.local` 没有被提交进任何仓库（`.gitignore` 已默认忽略）；
5. 数据库账号不是 `root`，且只授权 `yunduo_community*` 库；
6. 已按要求做备份（见 `docs/OPERATIONS.md`）。
<!-- vulns:begin -->

## 6. 已知依赖漏洞（快照 2026-09-30）

**先把话说清楚：本仓库的依赖树里确实有已知漏洞。** 社区版沿用的是 2020 年前后的技术栈
（Spring Boot 2.2.5 / Spring Framework 5.2.4 / Vue CLI 3 / axios 0.18），
这些版本的组件在公开漏洞库里有大量已披露条目。我们**不宣称「无漏洞」**，而是把清单查出来、公开放在这里，
由部署方判断是否升级。

**查询口径**：用 [OSV](https://osv.dev/) 按**项目锁定的精确版本**查询（不是「最新版本」），快照日期 **（未知，见 vulns.json）**。

下表口径：

| 口径 | 数值 |
|---|---|
| 受影响的 Maven 运行时依赖 | **39** 个包（当前依赖树共 109 个；查询当时 109 个） |
| 受影响的 npm 直接依赖 | **12** 个包（当前依赖树共 57 个；查询当时 57 个） |
| 去重后的漏洞条目 | **197** 条 |
| 严重级别分布 | CRITICAL **19**，HIGH **83**，MODERATE **79**，LOW **16** |

严重级别分布偏高是**栈的年代问题**，不是「本项目引入了特别危险的组件」——
升级是需要整体回归的决定，不属于本仓库的默认动作。

### 6.1 后端受影响最重的组件

| 组件 | 该组件命中条数 | 其中最高级别 | 代表作 |
|---|---|---|---|
| `org.apache.tomcat.embed:tomcat-embed-core@9.0.31` | 44 | CRITICAL | `GHSA-5m62-pw8w-7w9f` |
| `com.fasterxml.jackson.core:jackson-databind@2.10.2` | 12 | HIGH | `GHSA-288c-cq4h-88gq` |
| `org.springframework:spring-webmvc@5.2.4.RELEASE` | 11 | CRITICAL | `GHSA-36p3-wjmg-h94x` |
| `ch.qos.logback:logback-core@1.2.3` | 8 | HIGH | `GHSA-vmq6-5m68-f53m` |
| `org.yaml:snakeyaml@1.25` | 8 | HIGH | `GHSA-3mc7-4q67-w48m` |
| `org.springframework:spring-expression@5.2.4.RELEASE` | 8 | HIGH | `GHSA-775g-4xr8-78h8` |
| `org.springframework:spring-web@5.2.4.RELEASE` | 7 | CRITICAL | `GHSA-4wrc-f8pq-fpqp` |
| `org.springframework.security:spring-security-core@5.2.2.RELEASE` | 7 | CRITICAL | `GHSA-hh32-7344-cg2f` |
| `org.springframework.security:spring-security-web@5.2.2.RELEASE` | 6 | CRITICAL | `GHSA-c4q5-6c82-3qpw` |
| `org.codehaus.plexus:plexus-utils@3.0.8` | 4 | CRITICAL | `GHSA-8vhq-qq4p-grq3` |

### 6.2 前端受影响最重的组件

| 组件 | 该组件命中条数 | 其中最高级别 | 代表作 |
|---|---|---|---|
| `axios@0.21.4` | 23 | HIGH | `GHSA-3g43-6gmg-66jw` |
| `svgo@1.2.0` | 3 | HIGH | `GHSA-2p49-hgcm-8545` |
| `moment@2.29.0` | 2 | HIGH | `GHSA-8hfj-j24r-96c4` |
| `http-proxy-middleware@0.19.1` | 2 | HIGH | `GHSA-c7qv-q95q-8v27` |
| `echarts@4.2.1` | 1 | MODERATE | `GHSA-fgmj-fm8m-jvvx` |
| `js-cookie@2.2.1` | 1 | HIGH | `GHSA-qjx8-664m-686j` |
| `path-to-regexp@2.4.0` | 1 | HIGH | `GHSA-9wv6-86v2-598j` |
| `vue@2.6.14` | 1 | LOW | `GHSA-5j4c-8p2g-v4jx` |
| `@babel/core@7.0.0` | 1 | LOW | `GHSA-4x5r-pxfx-6jf8` |
| `mockjs@1.0.1-beta3` | 1 | HIGH | `GHSA-mh8j-9jvh-gjf6` |

> 上表只是示意「重灾区在哪」，不是完整清单。严重级别取自 OSV 记录里的 CVSS / GitHub advisory 分级。

### 6.3 怎么自己复现这份清单

```bash
# 后端：先导出本项目的运行时依赖（含精确版本），再逐条查 OSV
cd backend && mvn dependency:list -DincludeScope=runtime -DoutputFile=/tmp/deps.txt
# 然后对每行 groupId:artifactId:jar:version:scope 调 POST https://api.osv.dev/v1/querybatch
#   { "package": { "ecosystem": "Maven", "name": "groupId:artifactId" }, "version": "…" }

# 前端：npm audit 覆盖同一类问题（对本项目建议先 npm install 再跑）
cd admin-web && npm audit --omit=dev
```

### 6.4 我们的建议

1. **不要把社区版直接暴露在公网**：它定位是「自建自用的教务后台」，建议放内网或加一层访问控制。
2. 若要公网部署，优先自己升到仍在维护的 Spring Boot 2.7/3.x 与 Vue 3 分支，并重跑本项目自带的验收脚本。
3. 与漏洞相关的**代码层**防线本项目已经做了（见第 4 节）；上面这些是**依赖版本**层面的债务。

<!-- vulns:end -->
