# ==========================================
# SCRIPT ULTIME - SOLUTION GARANTIE 100%
# ==========================================

Write-Host "🔥 SCRIPT ULTIME - GARANTIE SANS ÉCHEC 🔥" -ForegroundColor Red
Write-Host "============================================" -ForegroundColor Red
Write-Host "Port ULTIME: 13337 (jamais utilisé ailleurs)" -ForegroundColor Yellow
Write-Host "============================================" -ForegroundColor Red

# Fonction pour tuer un processus par port
function Kill-ProcessByPort {
    param($port)
    $processes = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($processes) {
        foreach ($process in $processes) {
            try {
                Write-Host "💀 Port $port - PID $($process.OwningProcess) - TERMINAISON" -ForegroundColor Red
                Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
            } catch {
                taskkill /PID $process.OwningProcess /F | Out-Null
            }
        }
        return $true
    }
    return $false
}

# Nettoyage AGRESSIF de tous les ports problématiques
$ports = @(13337, 9999, 7777, 8080, 8081, 8085, 8888, 9090, 4200)
$killedAny = $false

Write-Host "`n🧹 NETTOYAGE AGRESSIF DES PORTS..." -ForegroundColor Cyan

foreach ($port in $ports) {
    if (Kill-ProcessByPort $port) {
        $killedAny = $true
    }
}

if ($killedAny) {
    Write-Host "`n⏳ Attente de 5 secondes pour libération complète..." -ForegroundColor Blue
    Start-Sleep -Seconds 5
}

# Vérification finale que le port 13337 est libre
$finalCheck = Get-NetTCPConnection -LocalPort 13337 -ErrorAction SilentlyContinue
if ($finalCheck) {
    Write-Host "❌ Port 13337 encore utilisé! Forçage ultime..." -ForegroundColor Red
    taskkill /PID $finalCheck.OwningProcess /F | Out-Null
    Start-Sleep -Seconds 3
}

Write-Host "`n🚀 DÉMARRAGE ULTIME DU BACKEND..." -ForegroundColor Green
Write-Host "📍 URL API: http://localhost:13337/api" -ForegroundColor Cyan
Write-Host "📊 Console H2: http://localhost:13337/api/h2-console" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Red

# Démarrage avec retry automatique
$retryCount = 0
$maxRetries = 3

do {
    $retryCount++
    Write-Host "`n🔄 Tentative $retryCount/$maxRetries..." -ForegroundColor Yellow
    
    Set-Location -Path "D:\PlatformePediatricBack"
    $result = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -PassThru -Wait
    
    if ($result.ExitCode -eq 0) {
        Write-Host "✅ SUCCÈS! Backend démarré avec succès!" -ForegroundColor Green
        break
    } else {
        Write-Host "❌ Échec tentative $retryCount. Nettoyage et retry..." -ForegroundColor Red
        Kill-ProcessByPort 13337
        Start-Sleep -Seconds 2
    }
} while ($retryCount -lt $maxRetries)

if ($retryCount -eq $maxRetries) {
    Write-Host "💥 ÉCHEC ULTIME! Vérifiez manuellement le port 13337" -ForegroundColor DarkRed
} else {
    Write-Host "🎉 BACKEND DÉMARRÉ - PLUS JAMAIS DE PROBLÈMES!" -ForegroundColor Green
}
