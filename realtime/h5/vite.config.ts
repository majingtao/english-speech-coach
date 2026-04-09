import path from 'node:path'
import process from 'node:process'
import { loadEnv } from 'vite'
import type { ConfigEnv, UserConfig } from 'vite'
import { createVitePlugins } from './build/vite'
import { exclude, include } from './build/vite/optimize'

export default ({ mode }: ConfigEnv): UserConfig => {
  const root = process.cwd()
  const env = loadEnv(mode, root)

  return {
    base: env.VITE_APP_PUBLIC_PATH,
    plugins: createVitePlugins(mode),

    server: {
      host: true,
      port: 43000,
      https: {}, // 启用 HTTPS（手机端 getUserMedia 必须 https），证书由 @vitejs/plugin-basic-ssl 自动生成
      proxy: {
        // yudao 后端
        '/admin-api': {
          target: 'http://localhost:5666',
          ws: false,
          changeOrigin: true,
        },
        // Python 语音代理（LLM / ASR / TTS / 题库）
        '/py': {
          target: 'https://localhost:8443',
          ws: false,
          changeOrigin: true,
          secure: false,
          rewrite: path => path.replace(/^\/py/, ''),
        },
      },
    },

    resolve: {
      alias: {
        '@': path.join(__dirname, './src'),
        '~': path.join(__dirname, './src/assets'),
        '~root': path.join(__dirname, '.'),
      },
    },

    build: {
      cssCodeSplit: false,
      chunkSizeWarningLimit: 2048,
      outDir: env.VITE_APP_OUT_DIR || 'dist',
    },

    optimizeDeps: { include, exclude },
  }
}
