# 简易商城 — 使用说明
- 王思敏 202230040164 23级计科一班 SCUT
**项目简介**
- Spring Boot + Thymeleaf + H2 的购物网站，包含注册/登录、购物车、下单、支付、订单列表等。
- 新增 WebSocket 实时通知与“实时销售监控”页面，支付成功后自动推送结构化消息。

**快速启动**
- 普通启动（JAR 模式）：
  - 运行 `start.bat 8080`（端口可自定义）。
  - 自动清理端口，检测 Java/Maven，首次会自动构建 JAR 并启动。
- 开发热更新：
  - 运行 `start-dev.bat 8080`。
  - 如检测到 Maven，将以 `mvn spring-boot:run` 方式启动（保存代码自动重启）；无 Maven 则回退到 classpath 启动。

**常用地址**
- 登录：`http://localhost:PORT/login`
- 注册：`http://localhost:PORT/register`
- 购物：`http://localhost:PORT/shop`
- 订单：`http://localhost:PORT/orders`
- 实时监控：`http://localhost:PORT/monitor`
- H2 控制台：`http://localhost:PORT/h2-console`

**实时通知与监控**
- 后端 WebSocket 端点：`/ws/orders`
  - 配置位置：`src/main/java/com/example/shop/websocket/OrderWebSocketConfig.java:14-16`
  - 处理器：`OrderWebSocketHandler`（维护会话并广播消息）
- 广播事件：
  - 文本事件：`ORDER_PAID:<id>`（支付页与订单页用于简单提示）
  - JSON 事件：`{"type":"SALE","orderId":..,"user":..,"total":..,"items":[...]}`（监控页用于展示明细）
  - 触发位置：`src/main/java/com/example/shop/controller/OrderController.java:48-54`
- 监控页（结构化展示）：`src/main/resources/templates/monitor.html`
  - 打开后自动连接 WebSocket，支付成功后插入一条“时间/用户/商品明细/总金额”的记录

**验证流程**
- 启动服务后：
  - 在浏览器 A 打开 `购物`，登录后加购并结算到 `支付`。
  - 在浏览器 B 打开 `实时监控`。
  - 在 `支付` 点击“支付”，约 2 秒后：
    - 支付页状态改为 `PAID` 并禁用按钮；
    - 监控页出现新交易记录（含用户名、商品与数量、总价）。

**技术与目录概览**
- 后端入口：`src/main/java/com/example/shop/ShopApplication.java`
- 控制器：`src/main/java/com/example/shop/controller/*`
- 服务层：`src/main/java/com/example/shop/service/*`
- 仓库：`src/main/java/com/example/shop/repo/*`
- WebSocket：`src/main/java/com/example/shop/websocket/*`
- 模板页：`src/main/resources/templates/*`
- 样式：`src/main/resources/static/css/app.css`
- 启动脚本：`start.bat`、`start-dev.bat`

**注意事项**
- 首次启动可能需要联网下载依赖；若 Maven 不可用，普通启动脚本会尝试使用 classpath 回退运行。
- DevTools 热更新仅在 `spring-boot:run` 或 classpath 运行时有效。
