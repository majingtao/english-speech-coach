# ===== EnglishAI Python Service (server.py) Deploy =====
# Syncs code only. The server-side .env and logs/ are NEVER touched.

$ErrorActionPreference = "Stop"

# Log to TEMP, not the project dir: the transcript file is held open for
# writing, and tar cannot read it while packing the project directory.
$LogFile = Join-Path $env:TEMP ("deploy-py-log-" + (Get-Date -Format "yyyyMMdd-HHmmss") + ".txt")
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
$ProjectDir  = "E:\project\englishAi\realtime\web"
$SshHost     = "kugua"
$RemoteDir   = "/mnt/www/englishAI/service"
$RemoteBase  = "/mnt/www/englishAI"
$ServiceName = "english-py"
$RemotePort  = 8443
$UseHttps    = $true       # service listens on https with a self-signed cert
$MaxWaitSec  = 90
$KeepBackups = 5

# Never uploaded. .env holds the real API keys and must stay server-side only.
$Excludes = @(
    ".env", ".env.*", "__pycache__", "*.pyc", ".venv", "venv",
    "logs", ".git", ".idea", ".vscode", "*.log", ".DS_Store",
    "deploy-py-log-*.txt", "deploy-py.ps1", "deploy-py.bat"
)
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

foreach ($c in @("tar","scp","ssh")) {
    if (-not (Get-Command $c -ErrorAction SilentlyContinue)) { Fail "Command not found: $c" }
}
if (-not (Test-Path $ProjectDir)) { Fail "Project dir not found: $ProjectDir" }
Set-Location $ProjectDir
Write-Host "Working dir: $(Get-Location)" -ForegroundColor DarkGray

if (-not (Test-Path "server.py")) { Fail "server.py not found in $ProjectDir" }

# ---- 1. syntax check ----
# A syntax error would leave the service crash-looping on Restart=always.
# Catch it here rather than on the server.
Step "1/6 Syntax check"
$py = (Get-Command python -EA SilentlyContinue)
if ($py) {
    & python -m py_compile server.py
    if ($LASTEXITCODE -ne 0) { Fail "server.py has a syntax error - fix it before deploying." }
    Ok "server.py compiles"
} else {
    Warn "python not found locally - skipping syntax check"
}

# ---- 2. secret scan ----
# Hardcoded keys in source are a separate problem from .env, and this is the
# last point where they can be caught before they reach the server.
Step "2/6 Scan source for hardcoded keys"
$leaks = Get-ChildItem . -Recurse -Include *.py -File |
         Where-Object { $_.FullName -notmatch "\\(__pycache__|\.venv|venv)\\" } |
         Select-String -Pattern "sk-[A-Za-z0-9]{16,}" -EA SilentlyContinue
if ($leaks) {
    $leaks | Select-Object -First 5 | ForEach-Object { Write-Host "    $_" -ForegroundColor Red }
    Fail "Hardcoded API key found in source - move it to .env and redeploy."
}
Ok "No hardcoded keys"

# ---- 3. pack ----
Step "3/6 Pack"
$ts = Get-Date -Format "yyyyMMdd-HHmmss"
$tarName = "py-service-$ts.tar.gz"
$tarPath = Join-Path $env:TEMP $tarName
$exArgs = @()
foreach ($e in $Excludes) { $exArgs += "--exclude=$e" }
tar -czf $tarPath @exArgs -C $ProjectDir .
if ($LASTEXITCODE -ne 0) { Fail "tar failed" }

# Verify the archive really has no .env in it before anything leaves the machine.
$listing = tar -tzf $tarPath
if ($listing -match "(^|/)\.env($|\.)") { Fail "SAFETY STOP: the archive contains a .env file. Check the `$Excludes list." }
$entryCount = ($listing | Measure-Object).Count
Ok ("{0} entries, {1} MB" -f $entryCount, [math]::Round((Get-Item $tarPath).Length / 1MB, 2))

# ---- 4. upload ----
Step "4/6 Upload"
scp $tarPath "${SshHost}:/tmp/$tarName"
if ($LASTEXITCODE -ne 0) { Fail "scp failed - server untouched, safe to retry." }
Ok "Uploaded"

# ---- 5. backup + extract over the top + restart ----
# Extract without deleting first: the remote dir also holds .env, logs/ and
# anything else generated at runtime, none of which exist locally.
Step "5/6 Deploy and restart"
$deploy = @(
    "cd $RemoteBase",
    "tar czf py-service-backup-$ts.tar.gz --exclude=service/logs service",
    "echo '    backup: py-service-backup-$ts.tar.gz'",
    "tar xzf /tmp/$tarName -C $RemoteDir",
    "test -f $RemoteDir/server.py",
    "test -f $RemoteDir/.env",
    "rm -f /tmp/$tarName",
    "echo '    extracted, .env intact'",
    "systemctl restart $ServiceName"
) -join " && "
ssh $SshHost $deploy
if ($LASTEXITCODE -ne 0) { Fail "Deploy failed. Inspect: ssh $SshHost 'ls -la $RemoteDir'" }
$startWait = Get-Date

# ---- 6. wait for the port, roll back on failure ----
$scheme = if ($UseHttps) { "https" } else { "http" }
$curlOpts = if ($UseHttps) { "-sk" } else { "-s" }
Step "6/6 Wait for startup (max ${MaxWaitSec}s)"
$deadline = (Get-Date).AddSeconds($MaxWaitSec)
$up = $false
$lastCode = ""
while ((Get-Date) -lt $deadline) {
    $lastCode = (ssh $SshHost "curl $curlOpts -o /dev/null -m 2 -w '%{http_code}' ${scheme}://127.0.0.1:$RemotePort/" 2>$null)
    if ($lastCode -and $lastCode -ne "000") { $up = $true; break }
    $elapsed = [int]((Get-Date) - $startWait).TotalSeconds
    Write-Host "    ...starting (${elapsed}s)" -ForegroundColor DarkGray
    Start-Sleep -Seconds 3
}

if (-not $up) {
    Warn "Service did not respond within ${MaxWaitSec}s. Last 40 log lines:"
    ssh $SshHost "journalctl -u $ServiceName -n 40 --no-pager"
    Warn "Rolling back ..."
    $rb = @(
        "cd $RemoteBase",
        "tar xzf py-service-backup-$ts.tar.gz",
        "systemctl restart $ServiceName",
        "echo '    rolled back'"
    ) -join " && "
    ssh $SshHost $rb
    Fail "Deploy failed and was rolled back."
}
$took = [int]((Get-Date) - $startWait).TotalSeconds
Ok "UP after ${took}s (http $lastCode)"

ssh $SshHost "systemctl is-active $ServiceName; ls -t $RemoteBase/py-service-backup-*.tar.gz 2>/dev/null | tail -n +$($KeepBackups+1) | xargs -r rm -f"
Remove-Item $tarPath -Force -EA SilentlyContinue

Write-Host ""
Write-Host "DONE." -ForegroundColor Green
Write-Host "Rollback command:" -ForegroundColor Yellow
Write-Host "  ssh $SshHost `"cd $RemoteBase && tar xzf py-service-backup-$ts.tar.gz && systemctl restart $ServiceName`""
Write-Host ""
Write-Host "Log: $LogFile" -ForegroundColor DarkGray
try { Stop-Transcript | Out-Null } catch {}
Read-Host "Press Enter to close"
