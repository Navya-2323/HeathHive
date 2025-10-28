@echo off
echo ========================================
echo   Maven Installation Script
echo ========================================
echo.

REM Create Maven directory
if not exist "C:\Program Files\Apache\maven" mkdir "C:\Program Files\Apache\maven"

echo Downloading Maven...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'}"

if not exist "maven.zip" (
    echo Download failed. Please download Maven manually from:
    echo https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Extracting Maven...
powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath 'C:\Program Files\Apache\' -Force"

echo Setting up environment variables...
setx MAVEN_HOME "C:\Program Files\Apache\apache-maven-3.9.6" /M
setx PATH "%PATH%;C:\Program Files\Apache\apache-maven-3.9.6\bin" /M

echo Cleaning up...
del maven.zip

echo.
echo ========================================
echo   Maven Installation Complete!
echo ========================================
echo.
echo Maven has been installed to: C:\Program Files\Apache\apache-maven-3.9.6
echo.
echo IMPORTANT: Please restart Command Prompt or PowerShell for changes to take effect.
echo.
echo After restarting, you can run:
echo   mvn --version
echo   mvn spring-boot:run
echo.
pause
