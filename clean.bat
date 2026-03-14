@echo off
REM Remove IDE / compiler output directories so .class files are not visible.
if exist out rd /s /q out
if exist bin rd /s /q bin
if exist HallBooking.jar del /f /q HallBooking.jar
if exist manifest.txt del /f /q manifest.txt
echo Clean complete.
