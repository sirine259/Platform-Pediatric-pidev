# Script PowerShell pour nettoyer les ports et démarrer le backend
Write-Host "=== Nettoyage des ports et démarrage du backend ===" -ForegroundColor Cyan

# Ports à nettoyer
$ports = @(8080, 8081, 8085, 8082)

foreach ($port in $ports) {
    $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($process) {
        Write-Host "Port $port utilisé par le processus $($process.OwningProcess)" -ForegroundColor Yellow
        try {
            Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
            Write-Host "✓ Processus $($process.OwningProcess) arrêté" -ForegroundColor Green
        } catch {
            Write-Host "✗ Impossible d'arrêter le processus: $_" -ForegroundColor Red
        }
    } else {
        Write-Host "✓ Port $port libre" -ForegroundColor Green
    }
}

Write-Host "`nAttente de 3 secondes pour la libération des ports..." -ForegroundColor Blue
Start-Sleep -Seconds 3

Write-Host "`n=== Démarrage du backend sur le port 8080 ===" -ForegroundColor Cyan

# Démarrer Maven
Set-Location -Path "D:\PlatformePediatricBack"
mvn spring-boot:run
