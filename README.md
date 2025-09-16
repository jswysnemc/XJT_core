

  # XJT 学生成绩管理系统

  ## 1. 项目简介
  XJT 学生成绩管理系统是一套前后端分离的教务成绩平台，涵盖用户认证、课程与班级管理、成绩录入与统计分析、个人资料维护等核心业务。系统采用 Spring Boot 3 + MyBatis-Plus 构建 RESTful 后端服务，前端使用 Vue 3 + Vite + Ant Design Vue 搭
  建，支持基于角色的权限控制与 JWT 认证。

  ## 2. 技术栈
  - **后端**：Spring Boot 3.5、Spring Security、MyBatis-Plus、Redis、MySQL、Knife4j、jjwt、Hutool、Lombok
  - **前端**：Vite、Vue 3、TypeScript、Pinia、Vue Router、Ant Design Vue、Axios、ECharts、XLSX、FileSaver
  - **支撑**：Docker/MySQL、Redis、pnpm(npm/yarn)、Maven、Node.js ≥ 18、JDK 17

  ## 3. 仓库结构

  XJT/
  ├── XJT_core_service/           # Spring Boot 后端
  │   ├── pom.xml
  │   ├── src/main/java/com/ljp/xjt/
  │   │   ├── common/             # 统一响应 & 异常
  │   │   ├── config/             # 安全、MyBatis、Knife4j 配置
  │   │   ├── controller/         # 业务 REST 控制器
  │   │   ├── dto/                # 请求与响应 DTO
  │   │   ├── entity/             # MyBatis-Plus 实体
  │   │   ├── mapper/             # Mapper 接口
  │   │   ├── security/           # JWT 过滤器与处理器
  │   │   └── service/impl/       # 领域服务实现
  │   └── src/main/resources/
  │       ├── application.yml     # 运行配置
  │       └── mapper/*.xml        # MyBatis 映射文件
  └── XJT_web/                    # Vue 3 前端
  ├── package.json / pnpm-lock.yaml
  ├── src/
  │   ├── api/                # Axios API 封装
  │   ├── components/         # UI 组件
  │   ├── layout/             # 框架布局
  │   ├── router/             # 动态路由与守卫
  │   ├── store/              # Pinia 模块
  │   ├── utils/              # Axios 实例、工具函数
  │   └── views/              # 角色视图页
  └── vite.config.ts


  ## 4. 功能模块
  ### 后端
  - **认证与授权**：`AuthController` 提供登录、注册、令牌刷新、校验；`SecurityConfig` 配置 JWT 无状态认证，角色包含 `ROLE_ADMIN`、`ROLE_TEACHER`、`ROLE_STUDENT`。
  - **用户资料**：`ProfileController` 支持获取个人信息、修改密码、头像上传/回显，文件存储在 `uploads/`。
  - **基础数据**：`DepartmentController`、`MajorController`、`ClassesController`、`CourseController`、`TeacherController`、`StudentAdminController` 等实现组织、课程、学生、教师等管理。
  - **教学排课**：`TeachingAssignmentController` 管理教师-课程-班级关系。
  - **成绩管理**：`AdminGradeController`、`TeacherController`、`MyTeachingController` 支持教师录入、批量导入、管理员查询分析、学生自助查询。
  - **统计分析**：`StatisticsController` 汇总成绩分布、课程通过率等指标，供前端图表展示。
  - **全局治理**：统一响应 `ApiResponse`、异常捕获 `GlobalExceptionHandler`、业务异常 `BusinessException`，日志配置输出到 `logs/student-system.log`，开放 Spring Actuator 基础端点。

  ### 前端
  - **认证流程**：`src/store/modules/auth.ts` 持久化 JWT，刷新后自动补取 profile。
  - **动态路由**：`src/router/index.ts` 根据角色注入菜单，未授权跳转 404。
  - **界面布局**：`src/layout/Layout.vue` 搭配 Header/Breadcrumb/Sidebar 组件构成统一外壳。
  - **业务页面**：
    - 学生：课程与成绩查询、成绩曲线展示。
    - 教师：成绩录入、Excel 批量导入/导出、班级花名册。
    - 管理员：用户、班级、课程、排课、成绩查询分析管理面板。
  - **API 层**：`src/api/*.ts` 对应后端模块封装，`src/utils/request.ts` 统一拦截请求/响应，自动注入 token 与分页参数转化。
  - **数据可视化**：`GradeAnalysis.vue` 等页面使用 ECharts 展示统计数据。

  ## 5. 环境准备
  1. 安装 JDK 17、Maven 3.9+、Node.js 18+、pnpm (或 npm/yarn)、MySQL 8+、Redis 6+。
  2. 创建数据库 `school_db` 并导入表结构（可参考实体命名自行建表）。
  3. 准备 Redis 实例，确保密码与端口可用。
  4. 可在 `application.yml` 中将数据源与 Redis 连接改为本地环境，并建议改用环境变量或外部化配置管理敏感信息。

  ## 6. 快速开始
  ### 后端
  ```bash
  cd XJT_core_service
  # 如需自定义配置，可复制 application.yml 为 application-local.yml
  mvn clean package         # 编译与单元测试
  mvn spring-boot:run       # 开发环境启动，默认端口 8080，Context-Path /api

  - 生产部署可执行 java -jar target/student-grade-system-1.0.0.jar --spring.profiles.active=prod。
  - 上传头像保存至 uploads/，请提前创建目录并在生产环境配置持久化存储。
  - Spring Actuator：/api/actuator/health、/api/actuator/info 等。

  ### 前端

  cd XJT_web
  pnpm install          # 或 npm install / yarn
  pnpm dev              # 本地开发，默认 http://localhost:5173
  pnpm build            # 生成 dist/ 静态资源
  pnpm preview          # 预览构建产物

  - vite.config.ts 已配置 /api 代理到 http://localhost:8080，如部署后端于其他地址请同步修改或配置反向代理。
  - 构建输出 dist/ 可交由 Nginx/静态资源服务托管。

  ## 7. 配置说明（XJT_core_service/src/main/resources/application.yml）

  - 数据源：spring.datasource.*，含连接池 Hikari 设置。
  - Redis：spring.data.redis.*。若禁用缓存可临时评论相关配置。
  - 多环境：定义 dev、test、prod profile，默认激活 dev。
  - JWT：app.jwt.secret/expiration/refresh-expiration，建议生产环境使用长度 ≥ 256 bit 的密钥，放入安全存储。
  - 文件上传：app.file.upload-path/max-size/allowed-types，默认存储于项目根目录 uploads。
  - 日志：输出级别、格式及文件滚动策略 logging.*。
  - Knife4j：/api/doc.html 提供交互式 API 文档。
  - Actuator：management.endpoints.web.exposure.include 暴露 health/info/metrics。

  ## 8. API 文档与调试

  - 服务启动后访问 http://localhost:8080/api/doc.html 查看 Knife4j OpenAPI 文档，可在线调试。
  - Swagger 分组、接口描述覆盖主要业务控制器，便于前端对接。
  - 建议结合 Postman/Apifox 导出调试集合。

  ## 9. 权限模型与安全

  - 登录流程：/auth/login -> 颁发访问/刷新令牌；AuthServiceImpl 校验用户密码（BCrypt）、角色。
  - Token 校验：JwtAuthenticationFilter 解析 Authorization Bearer 头，注入 SecurityContext；过期或无效由 JwtAuthenticationEntryPoint、JwtAccessDeniedHandler 统一响应。
  - 角色限制：控制器方法通过 @PreAuthorize 与路由配置控制访问。
  - 密码修改：ProfileController 校验旧密码与确认密码。
  - 建议：生产环境开启 HTTPS、配置跨域策略、缩短 token 有效期、增加刷新令牌黑名单机制。

  ## 10. 前端开发约定

  - 路由元信息 meta.roles 控制菜单显示及访问权限。
  - 状态管理统一放在 Pinia store，持久化使用 pinia-plugin-persistedstate。
  - 请求返回统一结构 { code, message, success, data }，Axios 拦截器已兼容。
  - 公共组件与业务组件分目录存放，表单弹窗放在 views/admin/components 等子目录。
  - 使用 TypeScript DTO 定义（src/api/types.ts）保证请求响应类型安全。

  ## 11. 部署建议

  1. 后端：构建可执行 Jar，使用 systemd/Docker 管理进程；通过反向代理(如 Nginx) 暴露 https://your-domain/api/，并限制 uploads/ 访问权限。
  2. 前端：执行 pnpm build 后将 dist/ 发布到同一域名下（推荐同源部署以复用 /api 路径），或在代理层配置跨域头。
  3. 日志与监控：收集 logs/student-system.log，结合 ELK/Promtail；启用 Actuator 指标对接 Prometheus。
  4. 配置外部化：通过 --spring.config.additional-location 或环境变量覆盖数据库/Redis/JWT 密钥，避免敏感信息出现在仓库。
  5. 备份：定期备份数据库、Redis 缓存，头像等上传资源需备份或迁移至对象存储。

  ## 12. 质量与后续改进

  - 当前 src/test/java 空缺，建议补充服务层与控制器集成测试。
  - 可增加数据库建表脚本、Docker Compose 以便一键启动。
  - 建议对批量导入功能添加模板下载与失败提示国际化。
  - 考虑在刷新令牌逻辑中加入黑名单与多终端管理。
  - 可以接入前端 Sentry/后端 SkyWalking 收集异常。

  ## 13. 常用命令速查

  # 后端
  mvn clean package
  mvn spring-boot:run
  java -jar target/student-grade-system-1.0.0.jar --spring.profiles.active=prod

  # 前端
  pnpm dev
  pnpm build
  pnpm preview
