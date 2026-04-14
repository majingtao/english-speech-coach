# English AI Speech Coach

帮助孩子（剑桥 Flyers A2，7-12 岁）练习英语口语的 AI 教练。

## 功能模块

| 页面 | 地址 | 说明 |
|------|------|------|
| 主页面 | `https://<IP>:8443/index.html` | Free Talk 自由对话 + Exam Practice 模拟考试 |
| 听写练习 | `https://<IP>:8443/dictation.html` | 单词听写、LLM 自动释义、听写记录追踪 |

## 快速启动

### 1. 启动 ASR 语音识别服务（Docker）

```bash
cd realtime

# GPU 模式（推荐，需要 NVIDIA 显卡 + Docker GPU 支持）
./start-asr.sh

# CPU 模式（无显卡时的降级方案，识别会较慢）
./start-asr.sh cpu

# 停止 ASR 服务
./stop-asr.sh
```

ASR 服务启动后会监听以下端口：

| 端口 | 模型 | 类型 | 说明 |
|------|------|------|------|
| 6006 | Zipformer EN | 流式 | 实时识别（130MB） |
| 6007 | Zipformer EN Large | 流式 | 实时识别-大模型（300MB） |
| 6008 | Whisper Medium EN | 离线 | 录完后识别（1.5GB） |
| 6009 | SenseVoice Small | 离线 | 中英双语识别（230MB） |

### 2. 启动 Ollama + Open WebUI（Docker）

```bash
docker compose up -d
```

- Ollama：`localhost:11434`（本地 LLM 推理）
- Open WebUI：`http://localhost:3000`（Web 聊天界面）

### 3. 启动 Web 应用

```bash
cd realtime/web
python server.py
```

启动后访问 `https://<你的IP>:8443`（首次访问需接受自签名证书）。

## 页面使用说明

### 主页面（index.html）

#### Free Talk 自由对话

1. 选择 LLM 模型（本地 Ollama 或云端 OpenAI/Claude）
2. 选择 ASR 模型（推荐 SenseVoice Small，支持中英文）
3. 选择 TTS 引擎和声音
4. 点击 **Start** 开始对话，说英语后 AI 会给出纠正和反馈
5. 点击 **Replay** 重听反馈内容

#### Exam Practice 模拟考试

1. 选择试卷（17 套剑桥 Flyers 真题）
2. 选择起始部分（全部 / Part 1-4 单独练习）
3. **Voice Only** 模式：勾选后隐藏对话文字，模拟真实口语对话（只有语音交互）
4. 点击 **Start Exam** 开始
5. 考官语音提问 → 孩子用麦克风回答或打字回答 → AI 判分
6. 答错时会自动播放正确答案的发音，帮助孩子跟读
7. 考试结束显示统计：正确数、重试数、看答案数

**考试题型：**
- Part 1：Find the Differences（看图找不同）
- Part 2：Information Exchange（信息交换问答）
- Part 3：Tell the Story（看图讲故事）
- Part 4：Personal Questions（个人问题）

### 听写练习（dictation.html）

1. **添加单词**：输入英文单词（支持批量，每行一个），或点击 **Import Flyers 354** 导入预置的 354 个核心词
2. **查看释义**：点击单词名称，LLM 自动生成中文释义 + 中英对照例句（首次需等待，之后缓存）
3. **播放发音**：点击喇叭按钮播放单词发音（使用当前 TTS 设置）
4. **听写标记**：每个单词有四个按钮
   - **Know meaning**：认识中文意思
   - **Don't know**：不认识
   - **Can spell**：能正确拼写
   - **Can't spell**：不能拼写
5. **统计追踪**：显示每个单词的练习次数和通过率
6. **编辑/删除**：可修改释义例句或删除单词

## 配置

### 环境变量（realtime/web/.env）

```bash
# LLM API Keys（至少配一个云端 key，或使用本地 Ollama）
OPENAI_API_KEY=sk-xxx
ANTHROPIC_API_KEY=sk-ant-xxx
MOONSHOT_API_KEY=xxx
OPENROUTER_API_KEY=xxx

# 代理（访问 OpenAI/Claude 时使用）
HTTP_PROXY=http://127.0.0.1:1080

# VibeVoice TTS（可选，需要本地模型）
ENABLE_VIBEVOICE_TTS=true
VIBEVOICE_DEVICE=auto
```

### TTS 引擎选择

| 引擎 | 说明 | 需要 |
|------|------|------|
| Edge-TTS | 微软在线语音，音质好 | 联网 |
| Piper | 本地轻量 TTS | sherpa-onnx + 模型文件 |
| VibeVoice | 本地高质量 TTS | GPU 推荐，CPU 较慢 |
| System | 浏览器内置语音 | 无额外依赖 |

### ASR 模型选择建议

| 场景 | 推荐模型 | 原因 |
|------|----------|------|
| 考试模式 | SenseVoice Small | 中英双语，识别准 |
| 自由对话（实时） | Zipformer EN | 流式识别，延迟低 |
| 自由对话（准确） | Whisper Medium EN | 英文专用，准确率高 |

## 目录结构

```
englishAi/
├── realtime/
│   ├── web/
│   │   ├── server.py              # HTTPS 服务器（API 代理 + 静态文件）
│   │   ├── index.html             # 主页面（Free Talk + Exam）
│   │   ├── dictation.html         # 听写练习页面
│   │   ├── question-bank.json     # 17 套考试题库
│   │   ├── flyers354-words.json   # 354 个核心单词
│   │   ├── .env                   # API Keys（不提交）
│   │   └── prompts/               # LLM 系统提示词
│   ├── models/                    # ASR 模型文件
│   ├── start-asr.sh               # ASR 容器启动脚本
│   └── stop-asr.sh                # ASR 容器停止脚本
├── docker-compose.yaml            # Ollama + Open WebUI
├── audio/
│   └── VibeVoice-Realtime-0.5B/   # VibeVoice 模型
├── assets/
│   └── vibevoice/voices/          # TTS 声音样本
└── vibevoice_repo/                # VibeVoice 源码
```

## 常见问题

**Q: 首次访问提示证书不安全？**
A: server.py 使用自签名证书，在浏览器中点击"继续访问"即可。

**Q: ASR 识别很慢？**
A: CPU 模式下 SenseVoice 处理 4 秒音频约需 20-30 秒。用 `./start-asr.sh` 开启 GPU 模式可大幅加速。

**Q: Judge 判分超时？**
A: 使用云端 LLM（OpenAI/Claude）判分需要联网。超时时会自动跳过该题继续。本地 Ollama 不依赖网络但速度取决于硬件。

**Q: 听写页面 LLM 返回失败？**
A: 确认 LLM 模型选择正确且服务可用。失败后可重新点击单词重试。浏览器 Console（F12）可查看详细错误。

**Q: 手机访问页面没声音？**
A: 移动端浏览器要求用户交互后才能播放音频。先点击任意按钮，之后 TTS 就能正常播放。

## 管理后台实施计划（Refine + Ant Design + Supabase）

### 结论
采用 `Refine + Ant Design + Supabase`，不再引入 `Ant Design Pro`。

### 目标范围（V1）
- 后台模块：题库管理、用户管理、机构管理、订阅与订单管理。
- 权限模型：平台管理员、机构管理员、教师、学生。
- 数据能力：多租户隔离、审计字段、可追踪的内容变更。

### 分阶段计划

#### Phase 0：项目初始化（1-2 天）
- 创建 Refine 项目（建议 `Next.js + Ant Design`）。
- 接入 Supabase（`auth + database + storage`）。
- 建立基础布局：登录页、Dashboard、左侧菜单、404。
- 输出开发规范：环境变量、目录结构、命名规则。

#### Phase 1：数据模型与多租户（2-3 天）
- 设计核心表：`orgs`、`users_profile`、`memberships`、`question_banks`、`questions`、`attempts`、`subscriptions`、`payments`。
- 所有业务表加入 `org_id`、`created_by`、`created_at`、`updated_at`。
- 配置 Supabase RLS 策略，确保按 `org_id` 隔离。
- 增加基础索引与唯一约束，防止重复数据。

#### Phase 2：后台 CRUD 页面（4-6 天）
- 题库管理：列表、筛选、创建、编辑、发布状态。
- 用户管理：账号列表、角色变更、机构绑定。
- 机构管理：机构信息、套餐状态、配额统计。
- 订单管理：支付记录、状态流转、导出。

#### Phase 3：权限与审计（2-3 天）
- Refine 路由级权限控制（菜单可见性 + 页面访问）。
- 操作级权限控制（按钮级：创建/编辑/删除/发布）。
- 审计日志：关键操作写入 `audit_logs`。

#### Phase 4：AI 业务接入（3-5 天）
- 后台配置 AI 参数：模型、费用、重试策略、超时。
- 打通练习记录回流：口语评测结果与题库关联。
- 增加数据看板：活跃用户、练习次数、机构使用量。

### 建议目录（示例）
```text
admin/
  src/
    app/
    pages/
      question-banks/
      users/
      orgs/
      subscriptions/
      payments/
    providers/
    hooks/
    utils/
  .env.local
```

### 里程碑与验收
- M1：可登录 + 菜单可用 + Supabase 联通。
- M2：题库/用户/机构三大 CRUD 跑通。
- M3：多租户 RLS 生效，跨机构不可见。
- M4：订单与订阅可追踪，可支持 SaaS 运营。

### 风险与控制
- 风险：RLS 配置错误导致数据越权。
- 控制：每张表配套最小权限策略 + 用例验证。
- 风险：AI 调用成本不可控。
- 控制：按机构配置预算与限流。

### 下一步协作（请按顺序执行）
1. 我先为你落地 `Phase 0`：初始化 Refine 管理后台骨架。
2. 你确认部署方式：`Vercel`（推荐）或自托管。
3. 我再产出 Supabase 首版建表 SQL + RLS 策略（可直接执行）。
4. 最后开始做第一批页面：题库、用户、机构。

### Phase 0 落地状态（2026-04-13）
- 后台骨架已创建：`realtime/admin-new`
- 技术栈已固定：`Refine + Ant Design + Supabase + Next.js`
- 运行方式（自托管）：在 `realtime/admin-new` 执行 `npm run dev`（开发）/ `npm run build && npm run start`（生产）
