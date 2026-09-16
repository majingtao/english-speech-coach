# ===== EnglishAI Frontend Deploy (Next.js standalone) =====
# Requires: tar.exe, scp.exe, ssh.exe (built into Windows 10/11), npm
# Flow: build -> verify -> key scan -> pack -> upload -> backup+extract -> restart -> health check

$ErrorActionPreference = "Stop"

$LogFile = Join-Path $PSScriptRoot ("deploy-log-" + (Get-Date -Format "yyyyMMdd-HHmmss") + ".txt")
Start-Transcript -Path $LogFile | Out-Null

trap {
    Write-Host ""
    Write-Host "[FAIL] Unhandled error: $_" -ForegroundColor Red
    Write-Host "At: $($_.InvocationInfo.PositionMessage)" -ForegroundColor Red
    try { Stop-Transcript | Out-Null } catch {}
    Read-Host "Press Enter to exit"
    exit 1
}

# ---------- CONFIG ----------
$FrontendDir = "E:\project\englishAi\realtime\ipad\english-web"
$SshHost     = "kugua"
$RemoteDir   = "/mnt/www/englishAI/front"
$RemoteBase  = "/mnt/www/englishAI"
$ServiceName = "english-front"
$RemotePort  = 53000
$PublicUrl   = "https://learn.kugua.cn/"

# NEXT_PUBLIC_* are baked in at BUILD time - must match the systemd service file
$env:NEXT_PUBLIC_API_BASE_URL = "https://learn.kugua.cn"
$env:NEXT_PUBLIC_TENANT_ID    = "1"
# ----------------------------

function Step($m) { Write-Host ""; Write-Host "==> $m" -ForegroundColor Cyan }
function Ok($m)   { Write-Host "    $m" -ForegroundColor Green }
function Warn($m) { Write-Host "    $m" -ForegroundColor Yellow }
function Fail($m) {
    Write-Host ""
    Write-Host "[FAIL] $m" -ForegroundColor Red
    try { Stop-Transcript | Out-Null } catch {}
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Log file: $LogFile" -ForegroundColor DarkGray

foreach ($c in @("tar","scp","ssh","npm")) {
    if (-not (Get-Command $c -ErrorAction SilentlyContinue)) { Fail "Command not found: $c" }
}
if (-not (Test-Path $FrontendDir)) {
    Fail "Frontend dir not found: $FrontendDir - edit `$FrontendDir in this script."
}
Set-Location $FrontendDir
Write-Host "Working dir: $(Get-Location)" -ForegroundColor DarkGray

# ---- 1. build ----
Step "1/7 npm run build  (API_BASE_URL=$env:NEXT_PUBLIC_API_BASE_URL)"
npm run build
if ($LASTEXITCODE -ne 0) { Fail "npm run build failed - see output above." }

# ---- 2. verify standalone output ----
Step "2/7 Verify standalone output"
if (-not (Test-Path ".next\standalone\server.js")) {
    $found = Get-ChildItem ".next\standalone" -Filter "server.js" -Recurse -EA SilentlyContinue | Select-Object -First 1
    if ($found) { Fail "server.js found at: $($found.FullName) - monorepo layout, adjust the standalone path in this script." }
    Fail "Missing .next\standalone\server.js - add  output: 'standalone'  to next.config.js and rebuild."
}
Ok "server.js OK"

# ---- 3. secret scan ----
Step "3/7 Scan bundle for leaked API keys"
$leaks = Select-String -Path ".next\static\**\*.js" -Pattern "sk-[A-Za-z0-9]{16,}" -EA SilentlyContinue
if ($leaks) {
    $leaks | Select-Object -First 5 | ForEach-Object { Write-Host "    $_" -ForegroundColor Red }
    Fail "Possible API key found in bundle - deploy aborted."
}
Ok "No leaks found"

# ---- 4. pack ----
Step "4/7 Pack artifacts"
$stage = Join-Path $env:TEMP "eng-front-stage"
if (Test-Path $stage) { Remove-Item $stage -Recurse -Force }
New-Item -ItemType Directory -Path $stage -Force | Out-Null

Copy-Item ".next\standalone\*" $stage -Recurse -Force
New-Item -ItemType Directory -Path "$stage\.next\static" -Force | Out-Null
Copy-Item ".next\static\*" "$stage\.next\static\" -Recurse -Force
if (Test-Path "public") {
    New-Item -ItemType Directory -Path "$stage\public" -Force | Out-Null
    Copy-Item "public\*" "$stage\public\" -Recurse -Force
}

$tarName = "eng-front-" + (Get-Date -Format "yyyyMMdd-HHmmss") + ".tar.gz"
$tarPath = Join-Path $env:TEMP $tarName
tar -czf $tarPath -C $stage .
if ($LASTEXITCODE -ne 0) { Fail "tar failed" }
$sizeMB = [math]::Round((Get-Item $tarPath).Length / 1MB, 1)
Ok ("{0}  ({1} MB)" -f $tarName, $sizeMB)

# ---- 5. upload ----
Step "5/7 Upload to server"
scp $tarPath "${SshHost}:/tmp/$tarName"
if ($LASTEXITCODE -ne 0) { Fail "scp failed - server still on old version, safe to retry." }
Ok "Uploaded"

# ---- 6. backup + extract + restart ----
Step "6/7 Deploy on server"
$ts = Get-Date -Format "yyyyMMdd-HHmmss"
$parts = @(
    "cd $RemoteBase",
    "tar czf front-backup-$ts.tar.gz front",
    "echo '    backup: front-backup-$ts.tar.gz'",
    "rm -rf $RemoteDir/.next $RemoteDir/public",
    "tar xzf /tmp/$tarName -C $RemoteDir",
    "test -f $RemoteDir/server.js",
    "test -d $RemoteDir/.next/static",
    "rm -f /tmp/$tarName",
    "echo '    extracted and verified'",
    "systemctl restart $ServiceName",
    "sleep 3",
    "systemctl is-active $ServiceName"
)
$remote = ($parts -join " && ")
ssh $SshHost $remote
if ($LASTEXITCODE -ne 0) {
    Warn "Last 40 log lines:"
    ssh $SshHost "journalctl -u $ServiceName -n 40 --no-pager"
    Fail "Deploy or service start failed. Rollback:`n  ssh $SshHost `"cd $RemoteBase && rm -rf front && tar xzf front-backup-$ts.tar.gz && systemctl restart $ServiceName`""
}
Ok "Service active"

# ---- 7. health check ----
Step "7/7 Health check"
ssh $SshHost "curl -s -o /dev/null -w '    localhost:$RemotePort -> %{http_code}\n' http://127.0.0.1:$RemotePort/"
try {
    $r = Invoke-WebRequest -Uri $PublicUrl -UseBasicParsing -TimeoutSec 15
    Ok "public $PublicUrl -> $($r.StatusCode)"
} catch {
    Warn "public check failed: $_"
    Warn "If localhost is OK but public is not, the issue is in Nginx, not this deploy."
}

Remove-Item $tarPath -Force -EA SilentlyContinue
Remove-Item $stage -Recurse -Force -EA SilentlyContinue

Write-Host ""
Write-Host "DONE. Hard-refresh the browser (Ctrl+F5) to verify." -ForegroundColor Green
Write-Host "Rollback command:" -ForegroundColor Yellow
Write-Host "  ssh $SshHost `"cd $RemoteBase && rm -rf front && tar xzf front-backup-$ts.tar.gz && systemctl restart $ServiceName`""
Write-Host ""
Write-Host "Log: $LogFile" -ForegroundColor DarkGray
try { Stop-Transcript | Out-Null } catch {}
Read-Host "Press Enter to close"
