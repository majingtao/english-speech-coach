# Realtime English Speech Coach

浏览器麦克风 → sherpa-onnx ASR（多模型可选）→ Ollama 流式批改 → 页面实时展示 + 语音朗读（Edge-TTS / Piper / 系统）

## 架构

```
Browser (localhost:8000 or https://<LAN-IP>:8443)
  ├─ 麦克风采集 (Float32 / 16kHz)
  ├─ ASR 模型选择下拉框（流式 / 离线）
  ├─ 流式模型：WebSocket 实时识别
  ├─ 离线模型：录音 → POST 整段音频 → 返回识别结果
  ├─ fetch → Ollama /api/chat → 流式显示反馈
  └─ TTS 朗读（Edge-TTS 云端 / Piper 本地 / 浏览器系统）

server.py (HTTPS 反向代理 + TTS 服务)
  ├─ /ws?model=xxx     → 路由到对应流式 ASR 端口 (6006/6007)
  ├─ /asr?model=xxx    → 离线 ASR，POST 音频返回文本 (6008/6009)
  ├─ /asr/models       → 返回 ASR 模型列表
  ├─ /api/*            → 转发 HTTP 到 Ollama (:11434)
  ├─ /tts              → Edge-TTS 或 Piper TTS
  ├─ /tts/voices       → 返回声音列表
  └─ /*                → 静态文件 (index.html)

sherpa-onnx (Docker: sherpa-rt, ports 6006-6009)
  ├─ 6006: Zipformer EN 2023-06-26 (streaming, 130MB)
  ├─ 6007: Zipformer EN 2023-06-21 (streaming, 300MB)
  ├─ 6008: Whisper Medium EN (offline, 1.5GB)
  └─ 6009: SenseVoice Small (offline, bilingual zh-en, 230MB)

Ollama (Docker: ollama, port 11434)
  └─ qwen2.5:3b
```

## 前置条件

- Docker 已安装
- Ollama 已运行，`qwen2.5:3b` 模型已拉取
- ASR 模型文件已下载到 `models/` 目录
- Windows 主机安装依赖：`pip install aiohttp edge-tts websockets sherpa-onnx numpy`

## 目录结构

```
realtime/
├─ models/
│  ├─ sherpa-onnx-streaming-zipformer-en-2023-06-26/  # 流式小模型
│  ├─ sherpa-onnx-streaming-zipformer-en-2023-06-21/  # 流式大模型
│  ├─ sherpa-onnx-whisper-medium.en/                  # 离线 Whisper
│  ├─ sherpa-onnx-sense-voice-zh-en-ja-ko-yue-2024-07-17/  # 离线 SenseVoice
│  └─ vits-piper-en_US-amy-low/                       # Piper TTS 模型
├─ repo/              # sherpa-onnx 官方仓库 (git clone)
├─ web/
│  ├─ index.html      # 前端页面（自适应 PC / 手机）
│  └─ server.py       # HTTPS 反向代理 + TTS 服务
├─ start_asr.sh       # Docker 内 ASR 批量启动脚本
└─ README.md
```

## 启动步骤

### 1. 启动 Ollama

```powershell
docker compose -f E:\project\englishAi\docker-compose.yaml up -d
```

确认 Ollama 正常：

```powershell
curl http://localhost:11434/api/tags
```

### 2. 启动 sherpa-onnx ASR 服务

**如果容器已存在但停了：**

```powershell
docker start sherpa-rt
docker exec sherpa-rt bash /workspace/start_asr.sh
```

**如果容器不存在，重新创建（映射 4 个端口）：**

```powershell
docker run -d --name sherpa-rt ^
  -p 6006:6006 -p 6007:6007 -p 6008:6008 -p 6009:6009 ^
  -v E:\project\englishAi\realtime:/workspace ^
  python:3.10 bash -c "tail -f /dev/null"

docker exec sherpa-rt pip install sherpa-onnx websockets numpy -i https://pypi.tuna.tsinghua.edu.cn/simple
```

启动 4 个 ASR 服务：

```powershell
docker exec sherpa-rt bash /workspace/start_asr.sh
```

`start_asr.sh` 会在后台启动 4 个进程（端口 6006-6009），看到日志即启动成功。

### 3. 启动前端服务

```powershell
cd E:\project\englishAi\realtime\web
python server.py
```

首次运行会自动生成自签名证书（需要系统已安装 openssl）。

**访问方式：**

- 本机：`http://localhost:8000`（HTTP 简单文件服务）或 `https://localhost:8443`
- 局域网 / 手机：`https://<你的电脑局域网IP>:8443`

> 浏览器会提示证书不安全 → 点"高级" → "继续访问"即可。

## 使用方法

### ASR 模型选择

页面顶部 **ASR Model** 下拉框可选择识别模型：

| 模型 | 类型 | 特点 |
|------|------|------|
| Zipformer EN (130MB) | 流式 | 实时字幕，说一句识别一句，默认推荐 |
| Zipformer EN Large (300MB) | 流式 | 实时字幕，准确率更高 |
| Whisper Medium EN (1.5GB) | 离线 | 录完整段后识别，英文准确率最高 |
| SenseVoice Small (230MB) | 离线 | 录完整段后识别，支持中英文 |

**流式模型：** Start → 实时显示识别文字 → 自动断句 → 触发批改

**离线模型：** Start → 录音（显示 Recording...）→ Stop → 发送整段音频 → 显示识别结果 → 触发批改

### 语音批改

1. 点击 **Start** → 浏览器弹出麦克风授权 → 允许
2. 对着麦克风说英文
3. **Realtime** 区域显示识别中的文字
4. 断句后自动调用 Ollama，**Coach feedback** 区域流式显示批改结果
5. 点击 **Stop** 停止录音

### TTS 语音回复

勾选 **Voice reply** 复选框，Ollama 回复完毕后自动朗读英文部分。

**TTS 引擎选择（Engine 下拉框）：**

| 引擎 | 特点 |
|------|------|
| Edge-TTS | 微软云端，音质最好，需联网，默认推荐 |
| Piper (Local) | 本地 CPU 推理，无需联网，低延迟 |
| System | 浏览器内置 Web Speech API，无需额外服务 |

- **Voice 下拉框**：可选择不同声音，Edge-TTS 和 Piper 各有独立声音列表
- 朗读过程中可点击 **Stop speaking** 立即停止
- 自动跳过 Markdown 符号和中文内容，只朗读英文
- 长文本自动分段朗读，避免 Chrome 移动版的 15 秒截断问题
- 点击 Start 或勾选 Voice reply 时会自动解锁移动端音频引擎

## 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端页面 (HTTPS) | 8443 | server.py 提供，反向代理所有请求 |
| 前端页面 (HTTP) | 8000 | 可选，本机简单文件服务 |
| ASR: Zipformer 2023-06-26 | 6006 | 流式语音识别 |
| ASR: Zipformer 2023-06-21 | 6007 | 流式语音识别 |
| ASR: Whisper Medium EN | 6008 | 离线语音识别 |
| ASR: SenseVoice Small | 6009 | 离线语音识别 |
| Ollama | 11434 | LLM API |
| Open WebUI | 3000 | 聊天界面（本链路不依赖） |

## 两种访问模式对比

| | 本机 (HTTP :8000) | 局域网 (HTTPS :8443) |
|---|---|---|
| 浏览器直连 ASR/Ollama | 是（ws://localhost:port） | 否 |
| 通过 server.py 代理 | 否 | 是（/ws, /asr, /api/*, /tts） |
| 麦克风权限 | localhost 自动允许 | 需 HTTPS + 接受证书警告 |
| Edge-TTS / Piper | 不可用（需 server.py） | 可用 |
| 依赖 | 无额外依赖 | `pip install aiohttp edge-tts websockets sherpa-onnx numpy` |

## 常见问题

**Q: 点 Start 后状态一直显示 Connecting...**
A: 检查 sherpa-rt 容器是否在运行，对应端口是否映射成功。`docker exec sherpa-rt bash -c "ss -tlnp"` 查看容器内端口。

**Q: 识别出文字但没有 Coach feedback**
A: 检查 Ollama 是否运行。`curl http://localhost:11434/api/tags` 确认服务正常。

**Q: 切换离线模型后没有识别结果**
A: 离线模型需要先 Start 录音，再 Stop 才会发送音频。确认容器内对应端口（6008/6009）的进程在运行。

**Q: 本机访问麦克风权限被拒绝**
A: 确保用 `http://localhost:8000` 访问，不要用 `127.0.0.1` 或局域网 IP。

**Q: 手机/局域网设备麦克风权限被拒绝**
A: 必须通过 HTTPS 访问。用 `python server.py` 启动 HTTPS 反向代理，然后用 `https://<IP>:8443` 访问。

**Q: 手机访问时 ASR 或 Ollama 连接失败**
A: 通过 `server.py` 访问时所有请求经由代理转发，手机不需要直连。确认 `server.py` 终端无报错。检查 Windows 防火墙是否放行了 8443 端口。

**Q: Voice reply 没有声音**
A: 确保在说话之前先勾选了 Voice reply（或已点过 Start）。移动端音频需要在用户点击时解锁。也请检查设备音量和静音开关。

**Q: Edge-TTS 声音从另一台设备播出**
A: 每台设备独立播放。如果只想在手机听，确保电脑上取消勾选 Voice reply，反之亦然。

**Q: 语音朗读中途断了**
A: 长文本已自动分段朗读。如果仍有中断，保持页面在前台（后台会被浏览器节流）。

**Q: Piper TTS 不可用**
A: 确认 Windows 主机已安装 `sherpa-onnx`（`pip install sherpa-onnx`），且 `models/vits-piper-en_US-amy-low/` 下有模型文件。server.py 启动时会打印 Piper 加载状态。

**Q: 容器停了怎么恢复**
A: `docker start sherpa-rt && docker exec sherpa-rt bash /workspace/start_asr.sh`
