# ==========================================
# SCRIPT DEMARRAGE API GATEWAY
# Port: 8080
# ==========================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  DEMARRAGE API GATEWAY" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Verifier si le port 8080 est libre
$port = 8080
$process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue

if ($process) {
    Write-Host "[INFO] Port $port deja utilise par PID $($process.OwningProcess)" -ForegroundColor Yellow
    Write-Host "[ACTION] Arret du processus..." -ForegroundColor Yellow
    Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectDir = Split-Path -Parent $scriptDir

Write-Host "[INFO] Repertoire projet: $projectDir" -ForegroundColor Gray
Write-Host "[INFO] Port Gateway: $port" -ForegroundColor Gray
Write-Host "[INFO] URL Gateway: http://localhost:$port" -ForegroundColor Green
Write-Host ""
Write-Host "[ROUTES]" -ForegroundColor Yellow
Write-Host "  /api/transplant/** -> kidneytransplant-forum-service" -ForegroundColor Gray
Write-Host "  /api/forum/**      -> kidneytransplant-forum-service" -ForegroundColor Gray
Write-Host ""

# Demarrer API Gateway
Set-Location -Path $projectDir
mvn spring-boot:run -pl api-gateway
