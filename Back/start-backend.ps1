# Script pour démarrer le backend sur le port 8080
# Vérifie si le port est utilisé et le libère si nécessaire

Write-Host "Vérification du port 8080..." -ForegroundColor Green

# Vérifier si le port 8080 est utilisé
$process = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

if ($process) {
    Write-Host "Port 8080 utilisé par le processus $($process.OwningProcess)" -ForegroundColor Yellow
    Write-Host "Arrêt du processus..." -ForegroundColor Red
    
    # Arrêter le processus
    Stop-Process -Id $process.OwningProcess -Force
    Write-Host "Processus arrêté" -ForegroundColor Green
    
    # Attendre un peu que le port se libère
    Start-Sleep -Seconds 2
}

Write-Host "Démarrage du backend sur le port 8080..." -ForegroundColor Green

# Démarrer l'application Spring Boot
mvn spring-boot:run
