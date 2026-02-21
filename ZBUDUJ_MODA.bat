@echo off
title NPC Click Through - Budowanie moda
color 0A
echo ============================================
echo   NPC Click Through - Automatyczny builder
echo ============================================
echo.

REM Sprawdz czy Java jest zainstalowana
java -version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo [BLAD] Nie znaleziono Javy!
    echo.
    echo Pobierz JDK 21 ze strony:
    echo https://adoptium.net/temurin/releases/?version=21
    echo.
    pause
    exit /b 1
)

echo [OK] Java znaleziona.
echo.

REM Pobierz Gradle Wrapper jesli nie istnieje
if not exist "gradlew.bat" (
    echo [INFO] Pobieranie Gradle Wrapper...
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.8-bin.zip' -OutFile 'gradle.zip'}"
    
    if not exist "gradle.zip" (
        color 0C
        echo [BLAD] Nie udalo sie pobrac Gradle. Sprawdz polaczenie z internetem.
        pause
        exit /b 1
    )
    
    echo [INFO] Rozpakowywanie Gradle...
    powershell -Command "Expand-Archive -Path 'gradle.zip' -DestinationPath 'gradle-dist' -Force"
    del gradle.zip
    
    REM Znajdz rozpakowany folder gradle
    for /d %%i in (gradle-dist\gradle-*) do set GRADLE_DIR=%%i
    set GRADLE_BIN=%CD%\%GRADLE_DIR%\bin\gradle.bat
    
    echo [INFO] Gradle pobrane: %GRADLE_DIR%
) else (
    set GRADLE_BIN=gradlew.bat
)

echo.
echo [INFO] Budowanie moda (pierwsze uruchomienie moze trwac kilka minut)...
echo [INFO] Gradle pobiera zaleznosci Minecraft + Fabric...
echo.

if defined GRADLE_BIN (
    "%GRADLE_BIN%" build --stacktrace
) else (
    gradlew.bat build --stacktrace
)

if errorlevel 1 (
    color 0C
    echo.
    echo [BLAD] Budowanie nie powiodlo sie!
    echo Sprawdz bledy powyzej.
    echo.
    echo Najczestsze przyczyny:
    echo  - Brak internetu
    echo  - Zla wersja Javy (wymagana JDK 21, nie JRE)
    echo  - Niepoprawna wersja mappings dla 1.21.8
    echo.
    echo Sprawdz aktualne wersje na: https://fabricmc.net/develop/
    pause
    exit /b 1
)

echo.
color 0A
echo ============================================
echo   SUKCES! Mod zostal zbudowany!
echo ============================================
echo.
echo Plik .jar znajdziesz w folderze:
echo   build\libs\npcclick-1.0.0.jar
echo.
echo Skopiuj go do: .minecraft\mods\
echo (razem z Fabric API!)
echo.

REM Otwórz folder z gotowym jarem
explorer build\libs

pause
