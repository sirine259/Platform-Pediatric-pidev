# Script PowerShell pour démarrer le backend pédiatrique sur le port 9090
# Solution permanente - ne change jamais de port

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "DÉMARRAGE BACKEND PÉDIATRIQUE PORT 9090" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Vérifier si le port 9090 est utilisé
$port = 9090
$process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue

if ($process) {
    Write-Host "⚠️  Port $port déjà utilisé par le processus $($process.OwningProcess)" -ForegroundColor Yellow
    Write-Host "🔄 Arrêt du processus existant..." -ForegroundColor Red
    
    try {
        Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
        Write-Host "✅ Processus arrêté avec succès" -ForegroundColor Green
    } catch {
        Write-Host "❌ Impossible d'arrêter le processus: $_" -ForegroundColor Red
        Write-Host "🔄 Tentative de fermeture forcée..." -ForegroundColor Yellow
        taskkill /PID $process.OwningProcess /F
    }
    
    # Attendre que le port se libère
    Start-Sleep -Seconds 3
}

# Vérifier que le port est maintenant libre
$process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
if ($process) {
    Write-Host "❌ Le port $port est toujours utilisé. Arrêt manuel requis." -ForegroundColor Red
    Write-Host "Exécutez: taskkill /PID $($process.OwningProcess) /F" -ForegroundColor Yellow
    Read-Host "Appuyez sur Entrée après avoir arrêté le processus manuellement"
}

Write-Host "🚀 Démarrage du backend sur le port $port..." -ForegroundColor Green
Write-Host "📍 URL: http://localhost:$port/api" -ForegroundColor Blue
Write-Host "📊 Console H2: http://localhost:$port/api/h2-console" -ForegroundColor Blue
Write-Host "========================================" -ForegroundColor Cyan

# Changer de répertoire et démarrer
Set-Location -Path "D:\PlatformePediatricBack"

# Démarrer Maven Spring Boot
mvn spring-boot:run
