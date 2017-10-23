@echo off
for /f %%i in ('adb devices ^| findstr /V /C:"List"') do (adb -s %%i reboot)