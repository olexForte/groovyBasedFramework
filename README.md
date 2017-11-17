# groovyBasedFramework

## Overview

Framework built with following set of technologies:
Groovy - main language for scripts
Gradle - build system
Geb - Selenium-based automation solution ( http://www.gebish.org/manual/current/ )
Spock - testing framework that supports data driven testing and BDD-like syntax in scripts ( http://spockframework.org/spock/docs/1.1/index.html )

Packages:

at-common - common utils and reports generation
at-api  - placeholder for REST API testing based on org.apache.httpcomponents
at-mobile - common mobile testing functionality (based on Selendroid)
at-ui - components for web application testing (page objects, specs, configuration etc.)

at-ui/main/groovy//content - page objects and page modules
at-ui/main/groovy//entities - custom data types
at-ui/main/groovy//env - driver setup (Driver.groovy - for driver initialization)
at-ui/main/groovy//extensins - wrappers for basic functionality
at-ui/main/groovy//utils - helper-classes
at-ui/main/groovy//BaseUISpec.groovy - basic test - all other specs should be inherited form BaseUISpec

at-ui/main/resources/ - Selenium drivers for different browsers and GebConfig file

at-ui/main/resources/GebConfig.groovy (src/main/resources/) contains basic settings for Geb

at-ui/specs - all tests
at-ui/resources/data - set of files required for tests

at-ui/build.gradle - main file of Gradle configuration (it takes care about gathering test names, number of threads and other run time parameters)

### Dependencies
- Java SDK 8
- Firefox 55+
- Chrome 60+

### Installation and execution Instructions

1. Install Java JDK http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html
2. Install Git
3. Clone git Repository (credentials should be taken from authorised user)
4. If you using intellij: import project from external model: Gradle; Use gradle wraper task configuration (!!!Dont use default gradle wraper!!!);
5. Run

Example: ./gradlew clean :at-ui:test -DbaseURL=https://www.google.com -Dbrowsers=chrome

JVM options for JUnit single test run: -ea -DbaseURL=https://www.google.com -Dbrowser=chrome

# Execution parameters

Details:
./gradlew clean :at-ui:test  - runs test
-DtestEnv - name of environment (so far we have only one - qa_env)
-DbaseURL - main URL
-Dbrowsers - browsers (chrome or firefox)

-Dinclude.tests=Example1.class - will run a single test
-Dinclude.tests=Example1.class;Example1.class - will run multiple tests
-Dinclude.tests=*Example*;*AnotherExample* - multiple tests that contain 'Example' or 'AnotherExample' text

-Dthread.count - if you want to save time you may run tests in multiple threads
(-Dthread.count=4 makes tests run in 4 threads - may affect performance)

# Reports analysis

Two different reports will be generated:

- reports/index.html  (DEFAULT report)
- build/reports/<browser_name>_Test/index.html (Report that contains additional info)

Each test looks like short story with parameters

In case if you see errors step with failure will be marked Red

There are two main types of errors: execution error and assertion error (see screenshot: assertion error has no stack trace and has message 'Condition not satisfied')

Assertion error should be self explanatory - if No - see screenshots (ping automation team if you want to change/add messages)
Please notify automation team about execution errors (with stack trace)


