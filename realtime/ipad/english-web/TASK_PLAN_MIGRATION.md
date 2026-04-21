# english-web 迁移任务计划（Phase 1-2 已启动）

## 目标
- 将 `realtime/h5` 核心能力迁移到 `realtime/ipad/english-web`。
- 后端接口保持不变（Yudao `/app-api/*` + `/py/*`）。
- 首批上线 iPad/手机可用、视觉精致、操作顺手的 MVP 链路：
  `登录 -> 首页 -> YLE选书 -> Flyers考试`。

## 里程碑

### Phase 1 基础架构（当前执行）
- 建立 API 请求层（axios + 统一拦截 + Yudao 响应解包）。
- 建立鉴权基础（token 存储、401 清理、登录跳转）。
- 建立全局状态层（zustand：用户 token / 登录状态）。
- 建立移动端设计基础变量（颜色、圆角、间距、动效节奏、断点）。

验收标准：
- 能正确携带 `Authorization` 与 `tenant-id` 请求头。
- 后端返回 `code !== 0` 时统一报错处理。
- token 丢失或过期时可回跳登录页。

### Phase 2 登录注册（当前执行）
- 实现登录/注册双模式切换。
- 实现三通道：邮箱、账号、手机。
- 对齐旧版接口：
  - `email-login / email-register / username-login / username-register`
  - `sms-login / send-sms-code / send-email-code`
  - `check-email / check-username / check-mobile`
- 支持验证码倒计时、基础校验、错误提示、登录后 redirect 回跳。

验收标准：
- 三通道登录可成功获取 token 并落库。
- 注册链路可走通（包含查重、验证码发送）。
- 登录后可跳转回原受保护页面。

### Phase 3 首页与YLE选书（下一阶段）
- 首页应用入口、用户菜单、额度弹层。
- `exam-series/list-all-simple` 接入与级别/系列选择页。

### Phase 4 Flyers考试主流程（下一阶段）
- 题库加载、考试步骤驱动、判题反馈。
- 语音链路：ASR/TTS/录音状态/重播。

### Phase 5 次级页面（下一阶段）
- `promo/logout` 迁移。
- `checkout`（mock 支付）与 `dictation/primary`（可先占位）。

## 风险与对策
- Next.js 版本变化导致 API 细节差异：以本地 `next/dist/docs` 为准实现。
- 移动端录音与自动播报受浏览器策略限制：先保证交互可退化，再优化体验。
- 旧版流程复杂（flyers）：先实现文本闭环，再补语音与动效。

## 分支建议
- `feat/foundation-auth-phase1-2`：基础架构与登录注册
- `feat/home-yle-phase3`
- `feat/flyers-phase4`

