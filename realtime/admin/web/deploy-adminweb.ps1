# ===== EnglishAI Admin Web (Vben5 / pnpm + turbo) Deploy =====
# Static files only - Nginx serves them, no service restart needed.

$ErrorActionPreference = "Stop"

$LogFile = Join-Path $PSScriptRoot ("deploy-adminweb-log-" + (Get-Date -Format "yyyyMMdd-HHmmss") + ".txt")
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
$ProjectDir  = "E:\project\englishAi\realtime\admin\web"
# Which app to build. "" = build everything (slow, builds all UI variants).
# Set to e.g. "build:antd" once you know which variant you use.
$BuildScript = "build:antd"
# Relative path to the dist folder. "" = auto-detect under apps\*\dist
$DistDir     = "apps\web-antd\dist"
$RunInstall  = $true          # set $false to skip pnpm install when deps unchanged
$SshHost     = "kugua"
$RemoteDir   = "/mnt/www/englishAI/admin/web"
$RemoteBase  = "/mnt/www/englishAI/admin"
$PublicUrl   = "https://learn.kugua.cn/admin/"
$KeepBackups = 5
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

foreach ($c in @("pnpm","tar","scp","ssh")) {
    if (-not (Get-Command $c -ErrorAction SilentlyContinue)) { Fail "Command not found: $c" }
}

# Resolve pnpm to the .cmd shim. The corepack-generated pnpm.ps1 throws
# "index out of range" under $ErrorActionPreference = Stop even when the
# underlying command succeeded, which would abort a perfectly good build.
$pnpmCmd = (Get-Command "pnpm.cmd" -EA SilentlyContinue).Source
if (-not $pnpmCmd) { $pnpmCmd = (Get-Command "pnpm" -EA SilentlyContinue).Source }
if (-not $pnpmCmd) { Fail "pnpm not found" }
Write-Host "pnpm: $pnpmCmd" -ForegroundColor DarkGray

# pnpm draws boxed progress output using ANSI cursor control sequences.
# The PowerShell console host throws IndexOutOfRangeException while rendering
# them, which aborts the script even though pnpm itself succeeded.
# CI=1 switches pnpm to plain line-based output; routing through cmd /c keeps
# PowerShell from interpreting whatever is left.
$env:CI = "1"
$env:FORCE_COLOR = "0"

# NOTE: pipe to Out-Host. A PowerShell function returns everything written to
# the pipeline, so without this the command output itself becomes part of the
# return value and the exit-code check compares against a giant string.
function Invoke-Pnpm([string]$argLine) {
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & cmd /c "`"$pnpmCmd`" $argLine 2>&1" | Out-Host
    $code = $LASTEXITCODE
    $ErrorActionPreference = $prev
    return $code
}
if (-not (Test-Path $ProjectDir)) { Fail "Project dir not found: $ProjectDir" }
Set-Location $ProjectDir
Write-Host "Working dir: $(Get-Location)" -ForegroundColor DarkGray

# ---- 1. install ----
if ($RunInstall) {
    Step "1/7 pnpm install"
    $rc = Invoke-Pnpm "install --frozen-lockfile"
    if ($rc -ne 0) {
        Warn "--frozen-lockfile failed, retrying without it"
        $rc = Invoke-Pnpm "install"
        if ($rc -ne 0) { Fail "pnpm install failed (exit $rc)" }
    }
} else {
    Step "1/7 pnpm install (skipped)"
}

# ---- 2. build ----
$script = if ($BuildScript -ne "") { $BuildScript } else { "build" }
Step "2/7 pnpm run $script"
$rc = Invoke-Pnpm "run $script"
if ($rc -ne 0) { Fail "Build failed (exit $rc) - see output above." }

# ---- 3. locate dist ----
# Vben is a monorepo: each UI variant builds into apps\<name>\dist.
# Multiple dist folders means the build produced several variants and we
# cannot guess which one is deployed - the user must pick.
Step "3/7 Locate dist"
if ($DistDir -ne "") {
    $dist = Resolve-Path $DistDir -EA SilentlyContinue
    if (-not $dist) { Fail "DistDir not found: $DistDir" }
    $dist = $dist.Path
} else {
    $cands = @()
    if (Test-Path "apps") {
        $cands += Get-ChildItem "apps" -Directory |
                  ForEach-Object { Join-Path $_.FullName "dist" } |
                  Where-Object { Test-Path (Join-Path $_ "index.html") }
    }
    foreach ($p in @("dist","dist-prod")) {
        if (Test-Path (Join-Path $p "index.html")) { $cands += (Resolve-Path $p).Path }
    }
    if ($cands.Count -eq 0) { Fail "No dist folder with index.html found. Check the build output path and set `$DistDir." }
    if ($cands.Count -gt 1) {
        Write-Host "    Multiple dist folders found:" -ForegroundColor Yellow
        $cands | ForEach-Object { Write-Host "      $_" -ForegroundColor Yellow }
        Fail "Set `$DistDir in this script to the one you deploy (and set `$BuildScript to build only that app)."
    }
    $dist = $cands[0]
}
$fileCount = (Get-ChildItem $dist -Recurse -File).Count
$distMB = [math]::Round(((Get-ChildItem $dist -Recurse -File | Measure-Object Length -Sum).Sum / 1MB), 1)
Ok "$dist  ($fileCount files, $distMB MB)"

# ---- 4. secret scan ----
Step "4/7 Scan bundle for leaked API keys"
$leaks = Get-ChildItem $dist -Recurse -Include *.js,*.mjs -File |
         Select-String -Pattern "sk-[A-Za-z0-9]{16,}" -EA SilentlyContinue
if ($leaks) {
    $leaks | Select-Object -First 5 | ForEach-Object { Write-Host "    $_" -ForegroundColor Red }
    Fail "Possible API key found in bundle - deploy aborted."
}
Ok "No leaks found"

# ---- 5. pack + upload ----
Step "5/7 Pack and upload"
$ts = Get-Date -Format "yyyyMMdd-HHmmss"
$tarName = "admin-web-$ts.tar.gz"
$tarPath = Join-Path $env:TEMP $tarName
tar -czf $tarPath -C $dist .
if ($LASTEXITCODE -ne 0) { Fail "tar failed" }
Ok ("{0} MB packed" -f [math]::Round((Get-Item $tarPath).Length / 1MB, 1))
scp $tarPath "${SshHost}:/tmp/$tarName"
if ($LASTEXITCODE -ne 0) { Fail "scp failed - server still on old version, safe to retry." }
Ok "Uploaded"

# ---- 6. backup + replace ----
# Wipe the target dir before extracting so stale hashed assets do not pile up.
Step "6/7 Deploy on server"
$deploy = @(
    "cd $RemoteBase",
    "tar czf admin-web-backup-$ts.tar.gz web",
    "echo '    backup: admin-web-backup-$ts.tar.gz'",
    "rm -rf $RemoteDir",
    "mkdir -p $RemoteDir",
    "tar xzf /tmp/$tarName -C $RemoteDir",
    "test -f $RemoteDir/index.html",
    "rm -f /tmp/$tarName",
    "echo '    extracted and verified'"
) -join " && "
ssh $SshHost $deploy
if ($LASTEXITCODE -ne 0) {
    Warn "Deploy failed. Rolling back ..."
    $rb = @(
        "cd $RemoteBase",
        "rm -rf web",
        "tar xzf admin-web-backup-$ts.tar.gz",
        "echo '    rolled back'"
    ) -join " && "
    ssh $SshHost $rb
    Fail "Deploy failed and was rolled back."
}
Ok "Files replaced"

# ---- 7. verify ----
Step "7/7 Verify"
try {
    $r = Invoke-WebRequest -Uri $PublicUrl -UseBasicParsing -TimeoutSec 15
    Ok "public $PublicUrl -> $($r.StatusCode)"
} catch {
    Warn "public check failed: $_"
    Warn "Files are deployed; if this fails the issue is in Nginx config, not the files."
}
ssh $SshHost "ls $RemoteDir | head -10; ls -t $RemoteBase/admin-web-backup-*.tar.gz 2>/dev/null | tail -n +$($KeepBackups+1) | xargs -r rm -f"

Remove-Item $tarPath -Force -EA SilentlyContinue

Write-Host ""
Write-Host "DONE. Hard-refresh the browser (Ctrl+F5) to verify." -ForegroundColor Green
Write-Host "Rollback command:" -ForegroundColor Yellow
Write-Host "  ssh $SshHost `"cd $RemoteBase && rm -rf web && tar xzf admin-web-backup-$ts.tar.gz`""
Write-Host ""
Write-Host "Log: $LogFile" -ForegroundColor DarkGray
try { Stop-Transcript | Out-Null } catch {}
Read-Host "Press Enter to close"
