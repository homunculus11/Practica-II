@echo off
REM JavaFX Runner - Execute from project root
REM Features: Auto-detects JDK 25, can install from bundled MSI if needed

cd /d "%~dp0\.."

set "JDK_HOME="
set "LOCAL_JDK_INSTALLER="

REM Check if JAVA_HOME is set and valid
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JDK_HOME=%JAVA_HOME%"
        goto :compile
    )
)

REM Try common installation paths for JDK 25
REM Check Eclipse Adoptium JDK 25 (most common)
if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot" (
    set "JDK_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
)
if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.4_8-hotspot" (
    set "JDK_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.4_8-hotspot"
)

REM Check 64-bit Program Files for Eclipse Adoptium
if not defined JDK_HOME (
    pushd "C:\Program Files\Eclipse Adoptium" 2>nul
    for /d %%D in (jdk-25*) do (
        if exist "%%D\bin\java.exe" (
            set "JDK_HOME=C:\Program Files\Eclipse Adoptium\%%D"
        )
    )
    popd
)

REM Check local JDK installation (from previous bundled install)
if not defined JDK_HOME (
    if exist "configurations\jdk-25\bin\java.exe" (
        set "JDK_HOME=configurations\jdk-25"
    )
)

REM Check Microsoft JDK
if not defined JDK_HOME (
    if exist "C:\Program Files\Microsoft\jdk-25.0.0.7-hotspot" (
        set "JDK_HOME=C:\Program Files\Microsoft\jdk-25.0.0.7-hotspot"
    )
)

REM Check 32-bit Program Files
if not defined JDK_HOME (
    pushd "C:\Program Files (x86)\Eclipse Adoptium" 2>nul
    for /d %%D in (jdk-25*) do (
        if exist "%%D\bin\java.exe" (
            set "JDK_HOME=C:\Program Files (x86)\Eclipse Adoptium\%%D"
        )
    )
    popd
)

REM Check user home directory
if not defined JDK_HOME (
    pushd "%USERPROFILE%" 2>nul
    for /d %%D in (jdk-25*) do (
        if exist "%%D\bin\java.exe" (
            set "JDK_HOME=%USERPROFILE%\%%D"
        )
    )
    popd
)

REM If still not found, look for MSI installer in jdk-local folder
if not defined JDK_HOME (
    if exist "configurations\jdk-local" (
        for %%F in (configurations\jdk-local\*.msi) do (
            if exist "%%F" (
                set "LOCAL_JDK_INSTALLER=%%F"
                goto :install_jdk
            )
        )
    )
)

REM JDK not found anywhere
if not defined JDK_HOME goto :error_no_jdk

:compile
set "JAVAC_CMD=%JDK_HOME%\bin\javac.exe"
set "JAVA_CMD=%JDK_HOME%\bin\java.exe"

if not exist "%JAVAC_CMD%" (
    echo [ERROR] javac not found at: %JAVAC_CMD%
    exit /b 1
)

if not exist "%JAVA_CMD%" (
    echo [ERROR] java not found at: %JAVA_CMD%
    exit /b 1
)

echo.
echo Using JDK: %JDK_HOME%
echo.
echo Compiling Java sources...
"%JAVAC_CMD%" -encoding UTF-8 --module-path configurations\javafx-25-sdk\lib --add-modules javafx.controls --class-path "mssql-jdbc.jar" *.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    echo.
    echo Possible issues:
    echo 1. JavaFX SDK not found
    echo    - Download from: https://gluonhq.com/download/javafx-25/
    echo    - Extract to: configurations\javafx-25-sdk
    echo.
    echo 2. Missing lib folder
    echo    - Check: configurations\javafx-25-sdk\lib\ contains JAR files
    echo.
    echo 3. SQL Server JDBC driver not found
    echo    - Check: ..\mssql-jdbc.jar exists
    echo.
    pause
    exit /b 1
)

echo.
echo [OK] Compilation successful!
echo.
echo Running Main...
echo.

"%JAVA_CMD%" -Djava.library.path="..\z-Others;..\sqljdbc_13.4\enu\auth\x64" --module-path configurations\javafx-25-sdk\lib --add-modules javafx.controls --class-path "mssql-jdbc.jar;." Main
exit /b %ERRORLEVEL%

:install_jdk
echo.
echo ============================================================================
echo [INFO] Installing JDK 25 from bundled installer...
echo ============================================================================
echo.
echo Installer found: %LOCAL_JDK_INSTALLER%
echo Note: Adoptium JDK MSI installs to default Program Files location
echo.

REM Run MSI installer silently (Adoptium MSI ignores INSTALLDIR)
echo [INFO] Running installer (this may take a few minutes)...
msiexec /i "%LOCAL_JDK_INSTALLER%" /qn /norestart

if %ERRORLEVEL% neq 0 (
    echo.
    echo [WARNING] MSI installation returned code %ERRORLEVEL%
    echo Attempting to continue...
)

REM Find the installed JDK in Program Files
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
    if exist "%%D\bin\java.exe" (
        set "JDK_HOME=%%D"
        goto :verify_install
    )
)

:verify_install
REM Verify installation
if defined JDK_HOME (
    if exist "%JDK_HOME%\bin\java.exe" (
        echo [OK] JDK 25 installed successfully!
        echo JDK location: %JDK_HOME%
        goto :compile
    )
)
    echo.
    echo [ERROR] JDK 25 installation failed
    echo Checked for JDK in: C:\Program Files\Eclipse Adoptium\jdk-*
    echo No valid JDK installation found.
    echo.
    echo Possible solutions:
    echo 1. Verify the MSI installer is valid and for Windows
    echo 2. Try manual installation from: https://adoptium.net/temurin/releases/
    echo 3. Check that "%CD%\configurations\jdk-local\" contains a valid JDK 25 MSI file
    echo 4. Ensure you have administrator privileges for installation
    echo.
    pause
    exit /b 1
)

:error_no_jdk
echo.
echo ============================================================================
echo [ERROR] JDK 25 not found and no bundled installer available
echo ============================================================================
echo.
echo Your system needs JDK 25 to run this JavaFX application.
echo Detected Java: 1.8.0_401 (if any)
echo Required Java: 25.0.3 or later
echo.
echo SOLUTION OPTIONS:
echo.
echo Option 1: Provide bundled JDK 25 installer
echo   - Place a JDK 25 MSI installer in: %CD%\jdk-local\
echo   - Run this script again
echo.
echo Option 2: Install JDK 25 system-wide and set JAVA_HOME
echo   - Download from: https://adoptium.net/temurin/releases/
echo   - Select: Latest LTS (JDK 25) - Windows x64
echo   - Run the installer
echo   - Set JAVA_HOME environment variable:
echo     * Press Windows + X, select "System"
echo     * Click "Advanced system settings"
echo     * Click "Environment Variables"
echo     * Add new system variable:
echo       Name:  JAVA_HOME
echo       Value: C:\Program Files\Eclipse Adoptium\jdk-25.0.x.x-hotspot
echo     * Click OK three times and restart this script
echo.
echo ============================================================================
echo.
pause
exit /b 1
