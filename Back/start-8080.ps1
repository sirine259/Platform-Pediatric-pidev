# ==========================================
# SCRIPT PORT 8080 - SOLUTION STANDARD
# ==========================================

Write-Host "🚀 DÉMARRAGE BACKEND PORT 8080" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green

# Nettoyer le port 8080
$process = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($process) {
    Write-Host "🔄 Port 8080 utilisé par PID $($process.OwningProcess) - Arrêt..." -ForegroundColor Yellow
    taskkill /PID $process.OwningProcess /F | Out-Null
    Write-Host "✅ Processus arrêté" -ForegroundColor Green
    Start-Sleep -Seconds 3
}

Write-Host "`n🚀 Démarrage du backend sur le port 8080..." -ForegroundColor Green
Write-Host "📍 URL API: http://localhost:8080/api" -ForegroundColor Blue
Write-Host "📊 Console H2: http://localhost:8080/api/h2-console" -ForegroundColor Blue
Write-Host "================================" -ForegroundColor Green

# Démarrer le backend
Set-Location -Path "D:\PlatformePediatricBack"
mvn spring-boot:run
