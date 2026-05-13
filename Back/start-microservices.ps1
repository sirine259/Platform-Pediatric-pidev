# Start all microservices for Pediatric Platform
# Run this script from the Back folder

$ErrorActionPreference = "Stop"

$backFolder = "C:\Platform-Pediatric-pidev-pediatric-platform\Platform-Pediatric-pidev-pediatric-platform\Back"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Pediatric Platform Microservices" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Start Eureka Server (port 8761)
Write-Host "`n[1/3] Starting Eureka Server on port 8761..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backFolder\eureka-server'; .\mvnw spring-boot:run" -WindowStyle Normal
Start-Sleep -Seconds 10

# 2. Start Backend (kidneytransplant + forum) on port 8091
Write-Host "`n[2/3] Starting Backend Service on port 8091..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backFolder'; .\mvnw spring-boot:run" -WindowStyle Normal
Start-Sleep -Seconds 15

# 3. Start API Gateway on port 8080
Write-Host "`n[3/3] Starting API Gateway on port 8080..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backFolder\api-gateway'; .\mvnw spring-boot:run" -WindowStyle Normal

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "All microservices started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "API Gateway:      http://localhost:8080" -ForegroundColor White
Write-Host "Backend API:     http://localhost:8091" -ForegroundColor White
Write-Host "`nTest endpoints:" -ForegroundColor White
Write-Host "  - http://localhost:8080/api/transplant/" -ForegroundColor Cyan
Write-Host "  - http://localhost:8080/api/forum/" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Green
