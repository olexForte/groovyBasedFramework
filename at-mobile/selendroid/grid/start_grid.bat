@echo off
set PORT=8888
set SELENDROID_VERSION=0.15.0
set SELENIUM_VERSION=2.48.2
set CLASSPATH="selendroid-grid-plugin-%SELENDROID_VERSION%.jar;selenium-server-standalone-%SELENIUM_VERSION%.jar"
set COMMAND=java -Dfile.encoding=UTF-8 -cp %CLASSPATH% org.openqa.grid.selenium.GridLauncher -capabilityMatcher io.selendroid.grid.SelendroidCapabilityMatcher -role hub -port %PORT% 2>&1
start "Selenium Grid" %COMMAND%