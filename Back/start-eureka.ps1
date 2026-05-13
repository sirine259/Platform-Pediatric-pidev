# ==========================================
# SCRIPT DEMARRAGE EUREKA SERVER
# Port: 8761
# ==========================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  DEMARRAGE EUREKA SERVER" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Verifier si le port 8761 est libre
$port = 8761
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
Write-Host "[INFO] Port Eureka: $port" -ForegroundColor Gray
Write-Host "[INFO] URL Dashboard: http://localhost:$port" -ForegroundColor Green
Write-Host ""

# Demarrer Eureka Server
Set-Location -Path $projectDir
mvn spring-boot:run -pl eureka-server
