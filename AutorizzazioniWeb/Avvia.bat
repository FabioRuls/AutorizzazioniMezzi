@echo off
title AutorizzazioniMezzi
echo.
echo  ==========================================
echo   AutorizzazioniMezzi - Avvio in corso...
echo  ==========================================
echo.

:: Vai nella cartella dello script
cd /d "%~dp0"

:: Controlla Python
python --version >nul 2>&1
if errorlevel 1 (
    echo ERRORE: Python non trovato.
    echo Installa Python da https://www.python.org/downloads/
    pause
    exit /b
)

:: Controlla CSV
if not exist "autorizzazioni.csv" (
    echo ATTENZIONE: file autorizzazioni.csv non trovato.
    echo Metti il file CSV in questa cartella e riavvia.
    pause
    exit /b
)

:: Apri il browser dopo 2 secondi in background
PowerShell -Command "Start-Sleep 2; Start-Process 'http://localhost:8080'"

:: Avvia il server
echo  Server avviato. NON chiudere questa finestra.
echo  Per fermare il programma chiudi questa finestra.
echo.
python server.py
pause
