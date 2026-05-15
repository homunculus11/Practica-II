#!/usr/bin/env powershell

# Navigate to project root
Set-Location $PSScriptRoot\..

# Function to find Java executable
function Find-JavaHome {
    # First check JAVA_HOME environment variable
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        return $env:JAVA_HOME
    }
    
    # Search in common Eclipse Adoptium locations
    $adoptiumPaths = @(
        "C:\Program Files\Eclipse Adoptium\jdk-25*",
        "C:\Program Files\Eclipse Adoptium\jdk-21*",
        "C:\Program Files\Eclipse Adoptium\jdk-17*",
        "C:\Program Files (x86)\Eclipse Adoptium\jdk-25*"
    )
    
    foreach ($path in $adoptiumPaths) {
        $found = Get-ChildItem -Path (Split-Path $path) -Filter (Split-Path $path -Leaf) -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found -and (Test-Path "$($found.FullName)\bin\java.exe")) {
            return $found.FullName
        }
    }
    
    # Search in common Microsoft JDK locations
    $microsoftPaths = @(
        "C:\Program Files\Microsoft\jdk-25*",
        "C:\Program Files\Microsoft\jdk-21*",
        "C:\Program Files\Microsoft\jdk-17*"
    )
    
    foreach ($path in $microsoftPaths) {
        $found = Get-ChildItem -Path (Split-Path $path) -Filter (Split-Path $path -Leaf) -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found -and (Test-Path "$($found.FullName)\bin\java.exe")) {
            return $found.FullName
        }
    }
    
    # Check user profile
    $userPaths = @(
        "$env:USERPROFILE\jdk-25*",
        "$env:USERPROFILE\jdk-21*",
        "$env:USERPROFILE\jdk-17*"
    )
    
    foreach ($path in $userPaths) {
        $found = Get-Item -Path $path -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found -and (Test-Path "$($found.FullName)\bin\java.exe")) {
            return $found.FullName
        }
    }
    
    # Try to find Java in PATH
    try {
        $javaPath = Get-Command java -ErrorAction Stop | Select-Object -ExpandProperty Source
        if ($javaPath) {
            $javaDir = Split-Path (Split-Path $javaPath)
            return $javaDir
        }
    } catch {
        # Java not in PATH
    }
    
    return $null
}

# Find Java
$javaHome = Find-JavaHome

if (-not $javaHome -or -not (Test-Path "$javaHome\bin\java.exe") -or -not (Test-Path "$javaHome\bin\javac.exe")) {
    Write-Host ""
    Write-Host "============================================================================" -ForegroundColor Red
    Write-Host "[ERROR] JDK 25 not found and JAVA_HOME is not set" -ForegroundColor Red
    Write-Host "============================================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Your system needs JDK 25 to run this JavaFX application." -ForegroundColor Yellow
    Write-Host "Required Java version: 25 or later"
    Write-Host ""
    Write-Host "SOLUTION OPTIONS:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Option 1: Set JAVA_HOME Environment Variable"
    Write-Host "  1. Install JDK 25 from: https://adoptium.net/temurin/releases/"
    Write-Host "  2. After installation, set environment variable:"
    Write-Host "     - Open PowerShell as Administrator"
    Write-Host "     - Run: [Environment]::SetEnvironmentVariable('JAVA_HOME','C:\Path\To\Your\JDK','Machine')"
    Write-Host "  3. Restart PowerShell and run this script again"
    Write-Host ""
    Write-Host "Option 2: Use Windows batch script"
    Write-Host "  Run: .\run.bat"
    Write-Host "  It has better automatic JDK detection"
    Write-Host ""
    exit 1
}

Write-Host "Using Java from: $javaHome" -ForegroundColor Green
Write-Host ""
Write-Host "Compiling Java sources..." -ForegroundColor Cyan
& "$javaHome\bin\javac" -encoding UTF-8 --module-path configurations\javafx-25-sdk\lib --add-modules javafx.controls --class-path "mssql-jdbc.jar" *.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    Write-Host "" -ForegroundColor Red
    Write-Host "Possible issues:" -ForegroundColor Yellow
    Write-Host "1. JavaFX SDK not found" -ForegroundColor Yellow
    Write-Host "   - Download from: https://gluonhq.com/download/javafx-25/" -ForegroundColor Yellow
    Write-Host "   - Extract to: configurations\javafx-25-sdk" -ForegroundColor Yellow
    Write-Host "" -ForegroundColor Yellow
    Write-Host "2. Missing lib folder" -ForegroundColor Yellow
    Write-Host "   - Check: configurations\javafx-25-sdk\lib\ contains JAR files" -ForegroundColor Yellow
    Write-Host "" -ForegroundColor Yellow
    Write-Host "3. SQL Server JDBC driver not found" -ForegroundColor Yellow
    Write-Host "   - Check: ..\mssql-jdbc.jar exists" -ForegroundColor Yellow
    exit 1
}

Write-Host "[OK] Compilation successful!" -ForegroundColor Green
Write-Host "Running Main..." -ForegroundColor Cyan
Write-Host ""

& "$javaHome\bin\java" "-Djava.library.path=..\z-Others;..\sqljdbc_13.4\enu\auth\x64" --module-path configurations\javafx-25-sdk\lib --add-modules javafx.controls --class-path "mssql-jdbc.jar;." Main
