@echo off
setlocal enableextensions
cd /d "%~dp0"

set PORT=%1
if "%PORT%"=="" set PORT=8080

echo ==== Java & Maven Check (Dev) ====
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
if "%MVN_FOUND%"=="1" goto START_MVN
goto START_FALLBACK

:START_MVN
echo Maven detected. Starting hot reload via spring-boot:run ...
start "ShopDev" cmd /k mvn -q spring-boot:run -Dspring-boot.run.arguments=--server.port=%PORT%
goto END

:START_FALLBACK
echo Maven NOT found. Fallback to classpath run.
if not exist "target\classes" (
  echo Missing compiled classes. Please install Maven and run: mvn -q -DskipTests package
  goto END
)
if not exist "target\deps" (
  echo Missing runtime deps. Run: mvn -q dependency:copy-dependencies -DoutputDirectory=target\deps -DincludeScope=runtime
  goto END
)
echo Starting with classpath (devtools enabled if present)...
start "ShopDev" cmd /k java -cp "target\classes;target\deps\*" com.example.shop.ShopApplication --server.port=%PORT%
goto END

:END
echo Dev command launched.
pause
exit /b 0
