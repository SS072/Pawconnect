@echo off
setlocal enabledelayedexpansion

echo ====================================================
echo Compiling PawConnect Pet Adoption Management System
echo (Pure Standalone Java Swing GUI Mode)
echo ====================================================

set BASE_DIR=%~dp0..
set SRC_DIR=%BASE_DIR%\src
set BIN_DIR=%BASE_DIR%\bin

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

dir /s /b "%SRC_DIR%\*.java" > "%BASE_DIR%\sources.txt"

javac -d "%BIN_DIR%" @"%BASE_DIR%\sources.txt"

if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Compilation completed without errors.
    del "%BASE_DIR%\sources.txt"
) else (
    echo [ERROR] Compilation failed with error level %ERRORLEVEL%.
)

endlocal
