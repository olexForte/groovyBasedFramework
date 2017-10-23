@echo off
set appName=app-full-debug.apk
set jarName=selendroid-standalone-0.15.0-with-dependencies.jar
if "%1" == "" (
	set port=1111
) else (
	set port=%1
)
set command=java -jar %jarName% -selendroidServerPort %port% -deviceScreenshot -app %appName% 2>&1
start "Selendroid Server" %command%
call grid\register_node.bat