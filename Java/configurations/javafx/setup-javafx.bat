@echo off
REM Manual JavaFX Setup Script for Windows
REM This script helps you set up JavaFX if automatic download doesn't work

setlocal enabledelayedexpansion

echo.
echo ========================================
echo   JavaFX 25 Manual Setup for Java 25.0.3
echo ========================================
echo.

if exist ..\javafx-25-sdk (
    if exist ..\javafx-25-sdk\lib (
        echo [OK] javafx-25-sdk folder already exists with lib directory
        echo Skipping download and extraction
        echo.
    ) else (
        echo [WARNING] javafx-25-sdk folder exists but lib directory is missing
        echo Please delete javafx-25-sdk folder and re-run this script
        pause
        exit /b 1
    )
) else (
    echo [INFO] JavaFX SDK not found locally
    echo.
    echo Please download manually from:
    echo https://gluonhq.com/download/javafx-25/
    echo.
    echo Steps:
    echo 1. Click "Windows x64 SDK" button
    echo 2. Extract the ZIP file
    echo 3. Rename the extracted folder to: javafx-25-sdk
    echo 4. Place it in: !CD!\..\ (configurations folder parent)
    echo    That should be: ..\..\configurations\javafx-25-sdk
    echo.
    echo Your current location: !CD!\
    echo Target location: !CD!\..\javafx-25-sdk\
    echo.
    echo After extraction, re-run this script and choose option 2 or 3
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Compile and Run Options
echo ========================================
echo.
echo Option 1: Compile only
echo Option 2: Compile and Run
echo Option 3: Clean compiled files
echo.

set /p choice="Choose an option (1-3): "

if "%choice%"=="1" (
    echo.
    echo Compiling Main.java...
    javac --module-path ..\javafx-25-sdk\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics ..\..\Main.java
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Compilation successful!
    ) else (
        echo [ERROR] Compilation failed!
        echo Check that:
        echo - JDK 25 is installed and in PATH
        echo - javafx-25-sdk\lib contains JAR files
        pause
        exit /b 1
    )
) else if "%choice%"=="2" (
    echo.
    echo Compiling Main.java...
    javac --module-path ..\javafx-25-sdk\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics ..\..\Main.java
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Compilation successful!
        echo.
        echo Running Main...
        cd ..\.. && java --module-path configurations\javafx-25-sdk\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics Main
        if %ERRORLEVEL% NEQ 0 (
            echo [ERROR] Runtime error occurred!
            pause
        )
    ) else (
        echo [ERROR] Compilation failed!
        echo Check that:
        echo - JDK 25 is installed and in PATH
        echo - javafx-25-sdk\lib contains JAR files
        pause
        exit /b 1
    )
) else if "%choice%"=="3" (
    echo.
    echo Cleaning compiled files...
    del ..\..\*.class 2>nul
    echo [OK] Clean complete!
) else (
    echo [ERROR] Invalid option!
    exit /b 1
)
