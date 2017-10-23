@echo off
call svn up
call stop_selendroid.bat
call adb start-server
call uninstall_app.bat
call download_app.bat
rem call upload_book_data.bat
call start_selendroid.bat