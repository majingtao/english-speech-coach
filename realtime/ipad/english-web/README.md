This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

## 孩子日常使用（推荐）

孩子在 iPad 上日常练习时使用生产预览模式，避免依赖 Next.js 开发热更新。

在本目录 `ipad/english-web` 中执行：

```powershell
npm run build
npm run preview:prod:https
```

- `npm run build`：生成最新生产版本；代码发生变化后需要重新执行。
- `npm run preview:prod:https`：通过 HTTPS 启动生产预览服务。
- iPad 与电脑连接同一 Wi-Fi 后，使用 Safari 打开：

```text
https://192.168.0.7:53000/login
```

首次使用时，还需要按照 [README_USER.md](./README_USER.md) 安装并完全信任 `EnglishAI Local Root CA` 证书。

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.
