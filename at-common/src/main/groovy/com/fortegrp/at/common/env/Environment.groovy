package com.fortegrp.at.common.env

class Environment {

    static String baseUrl
    static conf
    private static int retryTimes = 0
    static String browserType = System.getProperty("browser")
    static String testCategory = System.getProperty("testCategory") ?: "all"
    static remoteUrl = System.properties['remoteUrl'] ?: "http://xxx.xx.xx.xxx:xxxx/wd/hub"

    static getForkNumber() {
        if (System.properties['fork.number'] != null) {
            System.properties['fork.number'].toInteger()
        } else {
            System.properties['org.gradle.test.worker'] ? System.properties['org.gradle.test.worker'].toInteger() - 1 : 1
        }
    }

    static getAppUrl() {
        getTestEnv().baseUrl
    }

    static getTestEnv() {
        getConfig().environments."${System.properties['geb.env']}"
    }

    //force config loading for the case when browser doesn't exit yet (needed to get baseURL for DB update)
    static getConfig() {
        if (conf == null) {
            conf = new ConfigSlurper().parse(new File('src/main/resources/GebConfig.groovy').toURL())
        }
        conf
    }

    static retries() {
        def val = System.properties['retry.times']
        if ((val != null) && (val != "")) {
            retryTimes = val.toInteger()
        }
        retryTimes
    }

    static reportGlobalSuffix() {
        "_${browserType}"
    }

    static getCWD(){
        System.getProperty("user.dir")
    }
}
