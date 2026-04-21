# Lottie 动画素材

考试页面「隐藏对话」模式下，中间区域会显示一个动画人物形象。

## 文件命名

将 Lottie JSON 文件放在本目录下，按以下名称命名：

| 文件名 | 状态 | 触发时机 |
|--------|------|----------|
| `teacher-idle.json` | 待机 | 空闲、判分中、考试结束 |
| `teacher-talking.json` | 说话 | TTS 播报考官台词时 |
| `teacher-listening.json` | 聆听 | 等待学生回答时 |

## 素材来源推荐

- [LottieFiles](https://lottiefiles.com/) — 搜索 `teacher`、`character talking`、`listening` 等关键词
- 建议选卡通风格，适合 7-12 岁儿童场景
- 下载 `.json` 格式（不是 `.lottie`）

## 无素材时

如果本目录下没有对应的 JSON 文件，组件会自动 fallback 到 CSS 动画角色（圆脸 + 眨眼 + 嘴巴动效），无需额外配置。
