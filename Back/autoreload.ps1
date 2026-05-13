# ==========================================
# SCRIPT AUTO-RELOAD - MODIFICATIONS AUTOMATIQUES
# ==========================================

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "  MODE AUTO-RELOAD ACTIVE" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "  Les modifications de code seront appliquees" -ForegroundColor Yellow
Write-Host "  automatiquement (hot reload)!" -ForegroundColor Yellow
Write-Host ""
Write-Host "  Ctrl+C pour arreter" -ForegroundColor Red
Write-Host ""

# Demarrer le service avec hot reload
cd "D:\Platform-Pediatric-pidev-pediatric-platform\Platform-Pediatric-pidev-pediatric-platform\Back"
mvn spring-boot:run -Dspring-boot.run.fork=false
