$ErrorActionPreference = "Stop"

function Get-LanIPv4 {
  $cfg = Get-NetIPConfiguration |
    Where-Object { $_.IPv4DefaultGateway -ne $null -and $_.IPv4Address -ne $null } |
    Select-Object -First 1

  if ($cfg -and $cfg.IPv4Address) {
    return $cfg.IPv4Address.IPAddress
  }
  return "127.0.0.1"
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$certDir = Join-Path $projectRoot "certs"
$caDir = Join-Path $certDir "ca"
$caKey = Join-Path $caDir "rootCA-key.pem"
$caPem = Join-Path $caDir "rootCA.pem"
$caCer = Join-Path $caDir "rootCA.cer"
$serverKey = Join-Path $certDir "dev-key.pem"
$serverCsr = Join-Path $certDir "dev.csr"
$serverCert = Join-Path $certDir "dev-cert.pem"
$serverExt = Join-Path $certDir "dev-cert.ext"
$serialFile = Join-Path $certDir "rootCA.srl"
$lanIp = Get-LanIPv4

if (!(Test-Path $certDir)) {
  New-Item -ItemType Directory -Path $certDir | Out-Null
}
if (!(Test-Path $caDir)) {
  New-Item -ItemType Directory -Path $caDir | Out-Null
}

Write-Host "Detected LAN IP: $lanIp"

if (!(Test-Path $caKey) -or !(Test-Path $caPem)) {
  Write-Host "Generating local CA..."
  & openssl req -x509 -nodes -newkey rsa:2048 `
    -keyout $caKey `
    -out $caPem `
    -days 3650 `
    -subj "/CN=EnglishAI Local Root CA" `
    -addext "basicConstraints=critical,CA:TRUE" `
    -addext "keyUsage=critical,keyCertSign,cRLSign" `
    -addext "subjectKeyIdentifier=hash" `
    -addext "authorityKeyIdentifier=keyid:always,issuer:always" | Out-Null
  Copy-Item $caPem $caCer -Force
}

Write-Host "Generating server cert for localhost + 127.0.0.1 + $lanIp ..."
& openssl req -nodes -newkey rsa:2048 `
  -keyout $serverKey `
  -out $serverCsr `
  -subj "/CN=$lanIp" | Out-Null

$extContent = @"
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage=digitalSignature,keyEncipherment
extendedKeyUsage=serverAuth
subjectAltName=DNS:localhost,IP:127.0.0.1,IP:$lanIp
"@
$extContent | Set-Content -Path $serverExt -Encoding ascii

& openssl x509 -req `
  -in $serverCsr `
  -CA $caPem `
  -CAkey $caKey `
  -CAcreateserial `
  -CAserial $serialFile `
  -out $serverCert `
  -days 825 `
  -sha256 `
  -extfile $serverExt | Out-Null

Write-Host "Server cert ready: $serverCert"
Write-Host "Root CA for iPhone trust: $caCer"
Write-Host "Starting Next.js on https://0.0.0.0:53000 ..."
$nextBin = Join-Path $projectRoot "node_modules/next/dist/bin/next"
& node $nextBin dev --webpack --experimental-https --experimental-https-key $serverKey --experimental-https-cert $serverCert -H 0.0.0.0 -p 53000

