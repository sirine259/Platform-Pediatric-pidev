# ==========================================
# SCRIPT DÉFINITIF - SOLUTION PERMANENTE
# ==========================================

Write-Host "🔥 SCRIPT DÉFINITIF - BACKEND PÉDIATRIE PORT 9999 🔥" -ForegroundColor Red
Write-Host "================================================" -ForegroundColor Red
Write-Host "Ce script résout TOUS les problèmes de ports" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Red

# TOUS les ports problématiques
$allPorts = @(9999, 7777, 8080, 8081, 8085, 8888, 9090, 4200)

Write-Host "`n🧹 NETTOYAGE COMPLET DES PORTS..." -ForegroundColor Cyan

foreach ($port in $allPorts) {
    $processes = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($processes) {
        foreach ($process in $processes) {
            try {
                Write-Host "❌ Port $port utilisé par PID $($process.OwningProcess) - ARRÊT..." -ForegroundColor Red
                Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
                Write-Host "✅ Processus $($process.OwningProcess) arrêté" -ForegroundColor Green
            } catch {
                Write-Host "⚠️  Forçage de l'arrêt du PID $($process.OwningProcess)..." -ForegroundColor Yellow
                taskkill /PID $process.OwningProcess /F | Out-Null
            }
        }
    } else {
        Write-Host "✅ Port $port déjà libre" -ForegroundColor Green
    }
}

Write-Host "`n⏳ Attente de libération des ports..." -ForegroundColor Blue
Start-Sleep -Seconds 5

Write-Host "`n🚀 DÉMARRAGE DÉFINITIF DU BACKEND..." -ForegroundColor Green
Write-Host "📍 URL API: http://localhost:9999/api" -ForegroundColor Cyan
Write-Host "📊 Console H2: http://localhost:9999/api/h2-console" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Red

# Démarrage définitif
Set-Location -Path "D:\PlatformePediatricBack"
mvn spring-boot:run
