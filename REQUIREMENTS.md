# English Speech Coach — 需求文档

> 状态：v0.4 定稿（待评审）
> 更新日期：2026-04-07
> 项目代号：kugua（自研模块前缀，yudao 原模块不动）

## 1. 项目目标

将现有 `realtime/web/index.html`（单页 HTML + Python aiohttp 代理）的 English Speech Coach 改造为可由管理员管理题库、可由学生在 H5 上练习的完整系统。首批面向剑桥 Flyers（A2，7-12 岁），后续扩展 KET（A2）/ PET（B1）。

两种练习模式保留：
- **Free Talk**：自由对话 + 实时 ASR + LLM 反馈
- **Exam Practice**：剑桥口语模拟，**多级别**支持（Flyers 4 部分 / KET 2 部分 / PET 4 部分）

## 2. 系统组成

```
┌──────────────────┐    ┌───────────────────────┐
│  管理后台 H5      │    │  学生 H5              │
│  vben-admin (PC)  │    │  Vue3+Vite+Vant (移动) │
└────────┬─────────┘    └───────────┬───────────┘
         │ JWT                       │ JWT
         ▼                           ▼
┌────────────────────────────────────────────┐
│  yudao-server (Spring Boot, MySQL)         │
│  - yudao-module-english (新增)              │
│  - 题库 / 班级 / 学生 / 练习记录 CRUD       │
│  - 用户体系、JWT 签发、多租户、文件服务     │
└────────────────────┬───────────────────────┘
                     │ 共享 JWT (HS256)
                     ▼
┌────────────────────────────────────────────┐
│  Python aiohttp (server.py，保留)           │
│  - /llm/chat、/llm/judge（OpenAI/Claude/Ollama）│
│  - /asr（sherpa-onnx 容器，端口 6006-6009） │
│  - /tts/*（Edge-TTS / Piper / VibeVoice）   │
│  - 验 yudao JWT、写调用日志、按 userId 限流  │
└────────────────────────────────────────────┘
```

**已部署**：
- 后台服务端：`realtime/admin/service/`（yudao-vue-pro）
- 后台前端：`realtime/admin/web/`（vben-admin）
- ASR 容器：`sherpa-rt`，端口 6006-6009
- Python 代理：`realtime/web/server.py`

**待新建**：
- 业务模块：`kugua-module-english`（二开模块统一用 `kugua-` 前缀，与 yudao 原生模块隔离；yudao 自身代码完全不动，便于将来跟随 yudao 升级合并）
- Java 包名：`cn.kugua.module.english.*`（独立于 yudao 命名空间）
- 启动类需要追加 `scanBasePackages = {"cn.iocoder.yudao", "cn.kugua"}`，MyBatis Mapper 扫描 / Spring Security 配置 / Knife4j 扫描包同步追加

**学生 H5 工程**：已基于 [vue-zone/vue3-vant-mobile](https://github.com/vue-zone/vue3-vant-mobile) 安装在 `realtime/h5/`（v3.15.0），等清理 i18n / Mock / 示例后开始平移业务。

## 3. 已确认需求

### 3.1 题库存储（v0.4 重构：Option U 多态 + 全拆 JSON）

题库改为关系型表存储，使用 yudao 自带文件服务（`infra-file`）存放图片资源。表前缀统一 `esc_`，与 yudao 共库。详见 `sql/kugua-module-english/esc_init.sql` v2（共 24 张表）。

**核心设计**：

1. **Option U 多态题型**：一套统一的 part 头表 `esc_exam_part`，用 `part_type` 字段区分子表，避免每加一个级别就新建一组独立表。Flyers / KET / PET 可共享题型（如 `personal_qa` 同时被 Flyers Part4 和 KET Part2 使用）。
2. **全拆 JSON**：原 v1 设计中的 `differences` / `hint_keywords` / `follow_up_questions` / `score_detail` / `llm_score` 全部拆为结构化字段或子表，便于后台单字段维护。
3. **级别字典**：`esc_exam_level`（flyers/ket/pet）+ `esc_exam_part_type`（find_diff/info_exchange/tell_story/personal_qa/collab_task/long_turn_photo/general_convo）作为枚举元数据。
4. **V2 版本化**：`exam_code` 跨版本稳定，`version` 递增，`is_active` 标识当前生效版本。练习记录直接持有 `exam_id`（某行主键）作为快照。
5. **混合多租户**：`tenant_id=0` 表示公共题库，`>0` 表示租户私有。

**24 张表分组**：
- 题库公共 (4)：`esc_exam_level` / `esc_exam_part_type` / `esc_exam` / `esc_exam_part`
- 题型子表 (13)：find_diff (2) / info_exchange (2) / tell_story (1) / personal_qa (2) / collab_task (2) / long_turn_photo (2) / general_convo (2)
- 班级学生 (3)：`esc_class` / `esc_class_teacher` / `esc_student`
- 练习记录 (3)：`esc_practice_session` / `esc_practice_part_score` / `esc_practice_answer`
- 调用日志 (1)：`esc_ai_call_log`

**LLM 评分维度**（剑桥口语标准，所有 part_score / answer 行均含以下列）：
- `score_grammar_vocab` 语法词汇
- `score_pronunciation` 发音
- `score_interaction` 互动
- `score_discourse` 篇章组织（PET 及以上才打）
- `score_overall` 综合分
- `feedback_text` / `corrected_text` / `llm_raw_response`

各维度均可为 NULL，表示该题型 / 该级别不评此项。

### 3.2 用户体系

- **学生表独立建**：`esc_student`，不复用 `system_users` 也不复用 `yudao-module-member`。
- **登录方式**：短账号（如 `flyers001`）+ 密码，由管理员批量创建/分配，老师可重置密码。
- **班级**：`esc_class` 表，仅作为分组用（本期不做"作业 / ddl"概念），学生归属班级。
- **角色**：4 级 → 超管 / 校管 / 老师 / 学生（详见 3.8）。
- **JWT**：学生登录走 kugua 自定义 Provider，签发与系统一致的 JWT（HS256），令牌中携带 `userId / username / tenantId / userType=student`。
- **账号生命周期**：软删除（`deleted=1`），历史练习记录保留不动，用户名释放。

### 3.3 多租户

- 启用 yudao 多租户。租户 = 学校 / 机构。
- 业务表统一加 `tenant_id`，所有查询走 yudao 租户拦截器。
- 题库、班级、学生、练习记录均按租户隔离（题库是否跨租户共享见 4.2）。

### 3.4 Python 代理服务的接入

- 采用方案 **P1：共享 JWT**。
- Python 端用 `PyJWT` 验签 yudao 颁发的 token，从 token 中取出 `userId / tenantId`。
- 未携带或验签失败的请求一律 401。
- **每日配额**：按 `userId` + 接口类型 限额，**初始统一 100 次/天/学生**（LLM、ASR、TTS 各自独立计数），数值通过配置项 `kugua.english.quota.{llm,asr,tts}.daily` 可调，先写死，后续接管理后台。
- **超限策略**：硬拦截，返回 429（不做降级到本地模型）。
- **租户总额**：暂不做（v0.2 不引入租户级总配额）。
- **管理员配额**：超管 / 校管 / 老师不限。
- 调用日志写入 MySQL（`esc_ai_call_log`：userId、tenantId、接口、模型、prompt/completion token 数、耗时、status、时间），便于成本追溯。

### 3.5 练习记录

- 学生每次完成 Exam 模式都要存库。Free Talk **不存档**。
- 存储粒度（v0.4 全拆字段）：
  - `esc_practice_session`：一次完整考试为一条（含 `final_overall_score` / `final_comment`）
  - `esc_practice_part_score`：每个 part 一条聚合分（5 维分数 + comment）
  - `esc_practice_answer`：每道题为一条（ASR 文本 + 5 维分数 + feedback/corrected/raw 三列，全部结构化无 JSON）
- **历史评分先不展示**（先存数据，UI 后期再做）。
- 不做 LLM judge 评分的统计 / 报表。
- 不存学生录音文件（只走一次 ASR 就丢，节省存储 + 隐私友好）。

### 3.6 学生 H5 技术栈

- 基于 [vue-zone/vue3-vant-mobile](https://github.com/vue-zone/vue3-vant-mobile) 模板：Vue3 + Vite + TypeScript + Vant4 + Pinia + Vue Router + axios + UnoCSS。
- 复用现 `index.html` 中的录音 / 流式 ASR / 流式 LLM / TTS 播放逻辑（先平移，再重构）。
- 目标设备：手机浏览器（iOS Safari / Chrome / 微信内置 WebView）。
- 微信小程序：以后再说，本期不做 Uni-app/Taro 折中。
- **进项目第一件事的清理工作**：删除或裁剪模板自带的 i18n、Mock 默认拦截、示例页面，把 axios baseURL 指向 yudao + Python。

### 3.7 部署

- yudao 后端、vben 后台已部署，本期不动。
- ASR / TTS / LLM 代理沿用 `realtime/web/server.py` + `sherpa-rt` 容器。
- 学生 H5 **本期只走 Vite 开发端口**（如 `http://localhost:5173`），不上独立域名、不做 PWA、不做反向代理；上线方案后期再议。
- HTTPS 仅在 yudao 与 Python 代理侧保留（getUserMedia 在生产环境必须 HTTPS，开发期 localhost 例外）。

### 3.8 角色权限矩阵

| 角色 | 跨租户 | 题库 | 班级 | 学生 | 老师 | 校管 | 练习记录 |
|---|---|---|---|---|---|---|---|
| **超管** | 全部 | 公共 + 任意私有 | 任意 | 任意 | 任意 | 任意 | 任意 |
| **校管** | 仅本校 | 本校私有题库（不能改公共） | 本校所有班 | 本校所有 | 本校所有 | ❌ | 本校所有（暂不展示 UI） |
| **老师** | 仅本校 | 只读 | 仅自己班 | 仅自己班 | ❌ | ❌ | 仅自己班（暂不展示 UI） |
| **学生** | ❌ | 只用 | ❌ | 仅自己 | ❌ | ❌ | 仅自己（暂不展示 UI） |

权限拦截通过 yudao 自带的 `@PreAuthorize` + 数据权限规则实现。

## 4. 班级 / 老师关系（v0.3 定稿）

- 一个班可以有 **多个老师**（多对多）。
- 一个老师可以带 **多个班**（多对多）。
- 学生 **不能** 同时在多个班（一对多：班级 → 学生）。
- 老师调岗：**直接改归属**，不需要先解绑（从 `esc_class_teacher` 关联表里删一行加一行即可，无侧效应）。
- 涉及表：
  - `esc_class`：班级主表（id、name、tenant_id、grade、created_at...）
  - `esc_class_teacher`：班级 ↔ 老师 多对多关联表
  - `esc_student.class_id`：学生属于唯一班级（外键直引）

## 5. 校管 / 租户元信息（v0.3 定稿）

- **校管由超管创建**。
- 一个租户内 **校管数量不限制**。
- 校管可以修改本租户的元信息（学校名称、logo 等），但不能修改租户的"启用状态"和"过期时间"——这些仍由超管掌控。
- 涉及表：复用 yudao 的 `system_tenant`，校管对该表只允许 update name/logo 等"展示型"字段，权限通过 yudao 自带的字段级权限或 service 层校验实现。

## 6. 内容审核（预留，本期不做）

- 仅在 LLM system prompt 中加儿童安全约束（最低成本预留）。
- ASR 文本过滤、家长回看：v0.4 之后再议。

---

## 7. 下一步执行计划

1. ✅ v0.3 需求定稿
2. **接下来**：出 `kugua-module-english` 完整 SQL 建表脚本（所有 `esc_` 表）供用户审。
3. 出工程骨架清单：
   - `kugua-module-english` 目录结构、pom 改动、yudao-server 启动配置改动
   - `realtime/h5/` 模板清理 checklist（删 i18n / 关 Mock 默认拦截 / 删示例页 / 改 axios baseURL）
   - `realtime/web/server.py` 的 JWT 中间件和配额中间件改动点
4. 数据迁移脚本：把 `realtime/web/question-bank.json` 17 套题导入 MySQL。
5. 学生 H5 平移业务（先把 Free Talk + Exam 的 UI 和录音/ASR/LLM/TTS 平移过去，先不重构）。

## 8. 变更历史

- **v0.1** 2026-04-07 草稿，列出 10 个开放问题
- **v0.2** 2026-04-07 题库结构 / 用户体系 / 配额 / 角色矩阵 / 部署方式 / 学生 H5 模板敲定，剩 3 个开放问题
- **v0.3** 2026-04-07 Java 包名 / 班级老师关系 / 校管范围敲定，准备进入 SQL 设计和工程骨架阶段
- **v0.4** 2026-04-07 题库 SQL 重构：Option U 多态题型支持 Flyers/KET/PET 多级别；所有 JSON 字段拆为结构化字段或子表；引入剑桥口语 5 维评分列；24 张表落地于 `sql/kugua-module-english/esc_init.sql`
