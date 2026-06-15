@echo off
title AutoAdvertising Suite - Control Panel

:: Configurazione Colori ANSI Standard (Massima compatibilità)
set "ESC="
for /F %%A in ('echo prompt $E ^| cmd') do set "ESC=%%A"

set "RESET=%ESC%[0m"
set "ROSSO=%ESC%[91m"
set "VERDE=%ESC%[92m"
set "ORO=%ESC%[93m"
set "AZZURRO=%ESC%[96m"
set "GRIGIO=%ESC%[90m"

echo.
echo  =============================================================
echo   %AZZURRO%AutoAdvertising Suite%RESET%
echo   %GRIGIO%Infrastruttura di Gestione Autorizzazioni Pubblicitarie%RESET%
echo  =============================================================
echo   %GRIGIO%Ente:%RESET% %ORO%Comune di Novellara%RESET%
echo   %GRIGIO%Sviluppo a cura di:%RESET% Riccardo Delbue
echo  =============================================================
echo.
echo  %GRIGIO%[SYSTEM]%RESET% Inizializzazione moduli...

:: Spostamento nella directory dello script
cd /d "%~dp0"

:: Diagnostica Ambiente Python
python --version >nul 2>&1
if errorlevel 1 (
    echo.
    echo  %ROSSO%[ERRORE]%RESET% Ambiente Python non rilevato nel sistema.
    echo  Si prega di verificare l'installazione di Python.
    echo.
    pause
    exit /b
)

:: Verifica file CSV
if not exist "autorizzazioni.csv" (
    echo.
    echo  %ROSSO%[ERRORE]%RESET% File 'autorizzazioni.csv' non trovato.
    echo  Verificare che il file sia presente nella cartella.
    echo.
    pause
    exit /b
)

echo  %VERDE%[OK]%RESET% Verifiche di sistema completate.
echo  %GRIGIO%[SYSTEM]%RESET% Avvio interfaccia web...

:: Boot Interfaccia Web (Browser) in background
PowerShell -Command "Start-Sleep 2; Start-Process 'http://localhost:8080'"

:: Start Web Server
echo.
echo  %VERDE%[ONLINE]%RESET% Server attivo su http://localhost:8080
echo  -------------------------------------------------------------
echo  %ORO%  Mantenere questa finestra aperta durante l'uso.%RESET%
echo  %GRIGIO%  Per arrestare il programma, chiudere questa finestra.%RESET%
echo  -------------------------------------------------------------
echo.

python server.py
pause