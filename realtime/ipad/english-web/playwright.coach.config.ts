import { defineConfig } from "@playwright/test"
export default defineConfig({
  testDir: "./tests/coach", timeout: 60000, workers: 1, retries: 0,
  outputDir: "./test-results/coach",
  use: { baseURL: "http://127.0.0.1:53120", channel: "chrome", headless: true,
    launchOptions: { args: ["--use-fake-device-for-media-stream", "--use-fake-ui-for-media-stream"] },
    permissions: ["microphone"], viewport: { width: 1024, height: 1100 }, screenshot: "only-on-failure" },
  webServer: { command: "node scripts/start-coach-test.mjs", url: "http://127.0.0.1:53120/login", reuseExistingServer: !process.env.CI, timeout: 120000 },
})
