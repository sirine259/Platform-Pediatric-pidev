# ==========================================
# SCRIPT DÉFINITIF - BACKEND PÉDIATRIE PORT 7777
# ==========================================

Write-Host "🏥 DÉMARRAGE BACKEND PÉDIATRIQUE PORT 7777 🏥" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Vérifier et arrêter les processus sur les ports 7777, 8080, 8081, 8888, 9090
$ports = @(7777, 8080, 8081, 8888, 9090)

foreach ($port in $ports) {
    $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($process) {
        Write-Host "🔄 Port $port utilisé par PID $($process.OwningProcess) - Arrêt..." -ForegroundColor Yellow
        taskkill /PID $process.OwningProcess /F | Out-Null
        Write-Host "✅ Processus arrêté" -ForegroundColor Green
    }
}

Write-Host "`n🚀 Démarrage du backend sur le port 7777..." -ForegroundColor Green
Write-Host "📍 URL API: http://localhost:7777/api" -ForegroundColor Blue
Write-Host "📊 Console H2: http://localhost:7777/api/h2-console" -ForegroundColor Blue
Write-Host "========================================" -ForegroundColor Cyan

# Démarrer le backend
Set-Location -Path "D:\PlatformePediatricBack"
mvn spring-boot:run
