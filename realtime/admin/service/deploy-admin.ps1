# ===== EnglishAI Admin (yudao Spring Boot) Deploy =====
# Build with JDK 17 -> verify bytecode -> upload -> swap jar -> wait for startup
# Auto-rollback if the service does not come up in time.

$ErrorActionPreference = "Stop"

$LogFile = Join-Path $PSScriptRoot ("deploy-admin-log-" + (Get-Date -Format "yyyyMMdd-HHmmss") + ".txt")
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
$ProjectDir   = "E:\project\englishAi\realtime\admin\service"
# Path to LOCAL JDK 17. Leave empty to use whatever mvn picks up (risky).
$JavaHome     = "E:\java"
$SshHost      = "kugua"
$RemoteDir    = "/mnt/www/englishAI/admin/api"
$RemoteJar    = "yudao-server.jar"
$ServiceName  = "english-admin"
$RemotePort   = 48080
$MaxWaitSec   = 180          # Spring Boot cold start can be slow
$MaxTargetVer = 61           # 61 = Java 17. Build must not exceed this.
$KeepBackups  = 5
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

foreach ($c in @("mvn","scp","ssh")) {
    if (-not (Get-Command $c -ErrorAction SilentlyContinue)) { Fail "Command not found: $c" }
}
if (-not (Test-Path $ProjectDir)) { Fail "Project dir not found: $ProjectDir" }

if ($JavaHome -ne "") {
    if (-not (Test-Path "$JavaHome\bin\javac.exe")) {
        Fail "JDK not found at $JavaHome (no bin\javac.exe). Edit `$JavaHome in this script, or set it to '' to use the default."
    }
    $env:JAVA_HOME = $JavaHome
    $env:Path = "$JavaHome\bin;" + $env:Path
    Ok "JAVA_HOME = $JavaHome"
}

Set-Location $ProjectDir
Write-Host "Working dir: $(Get-Location)" -ForegroundColor DarkGray
Step "0/7 Toolchain"
mvn -v

# ---- 1. build ----
Step "1/7 mvn clean package -DskipTests"
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) { Fail "Maven build failed - see output above." }

# ---- 2. locate the executable fat jar ----
Step "2/7 Locate fat jar"
$jar = Get-ChildItem "yudao-server\target" -Filter "*.jar" -EA SilentlyContinue |
       Where-Object { $_.Name -notlike "original-*" -and $_.Name -notlike "*-sources.jar" -and $_.Name -notlike "*-javadoc.jar" } |
       Sort-Object Length -Descending | Select-Object -First 1
if (-not $jar) { Fail "No jar found in yudao-server\target" }
$jarMB = [math]::Round($jar.Length / 1MB, 1)
if ($jar.Length -lt 20MB) {
    Fail ("{0} is only {1} MB - that looks like a thin jar, not the Spring Boot fat jar. Check the spring-boot-maven-plugin config." -f $jar.Name, $jarMB)
}
Ok ("{0}  ({1} MB)" -f $jar.Name, $jarMB)

# ---- 3. bytecode version gate ----
# Reads the class file header: bytes 6-7 are the major version.
# 61 = Java 17, 65 = Java 21. Server runs JDK 17, so anything above 61 will
# throw UnsupportedClassVersionError at startup.
Step "3/7 Verify bytecode version (must be <= $MaxTargetVer)"
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($jar.FullName)
try {
    $entry = $zip.Entries | Where-Object { $_.FullName -like "BOOT-INF/classes/*.class" } | Select-Object -First 1
    if (-not $entry) { $entry = $zip.Entries | Where-Object { $_.FullName -like "*.class" } | Select-Object -First 1 }
    if (-not $entry) { Fail "No .class entry found inside the jar" }
    $stream = $entry.Open()
    $buf = New-Object byte[] 8
    $null = $stream.Read($buf, 0, 8)
    $stream.Close()
} finally { $zip.Dispose() }

if ($buf[0] -ne 0xCA -or $buf[1] -ne 0xFE) { Fail "Not a valid class file header" }
$major = [int]$buf[6] * 256 + [int]$buf[7]
$javaVer = $major - 44
if ($major -gt $MaxTargetVer) {
    Fail "Bytecode major version $major (Java $javaVer) exceeds server JDK 17 (61). The jar would fail with UnsupportedClassVersionError. Set `$JavaHome to a JDK 17 install and rebuild."
}
Ok "major=$major (Java $javaVer) - compatible"

# ---- 4. upload ----
Step "4/7 Upload"
$ts = Get-Date -Format "yyyyMMdd-HHmmss"
$tmpName = "yudao-server-$ts.jar"
scp $jar.FullName "${SshHost}:/tmp/$tmpName"
if ($LASTEXITCODE -ne 0) { Fail "scp failed - server untouched, safe to retry." }
Ok "Uploaded to /tmp/$tmpName"

# ---- 5. backup + swap + start ----
Step "5/7 Swap jar and restart"
$bak = "$RemoteJar.bak-$ts"
$swap = @(
    "cd $RemoteDir",
    "cp -p $RemoteJar $bak",
    "echo '    backup: $bak'",
    "systemctl stop $ServiceName",
    "mv /tmp/$tmpName $RemoteJar",
    "chmod 644 $RemoteJar",
    "systemctl start $ServiceName",
    "echo '    started, waiting for port'"
) -join " && "
ssh $SshHost $swap
if ($LASTEXITCODE -ne 0) { Fail "Swap failed. Check the server state manually: ssh $SshHost 'ls -l $RemoteDir'" }
$startWait = Get-Date

# ---- 6. wait for the port to respond ----
# Polling is done from PowerShell, one trivial ssh command per attempt.
# Do NOT build a bash for-loop here: nested quotes get mangled on the way
# through PowerShell -> ssh, and a syntax error would look like a failed
# deploy and trigger a needless rollback.
Step "6/7 Wait for startup (max ${MaxWaitSec}s)"
$deadline = (Get-Date).AddSeconds($MaxWaitSec)
$up = $false
$lastCode = ""
while ((Get-Date) -lt $deadline) {
    $lastCode = (ssh $SshHost "curl -s -o /dev/null -m 2 -w '%{http_code}' http://127.0.0.1:$RemotePort/" 2>$null)
    if ($lastCode -and $lastCode -ne "000") { $up = $true; break }
    $elapsed = [int]((Get-Date) - $startWait).TotalSeconds
    Write-Host "    ...booting (${elapsed}s)" -ForegroundColor DarkGray
    Start-Sleep -Seconds 5
}

if (-not $up) {
    Warn "Service did not respond within ${MaxWaitSec}s. Last 60 log lines:"
    ssh $SshHost "journalctl -u $ServiceName -n 60 --no-pager"
    Warn "Rolling back to $bak ..."
    $rb = @(
        "cd $RemoteDir",
        "systemctl stop $ServiceName",
        "mv $RemoteJar $RemoteJar.failed-$ts",
        "cp -p $bak $RemoteJar",
        "systemctl start $ServiceName",
        "echo '    rolled back'"
    ) -join " && "
    ssh $SshHost $rb
    Fail "Deploy failed and was rolled back. The bad jar is kept at $RemoteDir/$RemoteJar.failed-$ts for inspection."
}
$took = [int]((Get-Date) - $startWait).TotalSeconds
Ok "UP after ${took}s (http $lastCode)"

# ---- 7. verify + prune backups ----
Step "7/7 Verify"
ssh $SshHost "systemctl is-active $ServiceName; ls -lh $RemoteDir/$RemoteJar; ls -t $RemoteDir/$RemoteJar.bak-* 2>/dev/null | tail -n +$($KeepBackups+1) | xargs -r rm -f; echo '    old backups pruned (keeping $KeepBackups)'"

Write-Host ""
Write-Host "DONE." -ForegroundColor Green
Write-Host "Rollback command:" -ForegroundColor Yellow
Write-Host "  ssh $SshHost `"cd $RemoteDir && systemctl stop $ServiceName && cp -p $bak $RemoteJar && systemctl start $ServiceName`""
Write-Host ""
Write-Host "Log: $LogFile" -ForegroundColor DarkGray
try { Stop-Transcript | Out-Null } catch {}
Read-Host "Press Enter to close"
