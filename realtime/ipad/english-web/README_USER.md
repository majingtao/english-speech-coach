# EnglishAI iPad 局域网使用

## 启动

```powershell
npm run dev
```

孩子的 iPad 与电脑连接同一 Wi-Fi 后，使用 Safari 打开：

```text
https://192.168.0.7:53000/login
```

日常稳定使用建议启动生产预览：

```powershell
npm run build
npm run preview:prod:https
```

## iPad 证书与麦克风

1. 将 `certs/englishai-iphone-cert-package/englishai-rootCA.cer` 传到 iPad，不要传送任何 `*-key.pem` 私钥文件。
2. 在“设置 > 通用 > VPN 与设备管理”中安装已下载的描述文件。
3. 在“设置 > 通用 > 关于本机 > 证书信任设置”中完全信任 `EnglishAI Local Root CA`。
4. 在 Safari 的网站设置中将麦克风设为“允许”。
5. 关闭旧的 Safari 标签页，再重新打开上述 HTTPS 地址。

如果电脑的局域网 IP 发生变化，需要重新运行 `npm run dev` 生成包含新 IP 的服务器证书，并同步更新 `allowedDevOrigins`。

## 主要技术

- Next.js、Tailwind CSS、shadcn/ui
- Zustand：用户信息和练习状态
- Axios：通过同源 `/app-api` 路径访问后端
- React Hook Form：登录注册和表单
- Framer Motion：页面和练习状态动画
