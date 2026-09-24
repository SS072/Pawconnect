@echo off
setlocal enabledelayedexpansion

set BASE_DIR=%~dp0..
set BIN_DIR=%BASE_DIR%\bin

echo ====================================================
echo Launching PawConnect Pet Adoption Management System
echo (Pure Standalone Desktop GUI)
echo ====================================================

java -cp "%BIN_DIR%" org.pawconnect.Main %*

endlocal
