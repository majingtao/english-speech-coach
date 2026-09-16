import { spawn } from "node:child_process"
import { createRequire } from "node:module"
const require = createRequire(import.meta.url)
const child = spawn(process.execPath, [require.resolve("next/dist/bin/next"), "dev", "--hostname", "127.0.0.1", "--port", "53120"], {
  stdio: "inherit", windowsHide: true, env: { ...process.env, NEXT_BUILD_DIR: ".next-coach-test" },
})
child.on("exit", code => process.exit(code || 0))
for (const signal of ["SIGINT", "SIGTERM"]) process.on(signal, () => child.kill(signal))
