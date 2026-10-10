@echo off
REM MetroMind - build and run (Windows). Needs JDK 11 or newer (java and javac on PATH).
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
  echo.
  echo Compilation failed. Check that a JDK is installed:  javac -version
  pause
  exit /b 1
)
start "" http://localhost:8080
java -cp out metromind.Main 8080
pause
