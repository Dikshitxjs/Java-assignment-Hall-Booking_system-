@echo off
REM Compile Java sources into a temporary folder so .class files are not left in the repository.
set "TMPDIR=%TEMP%\hallbooking_build"
if exist "%TMPDIR%" rd /s /q "%TMPDIR%"
mkdir "%TMPDIR%"

REM Compile the project
javac -d "%TMPDIR%" src\main\java\com\hallsymphony\*.java src\main\java\com\hallsymphony\*\*.java src\main\java\com\hallsymphony\*\*\*.java
if errorlevel 1 (
  echo Compilation failed.
  pause
  exit /b 1
)

REM Run the application
java -cp "%TMPDIR%" com.hallsymphony.Main

REM Clean up compiled files
rd /s /q "%TMPDIR%"
