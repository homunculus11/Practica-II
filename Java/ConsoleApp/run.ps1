$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$jdkHome = $env:JAVA_HOME
if (-not $jdkHome -or -not (Test-Path "$jdkHome\bin\javac.exe")) {
    $known = "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
    if (Test-Path "$known\bin\javac.exe") {
        $jdkHome = $known
    } else {
        $jdkHome = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -Filter "jdk-*" -ErrorAction SilentlyContinue |
            Where-Object { Test-Path "$($_.FullName)\bin\javac.exe" } |
            Select-Object -First 1 -ExpandProperty FullName
    }
}

if (-not $jdkHome) {
    Write-Host "[ERROR] JDK not found. Install JDK 25 or set JAVA_HOME." -ForegroundColor Red
    exit 1
}

New-Item -ItemType Directory -Force -Path "build\classes" | Out-Null
& "$jdkHome\bin\javac.exe" -encoding UTF-8 -cp "lib\mssql-jdbc.jar" -d "build\classes" src\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
    exit 1
}

& "$jdkHome\bin\java.exe" --enable-native-access=ALL-UNNAMED "-Djava.library.path=lib" -cp "build\classes;resources;lib\mssql-jdbc.jar" Main
