@echo off
echo Starting Database Server for Student Management System...
echo =========================================================

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Navigate to project directory
cd /d "%~dp0"

REM Check if the classes are compiled
if not exist "target\classes" if not exist "out" (
    echo Error: Project classes not found. Please compile the project first.
    echo Run: mvn compile
    pause
    exit /b 1
)

REM Set classpath
if exist "target\classes" (
    set CLASSPATH=target\classes
) else if exist "out" (
    set CLASSPATH=out
)

echo Using classpath: %CLASSPATH%
echo Starting server on port 44444...
echo Press Ctrl+C to stop the server
echo.

REM Start the server
java -cp "%CLASSPATH%" files.Server.DatabaseServer

echo.
echo Server stopped.
pause