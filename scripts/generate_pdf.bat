@echo off
set "CHROME=C:\Program Files\Google\Chrome\Application\chrome.exe"
set "EDGE=C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
set "HTML=%~dp0..\docs\PawConnect_Project_Report.html"
set "PDF=%~dp0..\docs\PawConnect_Project_Report.pdf"

echo Converting PawConnect Project Report to PDF...
if exist "%CHROME%" (
    echo Using Google Chrome headless engine...
    "%CHROME%" --headless --disable-gpu --no-pdf-header-footer --allow-file-access-from-files --print-to-pdf="%PDF%" "%HTML%"
) else if exist "%EDGE%" (
    echo Using Microsoft Edge headless engine...
    "%EDGE%" --headless --disable-gpu --no-pdf-header-footer --allow-file-access-from-files --print-to-pdf="%PDF%" "%HTML%"
) else (
    echo Error: Neither Chrome nor Edge was found.
    exit /b 1
)

if exist "%PDF%" (
    echo [SUCCESS] PDF successfully created: %PDF%
) else (
    echo [ERROR] PDF creation failed.
    exit /b 1
)
