# ==========================================
# SCRIPT DEMARRAGE COMPLET (Eureka + Gateway + Service)
# ==========================================

Write-Host ""
Write-Host "==========================================" -ForegroundColor Magenta
Write-Host "  PLATEFORME PEDIATRIQUE - DEMARRAGE" -ForegroundColor Magenta
Write-Host "==========================================" -ForegroundColor Magenta
Write-Host ""

$ErrorActionPreference = "Continue"

# 1. VERIFIER LES PORTS
Write-Host "[1/3] Verification des ports..." -ForegroundColor Yellow

$ports = @(8761, 8080, 8091)
$portNames = @{
    8761 = "Eureka Server"
    8080 = "API Gateway"
    8091 = "KidneyTransplant Service"
}

foreach ($port in $ports) {
    $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($process) {
        Write-Host "  [PORT $port] Deja utilise - Arret..." -ForegroundColor Red
        Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 1
    } else {
        Write-Host "  [PORT $port] Libre" -ForegroundColor Green
    }
}

Write-Host ""

# 2. DEMARRER EUREKA
Write-Host "[2/3] Demarrage Eureka Server (port 8761)..." -ForegroundColor Cyan
Write-Host "      URL: http://localhost:8761" -ForegroundColor Gray
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'D:\Platform-Pediatric-pidev-pediatric-platform\Platform-Pediatric-pidev-pediatric-platform\Back\eureka-server'; mvn spring-boot:run" -WindowStyle Normal
Start-Sleep -Seconds 5

# 3. DEMARRER GATEWAY
Write-Host "[3/3] Demarrage API Gateway (port 8080)..." -ForegroundColor Cyan
Write-Host "      URL: http://localhost:8080" -ForegroundColor Gray
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'D:\Platform-Pediatric-pidev-pediatric-platform\Platform-Pediatric-pidev-pediatric-platform\Back\api-gateway'; mvn spring-boot:run" -WindowStyle Normal
Start-Sleep -Seconds 5

# 4. DEMARRER SERVICE PRINCIPAL
Write-Host "[4/3] Demarrage KidneyTransplant Service (port 8091)..." -ForegroundColor Cyan
Write-Host "      URL: http://localhost:8091" -ForegroundColor Gray
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'D:\Platform-Pediatric-pidev-pediatric-platform\Platform-Pediatric-pidev-pediatric-platform\Back'; mvn spring-boot:run" -WindowStyle Normal

Write-Host ""
Write-Host "==========================================" -ForegroundColor Magenta
Write-Host "  LIENS POUR NAVIGATEUR" -ForegroundColor Magenta
Write-Host "==========================================" -ForegroundColor Magenta
Write-Host ""
Write-Host "  EUREKA DASHBOARD:" -ForegroundColor Green
Write-Host "  http://localhost:8761" -ForegroundColor White
Write-Host ""
Write-Host "  KIDNEY TRANSPLANT (via Gateway):" -ForegroundColor Green
Write-Host "  http://localhost:8080/api/transplant/donors" -ForegroundColor White
Write-Host "  http://localhost:8080/api/transplant/recipients" -ForegroundColor White
Write-Host "  http://localhost:8080/api/transplant/transplants" -ForegroundColor White
Write-Host ""
Write-Host "  FORUM (via Gateway):" -ForegroundColor Green
Write-Host "  http://localhost:8080/api/forum/posts" -ForegroundColor White
Write-Host ""
Write-Host "  ACCES DIRECT (sans Gateway):" -ForegroundColor Yellow
Write-Host "  http://localhost:8091/api/transplant/donors" -ForegroundColor White
Write-Host "  http://localhost:8091/api/forum/posts" -ForegroundColor White
Write-Host "  http://localhost:8091/api/h2-console" -ForegroundColor White
Write-Host ""
Write-Host "  NOTE: Attendez 30-60 secondes que les services demarrent!" -ForegroundColor Red
Write-Host ""
