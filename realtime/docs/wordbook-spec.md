# 「我的生词本」功能需求规格 v1

> 状态：**P1 已编码完成**（不含自动生成）。P2 待做。
> 关联：`kugua-module-english` 词汇模块、`ipad/english-web` 学员端。
>
> **P1 实现说明**：为不破坏已有「按主题练习」的随机入队（`/enroll-new`），新增了独立的
> `/enroll-wordbook` 入队路径，而非改造 `enrollNewWords`。词汇首页默认走生词本入队，
> 主题页仍走随机入队。前端 `tsc --noEmit` 已通过；后端因本机 Maven 3.5.4 < 插件要求的
> 3.6.3 无法在此环境编译验证（工具链问题，非代码问题），需用 Maven ≥3.6.3 构建。

## 一句话流程

家长输入不会的单词 → 校验/自动生成词条 → 进「我的生词本」待学池 → 每天自动放 100 新词 → 孩子用 SRS 抽认卡背诵。

---

## 关键设计决策（已锁定）

| 决策点 | 结论 |
|--------|------|
| 生词本与 SRS 关系 | **待学池模式**：加词只进生词本，每天从池中取 ≤100 入 SRS |
| 缺词处理 | **自动 AI 生成词条入库**（放 P2） |
| 生词本数量 | **单一默认生词本**，用户不建多列表 |
| 生成词审核 | **直接发布可用**，不走人工审核 |
| 生成同步性 | **异步**：先入池占位，后台补内容 |
| 开发节奏 | **P1 先做（仅库内词），P2 补自动生成** |

---

## 现状盘点（已有能力，直接复用）

| 能力 | 现状 | 位置 |
|------|------|------|
| 词库（词条详情/例句/发音/难度/主题） | ✅ admin 可增删改查 + 批量导入 + AI 生成词条 | `VocabController` |
| SRS 艾宾浩斯复习（1/2/4/7/15/30 天） | ✅ 记忆/遗忘 → 自动排下次复习 | `UserVocabProgressServiceImpl` |
| 每日新词上限 100 | ✅ `DAILY_NEW_WORD_CAP = 100` | 同上 |
| 今日复习队列 | ✅ `GET /today-review` 按到期时间出队 | `AppVocabController` |
| 学新词入队 | ⚠️ 有，但从整个词库**随机抽**，需改造为优先出生词本 | `enrollNewWords` |
| 我的词库（自建命名列表 + 加/删词） | ✅ 后端 API 全有，前端入口灰着「即将开放」 | `AppUserVocabListController` |
| 按词搜索词库 | ❌ 只有按级别/主题/难度浏览，**缺按词查** | — |
| AI 生成词条内容（释义/IPA/例句） | ✅ 仅生成内容，新词还需先建 `VocabDO` 行 | `PyVocabClient.generateContent` |

---

## 数据模型（复用为主）

- **生词本**：复用 `UserVocabList`，每用户懒创建一条默认列表 `source='wordbook'`。前端不暴露「新建列表」。
- **待学池状态**：生词本的 `UserVocabListItem` 即池子。一个词「是否已进 SRS」由 `UserVocabProgress` 是否存在判定，**不加新字段**。
- **用户生成词**：写入共享 `esc_vocab`，建议加 `source='user_gen'`。
  - ⚠️ **多租户债**：`esc_vocab` 为共享表，用户生成词会跨租户可见。P2 开工前需定：可接受共享，还是按租户隔离。P1 不涉及。

---

## 每日 100 新词逻辑

- **新词节奏**：加词只进生词本，不立刻进 SRS。每天首次进词汇页时，自动从**待学池里还没进 SRS 的词**取 `min(100 − 今日已入队, 剩余)` 个入队（`status=0`）。`DAILY_NEW_WORD_CAP=100` 现成。
- **复习节奏**：`today-review` 取到期词，词汇页默认取 100。
- 池子剩余词第二天继续，不会一次压垮孩子。
- 首页卡片显示 **今日 新词 X · 复习 Y**，一键开始（复用 `/vocab/review`）。

---

## 接口清单

| 接口 | 说明 | 状态 |
|------|------|------|
| `GET /english/vocab/search?word=&level=` | 按词前缀/模糊查，联想补全 + 校验存在 | 🆕 P1 |
| `GET /english/vocab/wordbook` | 生词本列表（含待学/已学状态、词数） | 🆕 P1 |
| `POST /english/vocab/wordbook/add` | 加词到默认生词本（P1 仅接受库内 vocabId） | 🆕 P1 |
| `DELETE /english/vocab/wordbook/{vocabId}` | 从生词本移除（复用 `removeItem`） | ♻️ P1 |
| `POST /english/vocab/enroll-new` | **改造**：优先从生词本待学池出词，池空提示（P1 不随机兜底） | ♻️ P1 |
| `GET /english/vocab/today-review` | 今日复习队列（新+复习） | ✅ 已有 |
| `POST /english/vocab/{id}/review` | 提交背诵结果 | ✅ 已有 |
| `POST /english/vocab/wordbook/generate`（内部） | 建 `VocabDO` 行 → 异步 `generateContent` 回填 | 🆕 P2 |

---

## P1 任务清单（仅库内词，可完整跑通）

### 后端
1. `GET /vocab/search?word=&level=` — `VocabMapper` 加 `word LIKE`，返回候选做联想 + 校验。
2. 用户默认生词本：首次访问懒创建 `UserVocabList(source='wordbook')`；实现 `GET /wordbook`、`POST /wordbook/add`（仅接受库内 vocabId）、`DELETE /wordbook/{vocabId}`。
3. 改造 `enrollNewWords`：出词来源改为**优先生词本待学池**；池空**不随机兜底**，返回提示「生词本没词了，去添加」。

### 前端（`ipad/english-web`）
4. 点亮词汇首页灰掉的「我的词库」入口 → 改名「我的生词本」。
5. 生词本页：输入框 + 联想补全下拉（调 search）+ 词列表（待学/学习中/已掌握）+ 删除。
6. 首页今日卡片文案对齐「新词 X · 复习 Y」；「学新词」按钮改为从生词本出词。

### 验收标准
家长输入库内单词加入生词本 → 当日/次日自动进 ≤100 新词 → 孩子抽认卡背诵 → 记忆/遗忘驱动 SRS 复习。

---

## P2（后续）

- 自动生成缺词入库：建 `VocabDO`（`level=当前级别`、`difficulty` 默认中等、`source='user_gen'`、`status=published`）→ 异步 `generateContent` 回填释义/IPA/例句 → 发音走懒生成。
- 归一化（小写/去空格）+ 再查一次防重复。
- 生成失败：词条留空占位，标记待补，不阻塞入池。
- 批量粘贴多词逐个校验；听音拼写、造句练习等已有占位入口。
- 前置：解决 `esc_vocab` 共享表多租户可见问题。
