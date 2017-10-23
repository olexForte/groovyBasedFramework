@echo off
set appName=com.follett.fsc.Enlight
set eraseDataCommand=shell rm -R sdcard/Enlight*
for /f %%i in ('adb devices ^| findstr /V /C:"List"') do (
	adb -s %%i uninstall %appName%
	adb -s %%i %eraseDataCommand%
)