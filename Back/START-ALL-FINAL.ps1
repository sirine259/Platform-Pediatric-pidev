# ==========================================
# SCRIPT DEMARRAGE DEFINITIF - CORRIGE
# Tue tous les processus et redemarre les services
# ==========================================

$ErrorActionPreference = "Continue"

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  PLATEFORME PEDIATRIQUE - DEMARRAGE" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Ports a liberer
$ports = @(8761, 8080, 8091)

# 1. TUER LES PROCESSUS
Write-Host "[ETAPE 1] Liberation des ports..." -ForegroundColor Yellow

foreach ($port in $ports) {
    $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($process) {
        $pid = $process.OwningProcess
        Write-Host "  [PORT $port] Arret PID $pid..." -ForegroundColor Red
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
        Start-Sleep -Milliseconds 500
    } else {
        Write-Host "  [PORT $port] Libre" -ForegroundColor Green
    }
}

Write-Host ""

# Attendre un peu pour liberer les ports
Start-Sleep -Seconds 2

Write-Host "[ETAPE 2] Demarrage des services..." -ForegroundColor Yellow
Write-Host ""

$basePath = "D:\Platform-Pediatric-pidev-pediatric-platform\Platform-Pediatric-pidev-pediatric-platform"

# 2. DEMARRER EUREKA
Write-Host "  [1/3] Eureka Server (port 8761)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath\Back\eureka-server'; Write-Host ''; Write-Host '==========================================' -ForegroundColor Green; Write-Host '  EUREKA SERVER - http://localhost:8761' -ForegroundColor Green; Write-Host '==========================================' -ForegroundColor Green; mvn spring-boot:run" -WindowStyle Normal

# Attendre qu'Eureka demarre
Write-Host "    -> Attente 15 secondes..." -ForegroundColor Gray
Start-Sleep -Seconds 15

# 3. DEMARRER GATEWAY
Write-Host "  [2/3] API Gateway (port 8080)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath\Back\api-gateway'; Write-Host ''; Write-Host '==========================================' -ForegroundColor Green; Write-Host '  API GATEWAY - http://localhost:8080' -ForegroundColor Green; Write-Host '==========================================' -ForegroundColor Green; mvn spring-boot:run" -WindowStyle Normal

# Attendre que Gateway demarre
Write-Host "    -> Attente 15 secondes..." -ForegroundColor Gray
Start-Sleep -Seconds 15

# 4. DEMARRER SERVICE PRINCIPAL (8091)
Write-Host "  [3/3] KidneyTransplant Service (port 8091)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath\Back'; Write-Host ''; Write-Host '==========================================' -ForegroundColor Green; Write-Host '  SERVICE PRINCIPAL - http://localhost:8091' -ForegroundColor Green; Write-Host '==========================================' -ForegroundColor Green; mvn spring-boot:run" -WindowStyle Normal

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "  SERVICES DEMARRES!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "  LIENS NAVIGATEUR:" -ForegroundColor White
Write-Host ""
Write-Host "  ┌─────────────────────────────────────────────┐" -ForegroundColor White
Write-Host "  │  EUREKA DASHBOARD:  http://localhost:8761  │" -ForegroundColor Yellow
Write-Host "  │  API GATEWAY:       http://localhost:8080  │" -ForegroundColor Yellow
Write-Host "  │  SERVICE DIRECT:    http://localhost:8091  │" -ForegroundColor Yellow
Write-Host "  └─────────────────────────────────────────────┘" -ForegroundColor White
Write-Host ""
Write-Host "  APIS KIDNEY TRANSPLANT (via Gateway):" -ForegroundColor White
Write-Host "    http://localhost:8080/api/transplant/donors" -ForegroundColor Gray
Write-Host "    http://localhost:8080/api/transplant/recipients" -ForegroundColor Gray
Write-Host "    http://localhost:8080/api/transplant/transplants" -ForegroundColor Gray
Write-Host ""
Write-Host "  APIS FORUM (via Gateway):" -ForegroundColor White
Write-Host "    http://localhost:8080/api/forum/posts" -ForegroundColor Gray
Write-Host ""
Write-Host "  H2 CONSOLE:" -ForegroundColor White
Write-Host "    http://localhost:8091/api/h2-console" -ForegroundColor Gray
Write-Host ""
Write-Host "  IMPORTANT: Attendez 60 secondes!" -ForegroundColor Red
Write-Host ""
