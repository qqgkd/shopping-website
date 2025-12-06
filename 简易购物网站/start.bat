@echo off
setlocal enableextensions
cd /d "%~dp0"

set PORT=%1
if "%PORT%"=="" set PORT=8080

echo ==== Java & Maven Check ====
where java >nul 2>nul || (
  echo JAVA not found. Trying fallback...
  set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot"
  if exist "%JAVA_HOME%\bin\java.exe" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
  ) else (
    echo Fallback JDK not found. Please install Java 17.
  )
)
java -version

echo ==== Cleanup port %PORT% ====
powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-NetTCPConnection -LocalPort %PORT% -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess | Sort-Object -Unique | ForEach-Object { try { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue } catch {} }"
timeout /t 1 >nul

echo ==== Detect Maven ====
set MVN_FOUND=0
where mvn >nul 2>nul && set MVN_FOUND=1

if "%MVN_FOUND%"=="1" goto BUILD_WITH_MVN
goto FALLBACK_RUN

:BUILD_WITH_MVN
echo Preparing artifact with Maven...
if exist "target\shop-app.jar" del /q /f "target\shop-app.jar"
if exist "target\shop-app.jar.original" del /q /f "target\shop-app.jar.original"
call mvn -q -DskipTests package
if not %errorlevel%==0 (
  echo Maven build failed.
  pause
  exit /b 1
)
if not exist "target\shop-app.jar" (
  echo JAR not generated. Check build logs.
  pause
  exit /b 1
)
echo ==== Start Spring Boot (JAR, port %PORT%) ====
start "ShopServer" cmd /k java -jar "target\shop-app.jar" --server.port=%PORT%
echo Server window launched.
pause
exit /b 0

:FALLBACK_RUN
echo Maven NOT found. Trying classpath fallback...
if not exist "target\classes" (
  echo Missing compiled classes. Please install Maven and run: mvn -q -DskipTests package
  pause
  exit /b 1
)
if not exist "target\deps" (
  echo Missing runtime deps. Run: mvn -q dependency:copy-dependencies -DoutputDirectory=target\deps -DincludeScope=runtime
  pause
  exit /b 1
)
echo ==== Start (Classpath, port %PORT%) ====
start "ShopServer" cmd /k java -cp "target\classes;target\deps\*" com.example.shop.ShopApplication --server.port=%PORT%
echo Server window launched.
pause
exit /b 0
