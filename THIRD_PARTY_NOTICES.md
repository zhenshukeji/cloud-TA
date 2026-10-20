# 第三方版权与许可声明

本项目自有代码采用 AGPL-3.0，见根目录 `LICENSE`。第三方依赖及复制进源码的第三方文件仍适用各自原始许可；下列声明随源代码保留。

## 1. 上游项目来源声明

本项目**派生自**下列上游工程。按其许可证要求，逐字保留版权声明与许可原文：

### RuoYi / RuoYi-Vue —— MIT

- 上游地址：https://gitee.com/y_project/RuoYi-Vue
- **版权声明（逐字）**：`Copyright (c) 2018 RuoYi`
- 许可证原文：https://raw.githubusercontent.com/yangzongzhuan/RuoYi/master/LICENSE
- 派生范围：
  - admin-web/：整套前端脚手架与公共组件（vue-element-admin / RuoYi-Vue 血统），本次改造在其上做精简与重做，未替换技术栈
  - backend/：framework/ 与 common/ 下的多个基础设施类（RuoYiConfig、ExcelUtil、FileUploadUtils、AddressUtils、SecurityConfig 的基础实现等）
- 义务履行：保留上游版权声明与许可链接；第三方许可不因本项目提供商业许可而被替换。

## 2. 后端运行时依赖（按许可证分组）

共 **109** 个。

### Apache License, Version 2.0（44 个）

- `com.fasterxml:classmate:1.5.1`
- `commons-codec:commons-codec:1.13`
- `commons-fileupload:commons-fileupload:1.3.3`
- `commons-io:commons-io:2.5`
- `io.jsonwebtoken:jjwt:0.9.0`
- `net.bytebuddy:byte-buddy:1.10.8`
- `org.apache.commons:commons-collections4:4.1`
- `org.apache.commons:commons-lang3:3.9`
- `org.apache.commons:commons-pool2:2.7.0`
- `org.apache.logging.log4j:log4j-api:2.17.1`
- `org.apache.logging.log4j:log4j-to-slf4j:2.12.1`
- `org.apache.tomcat.embed:tomcat-embed-core:9.0.31`
- `org.apache.tomcat.embed:tomcat-embed-el:9.0.31`
- `org.apache.tomcat.embed:tomcat-embed-websocket:9.0.31`
- `org.springframework:spring-aop:5.2.4.RELEASE`
- `org.springframework:spring-beans:5.2.4.RELEASE`
- `org.springframework:spring-context:5.2.4.RELEASE`
- `org.springframework:spring-context-support:5.2.4.RELEASE`
- `org.springframework:spring-core:5.2.4.RELEASE`
- `org.springframework:spring-expression:5.2.4.RELEASE`
- `org.springframework:spring-jcl:5.2.4.RELEASE`
- `org.springframework:spring-jdbc:5.2.4.RELEASE`
- `org.springframework:spring-oxm:5.2.4.RELEASE`
- `org.springframework:spring-tx:5.2.4.RELEASE`
- `org.springframework:spring-web:5.2.4.RELEASE`
- `org.springframework:spring-webmvc:5.2.4.RELEASE`
- `org.springframework.boot:spring-boot:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-autoconfigure:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-aop:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-data-redis:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-jdbc:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-json:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-logging:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-security:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-tomcat:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-validation:2.2.5.RELEASE`
- `org.springframework.boot:spring-boot-starter-web:2.2.5.RELEASE`
- `org.springframework.data:spring-data-commons:2.2.5.RELEASE`
- `org.springframework.data:spring-data-keyvalue:2.2.5.RELEASE`
- `org.springframework.data:spring-data-redis:2.2.5.RELEASE`
- `org.springframework.plugin:spring-plugin-core:1.2.0.RELEASE`
- `org.springframework.plugin:spring-plugin-metadata:1.2.0.RELEASE`
- `org.yaml:snakeyaml:1.25`

### The Apache Software License, Version 2.0（36 个）

- `com.fasterxml.jackson.core:jackson-annotations:2.10.2`
- `com.fasterxml.jackson.core:jackson-core:2.10.2`
- `com.fasterxml.jackson.core:jackson-databind:2.10.2`
- `com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.10.2`
- `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.2`
- `com.fasterxml.jackson.module:jackson-module-parameter-names:2.10.2`
- `com.github.jedis-lock:jedis-lock:1.0.0`
- `com.google.guava:guava:20.0`
- `com.zaxxer:HikariCP:3.4.2`
- `commons-collections:commons-collections:3.2.1`
- `commons-lang:commons-lang:2.4`
- `io.springfox:springfox-core:2.9.2`
- `io.springfox:springfox-schema:2.9.2`
- `io.springfox:springfox-spi:2.9.2`
- `io.springfox:springfox-spring-web:2.9.2`
- `io.springfox:springfox-swagger-common:2.9.2`
- `io.springfox:springfox-swagger-ui:2.9.2`
- `io.springfox:springfox-swagger2:2.9.2`
- `org.ahocorasick:ahocorasick:0.3.0`
- `org.apache.maven:maven-plugin-api:2.0`
- `org.apache.poi:poi:3.17`
- `org.apache.poi:poi-ooxml:3.17`
- `org.apache.poi:poi-ooxml-schemas:3.17`
- `org.apache.velocity:velocity:1.7`
- `org.apache.xmlbeans:xmlbeans:2.6.0`
- `org.codehaus.plexus:plexus-utils:3.0.8`
- `org.jetbrains:annotations:19.0.0`
- `org.mapstruct:mapstruct:1.2.0.Final`
- `org.mybatis:mybatis:3.4.6`
- `org.mybatis:mybatis-spring:1.3.2`
- `org.mybatis.spring.boot:mybatis-spring-boot-autoconfigure:1.3.2`
- `org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.2`
- `org.springframework.security:spring-security-config:5.2.2.RELEASE`
- `org.springframework.security:spring-security-core:5.2.2.RELEASE`
- `org.springframework.security:spring-security-web:5.2.2.RELEASE`
- `stax:stax-api:1.0.1`

### The Apache License, Version 2.0（6 个）

- `com.baomidou:mybatis-plus:2.2.0`
- `com.baomidou:mybatis-plus-boot-starter:2.2.0`
- `com.baomidou:mybatis-plus-core:2.2.0`
- `com.baomidou:mybatis-plus-generate:2.2.0`
- `com.baomidou:mybatis-plus-support:2.2.0`
- `com.github.eljah:xmindjbehaveplugin:0.8`

### Apache License 2.0（4 个）

- `io.swagger:swagger-annotations:1.5.21`
- `io.swagger:swagger-models:1.5.21`
- `jakarta.validation:jakarta.validation-api:2.0.2`
- `org.hibernate.validator:hibernate-validator:6.0.18.Final`

### Apache 2（3 个）

- `com.alibaba:druid:1.1.14`
- `com.alibaba:druid-spring-boot-starter:1.1.14`
- `com.alibaba:fastjson:1.2.70`

### The MIT License (MIT)（3 个）

- `com.github.pagehelper:pagehelper:5.1.4`
- `com.github.pagehelper:pagehelper-spring-boot-autoconfigure:1.2.5`
- `com.github.pagehelper:pagehelper-spring-boot-starter:1.2.5`

### Eclipse Public License - v 1.0 / GNU Lesser General Public License（2 个）

- `ch.qos.logback:logback-classic:1.2.3`
- `ch.qos.logback:logback-core:1.2.3`

### MIT License（2 个）

- `org.slf4j:jul-to-slf4j:1.7.30`
- `org.slf4j:slf4j-api:1.7.30`

### Apache License, version 2.0（1 个）

- `org.jboss.logging:jboss-logging:3.4.1.Final`

### Apache-2.0（1 个）

- `com.github.promeg:tinypinyin:2.0.3` ← *pom 与该 jar 都没有 `<licenses>`，许可取自上游仓库原文*

### BSD License（1 个）

- `com.github.virtuald:curvesapi:1.04`

### EPL 2.0 / GPL2 w/ CPE（1 个）

- `jakarta.annotation:jakarta.annotation-api:1.3.5`

### Eclipse Public License - v 1.0（1 个）

- `org.aspectj:aspectjweaver:1.9.5`

### GNU Library or Lesser General Public License (LGPL) V2.1 / The Apache Software License, Version 2.0（1 个）

- `com.github.jsqlparser:jsqlparser:1.0`

### MIT（1 个）

- `redis.clients:jedis:3.1.0`

### New BSD License（1 个）

- `eu.bitwalker:UserAgentUtils:1.19`

### The GNU General Public License, v2 with FOSS exception（1 个）

- `mysql:mysql-connector-java:8.0.19`

## 3. 前端直接依赖（按许可证分组）

### MIT（55 个）

- `@babel/core@7.0.0`（devDependencies）
- `@babel/parser@7.29.7`（devDependencies）
- `@babel/register@7.0.0`（devDependencies）
- `@fullcalendar/core@5.11.5`（dependencies）
- `@fullcalendar/daygrid@5.11.5`（dependencies）
- `@fullcalendar/interaction@5.11.5`（dependencies）
- `@fullcalendar/list@5.11.5`（dependencies）
- `@fullcalendar/timegrid@5.11.5`（dependencies）
- `@fullcalendar/vue@5.11.5`（dependencies）
- `@riophae/vue-treeselect@0.4.0`（dependencies）
- `@vue/cli-plugin-babel@3.5.3`（devDependencies）
- `@vue/cli-plugin-eslint@3.12.1`（devDependencies）
- `@vue/cli-plugin-unit-jest@3.5.3`（devDependencies）
- `@vue/cli-service@3.5.3`（devDependencies）
- `@vue/test-utils@1.0.0-beta.29`（devDependencies）
- `autoprefixer@9.8.8`（devDependencies）
- `axios@0.21.4`（dependencies）
- `babel-core@7.0.0-bridge.0`（devDependencies）
- `babel-eslint@10.0.1`（devDependencies）
- `babel-jest@23.6.0`（devDependencies）
- `chalk@2.4.2`（devDependencies）
- `chokidar@2.1.5`（devDependencies）
- `clipboard@2.0.11`（dependencies）
- `connect@3.6.6`（devDependencies）
- `element-ui@2.13.0`（dependencies）
- `eslint@5.15.3`（devDependencies）
- `eslint-plugin-vue@5.2.2`（devDependencies）
- `html-webpack-plugin@3.2.0`（devDependencies）
- `http-proxy-middleware@0.19.2`（devDependencies）
- `husky@1.3.1`（devDependencies）
- `js-cookie@2.2.1`（dependencies）
- `jsencrypt@3.0.0-rc.1`（dependencies）
- `lint-staged@8.1.5`（devDependencies）
- `mockjs@1.0.1-beta3`（devDependencies）
- `moment@2.30.1`（dependencies）
- `normalize.css@7.0.0`（dependencies）
- `nprogress@0.2.0`（dependencies）
- `path-to-regexp@2.4.0`（dependencies）
- `plop@2.3.0`（devDependencies）
- `runjs@4.4.2`（devDependencies）
- `sass@1.101.0`（dependencies）
- `sass-loader@7.3.1`（devDependencies）
- `screenfull@4.2.0`（dependencies）
- `script-ext-html-webpack-plugin@2.1.3`（devDependencies）
- `script-loader@0.7.2`（devDependencies）
- `serve-static@1.16.3`（devDependencies）
- `svg-sprite-loader@4.1.3`（devDependencies）
- `svgo@1.2.0`（devDependencies）
- `tippy.js@6.3.7`（dependencies）
- `vue@2.6.14`（dependencies）
- `vue-count-to@1.0.13`（dependencies）
- `vue-quill-editor@3.0.6`（dependencies）
- `vue-router@3.0.2`（dependencies）
- `vue-template-compiler@2.6.14`（devDependencies）
- `vuex@3.1.0`（dependencies）

### Apache-2.0（1 个）

- `echarts@4.9.0`（dependencies）

### ISC（1 个）

- `vue-cropper@0.4.9`（dependencies）

## 4. 需要注意的许可证族（自动提示）

下列许可证族在**组合分发**时需要额外确认（社区版自身是 AGPL-3.0，二者并不自动冲突，但义务要逐条看）：

- **EPL 2.0 / GPL2 w/ CPE** —— 1 个组件
- **GNU Library or Lesser General Public License (LGPL) V2.1 / The Apache Software License, Version 2.0** —— 1 个组件
- **The GNU General Public License, v2 with FOSS exception** —— 1 个组件

已知需要单独说明的一条：`mysql:mysql-connector-java` 采用 **GPLv2 + Oracle Universal FOSS Exception 1.0**，
该例外条款允许与 AGPL-3.0 组合分发；社区版保留该驱动并在本文件与 `COMMERCIAL-LICENSING.md` 中声明。

另一条：`admin-web` 整体派生自 MIT 的 RuoYi-Vue（见第 1 节），MIT 与 AGPL-3.0 组合分发无冲突，
但 MIT 的版权声明必须保留 —— 本文件第 1 节已逐字给出。

## 5. 源码树内复制的第三方代码（vendored）

这些文件不是从 node_modules 引用，而是**复制进源码树**的第三方文件。BSD-3-Clause 要求「保留版权声明与免责声明」，因此必须逐个登记。

| 文件 | 字节 | sha256 | 上游 | 上游许可 | 是否与上游逐字节一致 |
|---|---|---|---|---|---|
| `admin-web/src/assets/quill/quill.core.css` | 10156 | `04dad2e4771616c0…` | `node_modules/quill/dist/quill.core.css` | BSD-3-Clause | 否（已修改 → BSD-3 需保留版权声明） |
| `admin-web/src/assets/quill/quill.snow.css` | 29319 | `59d29ba3a7c282b8…` | `node_modules/quill/dist/quill.snow.css` | BSD-3-Clause | 否（已修改 → BSD-3 需保留版权声明） |
| `admin-web/src/assets/quill/quill.bubble.css` | 29966 | `15a63211f0b7f0b8…` | `node_modules/quill/dist/quill.bubble.css` | BSD-3-Clause | 否（已修改 → BSD-3 需保留版权声明） |

## 6. 未声明许可证的组件

无 —— 全部 109 个后端运行时依赖与 57 个前端直接依赖都能追到许可证声明。
