@echo off
echo Running SmartHire Automated Verification Suite...
cd /d "%~dp0\.."

echo.
echo === 1. Code Quality Smoke Test ===
mvn exec:java "-Dexec.mainClass=util.CodeQualitySmokeTest"

echo.
echo === 2. Route Smoke Test ===
mvn exec:java "-Dexec.mainClass=util.RouteSmokeTest"

echo.
echo === 3. Full Authentication Flow Test ===
mvn exec:java "-Dexec.mainClass=util.FullAuthenticationFlowTest"

echo.
echo === 4. DAO Layer Test ===
mvn exec:java "-Dexec.mainClass=util.DAOTest"

echo.
echo === 5. Service Layer Test ===
mvn exec:java "-Dexec.mainClass=util.ServiceTest"

echo.
echo === 6. Multithreading Test ===
mvn exec:java "-Dexec.mainClass=util.MultithreadingTest"

echo.
echo === 7. Transaction Test ===
mvn exec:java "-Dexec.mainClass=util.TransactionTest"

echo.
echo === 8. Collections & Generics Test ===
mvn exec:java "-Dexec.mainClass=util.CollectionsGenericsTest"

pause
