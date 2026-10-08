@echo off
title SmartHire Local Server

cd /d "%~dp0"

echo Starting SmartHire...
start /B mvn exec:java

timeout /t 8 /nobreak >nul

start "" "http://localhost:8080/"

pause