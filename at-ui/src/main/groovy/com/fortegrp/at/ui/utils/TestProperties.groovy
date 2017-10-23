package com.fortegrp.at.ui.utils

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

/**
 * Processing of property files, JSON files
 */
class TestProperties {

    /**
     * Get path to files with data required for test
     * @return
     */
    def static getDataPathForEnv() {
        System.getProperty("user.dir") + File.separator + "src" +
                File.separator + "test" +
                File.separator + "resources" +
                File.separator + "data" +
                File.separator + System.properties [ 'testEnv' ] +
                File.separator
    }

    /**
     * Create JSON file and write to it
     * @param path
     * @param content
     * @return
     */
    def static writeToJSON(String path, Object content)
    {
        File file = new File(path)

        if(file.exists())
        {
            file.delete()
        }

        file.createNewFile()
        file.write(new JsonBuilder(content).toPrettyString())
    }

    /**
     * Read attributes from JSON
     * @param path file loaction
     * @return
     */

    def static Object readFromJSON(String path) {
        def json = null;
        println("Parse file: " + path)
        try {
            json = new JsonSlurper().parseText(new File(path).text)
        }
        catch (Exception e) {
            e.printStackTrace()
        }

        return json;
    }

    /**
     * Read test attributes from JSON
     * @param path file
     * @return
     */

    def static Object readTestDataFromJSON(String path) {
        def json = null;
        path =  getDataPathForEnv() + path
        println("Parse file: " + path)
        try {
            json = new JsonSlurper().parseText(new File(path).text)
        }
        catch (Exception e) {
            e.printStackTrace()
        }

        return json;
    }
}
