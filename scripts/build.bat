@echo off
echo Building SmartHire WAR package...
cd /d "%~dp0\.."
mvn clean package
pause
