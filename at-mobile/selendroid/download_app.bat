@echo off
set appLink=http://jenkins.fsc.follett.com/view/Universal-Reader/job/Universal-Reader-CI-Android/lastSuccessfulBuild/artifact/app/build/outputs/apk/app-full-debug.apk
rem set appLink=http://jenkins.fsc.follett.com/view/Universal-Reader/job/Universal-Reader-Release-CI-Android/lastSuccessfulBuild/artifact/app/androidGradle/app/build/outputs/apk/app-full-debug.apk
set appFileName=app-full-debug.apk
set WGET_LOCATION=G:\projects\sophos\bin\wget
%WGET_LOCATION%\wget %appLink% -O %CD%\%appFileName%