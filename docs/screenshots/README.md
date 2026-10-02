# 界面截图（实测生成，非设计稿）

这里的 8 张图不是手工截的，也不是设计稿 —— 它们是**用本仓库自己的产物跑出来的**：
空库建到 `003_demo_data.sql` → 起真后端 → 用 `admin-web/dist`（与 `deploy/Dockerfile.admin-web`
打包进镜像的是同一份构建产物）逐页打开、逐页断言、逐页截图。

生成脚本就在仓库里：**`scripts/visual-evidence.py`**

```bash
set -a && source scripts/env.local && set +a
python scripts/visual-evidence.py --readme-shots --out-dir ./shots
```

脚本做的事（每一步都有断言，不是「跑一遍看看」）：

1. 建空库并执行 `database/001_schema.sql` → `002_seed_system.sql` → `003_demo_data.sql`；
2. 用 `backend/target/jianxue.jar` 以 `community` profile 起后端（没有 jar 会先 `mvn -o package -DskipTests`）；
3. 起本地静态站托管 `admin-web/dist`，并把 `/api/` 反代到后端 —— 与 `deploy/nginx/default.conf`
   的 `^~ /api/` 是同一口径（差别只在 nginx ↔ Python，所以这批证据是 **L4**，不是 L5）；
4. 调 `/getRouters` 拿到**真实菜单**，再逐页用 headless Chrome/Edge 打开；
5. 逐页断言：侧边栏出现该菜单标题、页面不是登录页、不是错误页、像素不是空白；
   并把「演示数据真的渲染出来了」也记下来（学员管理页能找到 `003` 里的学员姓名）；
6. 出三组负向对照（见下）与一张联系表，最后写 `visual-evidence.json`。

## 图与它证明的东西

| 图 | 路由 | 这一张想说明 |
|---|---|---|
| `01-login.jpg` | 未登录访问 `/` | 登录页；社区版没有自助注册入口（注册即「创建第二机构」） |
| `02-dashboard.jpg` | `#/` | 机构管理员进入校区后的工作台 |
| `03-students.jpg` | `#/educenter/student` | 学员管理，列表里有**剩余课时**；数据来自虚构演示集 |
| `04-classes.jpg` | `#/educenter/class` | 班级信息 |
| `05-enrollment.jpg` | `#/educenter/enrollment` | 报读情况：课时账三列（购买 / 消课 / 剩余）。**这一页不显示金额** —— 金额只落库（`amount_cny`）不展示 |
| `06-curriculum.jpg` | `#/educenter/curriculum` | 课表管理（周视图排课） |
| `07-class-report.jpg` | `#/statistics/class` | 班级明细报表 |
| `08-coursetime-report.jpg` | `#/statistics/classTime` | 学生课次统计（与课时流水一致） |

> **图注只写截图里看得到的**：图注本身也是一条要被验证的断言。
> 这条是复验时才抓到的 —— 最初给「报读情况」写的图注是「线下收款登记，金额单位：元」，
> 而 #/educenter/enrollment 的表格列只有 `学员姓名/手机号/报读课程/所在班级/购买数量/消课数量/剩余数量`，
> 一个金额列都没有（金额只落库不展示）。

## 负向对照（判据不能恒真）

* **不带令牌**访问 `/` → 必须落到登录页（否则「页面有内容」可能只是因为谁打开都这样）；
* 访问一个**不存在的路由** → 必须落到「此网页超出了您的权限 / 对不起，您没有权限进入此页面」错误页；
  注意这个页面里**没有**「404」这个可见文本（"404" 只在图片 `alt` 与 class 名里），
  用它当关键字会得到假结论；
* `sys_menu.mec_type` 为空的目录菜单（`用户管理 / 字典管理 / 日志管理 / 操作日志 / 登录日志`）
  直接访问也必须落到同一个错误页。

## 一条实测发现：那几组「看得见却进不去」的菜单

`002_seed_system.sql` 保留了 RuoYi 原生的这 5 个目录菜单，但它们 `mec_type` 是 `NULL`；
而菜单查询用的是 `mec_type = #{mecType}` **等值过滤**，`NULL` 既不等于 `0` 也不等于 `1`，
所以它们**不会出现在任何菜单里**。实测口径：

* 机构管理员（`userType=0`）走 `mec_type` 过滤 → 机构视图（`机构信息` 3 页）与校区视图
  （`首页 / 基础信息 / 教务中心 / 统计数据` 14 页）都看不到这 5 个菜单；
* 教务主管 / 授课教师（内置 role 2 / role 3）走 `sys_role_menu` 映射，而这两个角色
  **本来就没有映射**这 5 个菜单；
* 但角色 1（机构管理员）的 `sys_role_menu` 里**是**映射了它们的 —— 它之所以还是看不到，
  是因为机构管理员的菜单查询走的是 `mec_type` 过滤那条路。

> 所以准确的结论是：**在社区版内置的三个角色下都进不去**；如果管理员自己新建角色并映射了这些菜单，
> 机构人员 / 校区人员那一侧是**能**看到的（那条 SQL 不过滤 `mec_type`）。
> 账号管理在社区版走的是「机构人员 / 校区人员」两个页面。

## 复跑与验收

* 命令执行完会打印证据目录，里面有 `visual-evidence.json`（逐页 DOM 文本长度、像素审计、
  每张图的 sha256）、`screenshots/*.png`（原图 1440x900）、`screenshots/*.dom.html`（渲染后的 DOM）、
  以及 `contact-sheet.jpg`（全部页面缩略图，供人眼复核）。
* `--readme-shots` 会把上面那 8 张按宽 1200 缩放写回本目录；同一批原图重跑得到**同样的字节与 sha256**，
  因此这些图也是可复算的。
* 本批证据的级别是 **L4（内容断言 + 前后值）**：它证明「本仓库的产物能正确渲染并展示演示数据」，
  但**不**替代 L5 —— 在真实 Docker 环境里部署后再取一次证，需要前者先跑通 `deploy/` 的编排。
