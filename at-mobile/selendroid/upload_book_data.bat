@echo off
set BOOK_DATA_LOCATION=offline_book_data/.
set DEVICE_LOCATION=/sdcard/offline_book_data
set mkDirCommand=shell mkdir %DEVICE_LOCATION%
set copyDataCommand=push -p %BOOK_DATA_LOCATION% %DEVICE_LOCATION%
for /f %%i in ('adb devices ^| findstr /V /C:"List"') do (
	adb -s %%i %mkDirCommand%
	adb -s %%i %copyDataCommand%
)