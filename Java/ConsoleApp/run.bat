@echo off
cd /d "%~dp0"

set "JDK_HOME="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "JDK_HOME=%JAVA_HOME%"

if not defined JDK_HOME (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javac.exe" (
        set "JDK_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
    )
)

if not defined JDK_HOME (
    for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
        if exist "%%D\bin\javac.exe" set "JDK_HOME=%%D"
    )
)

if not defined JDK_HOME (
    echo [ERROR] JDK not found. Install JDK 25 or set JAVA_HOME.
    pause
    exit /b 1
)

if not exist "build\classes" mkdir "build\classes"
"%JDK_HOME%\bin\javac.exe" -encoding UTF-8 -cp "lib\mssql-jdbc.jar" -d "build\classes" src\*.java
if errorlevel 1 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)

"%JDK_HOME%\bin\java.exe" --enable-native-access=ALL-UNNAMED -Djava.library.path=lib -cp "build\classes;resources;lib\mssql-jdbc.jar" Main
