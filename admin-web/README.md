# admin-web —— 云朵教务社区版 · 管理后台前端

本目录是社区版的管理后台（PC 端）。技术栈沿用既有工程：**Vue 2.6 + Element UI 2.13 + @vue/cli-service 3**，
构建产物是纯静态文件，由 Nginx 托管，接口经 `/api` 反向代理到后端。

完整的功能范围、「哪些能力被排除」、部署方式见仓库根目录的 `README.md` 与 `deploy/README.md`；
演示数据见 `database/003_demo_data.sql`。

## 本地开发

```bash
# 依赖安装
npm install

# 启动开发服务（默认 80 端口；接口代理见 vue.config.js 的 devServer.proxy）
npm run dev
```

> 后端地址在 `vue.config.js` 的 `devServer.proxy` 里配置。

## 构建

```bash
npm run build:prod     # 生产
npm run build:stage    # 测试
```

产物输出到 `dist/`。**Node 17 及以上构建前必须先设置**：

```bash
export NODE_OPTIONS=--openssl-legacy-provider          # Linux / macOS
$env:NODE_OPTIONS="--openssl-legacy-provider"          # Windows PowerShell
```

原因：本项目使用的 webpack 4 会调用 Node 已移除的 OpenSSL 旧接口，不设这个开关会报
`error:0308010C:digital envelope routines::unsupported`。`deploy/Dockerfile.admin-web` 已内置该设置。

## 目录速览

```
src/
├── api/            按模块划分的接口封装（与后端 Controller 一一对应）
├── assets/
│   ├── icons/svg/  图标集：由 require.context 全量注册为 sprite；
│   │               菜单图标名存在数据库表 sys_menu.icon 里，属于「按名动态引用」
│   ├── quill/      富文本编辑器样式（复制自 quill@1.3.7，BSD-3-Clause，见 THIRD_PARTY_NOTICES.md）
│   └── image/      登录页插图与默认头像（默认头像由 scripts/make-default-avatar.py 生成，可复算）
├── components/     公共组件（分页、上传、图标选择、富文本等）
├── layout/         框架布局（侧边栏、顶栏、多页签）
├── router/         路由与权限拦截
├── store/          Vuex
├── utils/          请求封装、权限指令、字典缓存等
└── views/          业务页面（educenter 教务、statistics 统计、system 系统管理等）
```

## 与商业版的差别

社区版前端**不含**商业版专有模块（家长端联动、短信/支付、对象存储直传、监控大盘、定时任务管理等）。
被移除的菜单在数据库种子 `002_seed_system.sql` 中就不存在，因此界面上不会出现「点进去报错」的入口。

## 许可

AGPL-3.0（见仓库根目录 `LICENSE`）。本目录派生自 MIT 许可的 RuoYi-Vue，
其版权声明与全部第三方组件清单见 `THIRD_PARTY_NOTICES.md`。
