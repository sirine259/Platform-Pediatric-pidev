# ==========================================
# SCRIPT DEMARRAGE SERVICE PRINCIPAL
# Port: 8091
# ==========================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  DEMARRAGE KIDNEYTRANSPLANT-FORUM-SERVICE" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Verifier si le port 8091 est libre
$port = 8091
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
Write-Host "[INFO] Port Service: $port" -ForegroundColor Gray
Write-Host ""
Write-Host "[MODULES]" -ForegroundColor Yellow
Write-Host "  - Kidney Transplant" -ForegroundColor Gray
Write-Host "  - Forum" -ForegroundColor Gray
Write-Host "  - Medical Records" -ForegroundColor Gray
Write-Host "  - Authentification" -ForegroundColor Gray
Write-Host ""

# Demarrer le service principal
Set-Location -Path $projectDir
mvn spring-boot:run
