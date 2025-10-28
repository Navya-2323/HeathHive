@echo off
echo ========================================
echo   Automated Java Installation Helper
echo ========================================
echo.

REM Check if running as administrator
net session >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo This script needs to be run as Administrator.
    echo Right-click and select "Run as administrator"
    pause
    exit /b 1
)

echo This script will help you install Java 17 automatically.
echo.
echo Options:
echo 1. Download and install Java 17 using Chocolatey
echo 2. Download Java 17 manually (opens browser)
echo 3. Check current Java installation
echo 4. Exit
echo.
set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" goto :chocolatey
if "%choice%"=="2" goto :manual
if "%choice%"=="3" goto :check
if "%choice%"=="4" goto :end
goto :invalid

:chocolatey
echo.
echo Checking if Chocolatey is installed...
choco --version >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo Chocolatey found! Installing Java 17...
    choco install openjdk17 -y
    echo.
    echo Java installation complete!
    echo Setting JAVA_HOME...
    for /f "tokens=*" %%i in ('where java') do set JAVA_PATH=%%i
    set JAVA_HOME=%JAVA_PATH:~0,-10%
    setx JAVA_HOME "%JAVA_HOME%" /M
    echo JAVA_HOME set to: %JAVA_HOME%
    echo.
    echo Please restart Command Prompt and run the project.
) else (
    echo Chocolatey is not installed.
    echo Installing Chocolatey first...
    powershell -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"
    echo.
    echo Chocolatey installed! Now installing Java 17...
    choco install openjdk17 -y
)
goto :end

:manual
echo.
echo Opening Java download page...
start https://adoptium.net/temurin/releases/
echo.
echo Please:
echo 1. Download Java 17 (LTS) for Windows x64
echo 2. Install it
echo 3. Set JAVA_HOME environment variable
echo 4. Add %%JAVA_HOME%%\bin to your PATH
echo.
echo See JAVA_INSTALLATION.md for detailed instructions.
goto :end

:check
echo.
echo Checking Java installation...
java -version 2>nul
if %ERRORLEVEL% equ 0 (
    echo Java is installed!
    java -version
    echo.
    echo Checking JAVA_HOME...
    if defined JAVA_HOME (
        echo JAVA_HOME is set to: %JAVA_HOME%
    ) else (
        echo JAVA_HOME is not set!
        echo Please set JAVA_HOME environment variable.
    )
) else (
    echo Java is not installed or not in PATH.
)
goto :end

:invalid
echo Invalid choice! Please try again.
goto :end

:end
echo.
echo Script completed.
pause
