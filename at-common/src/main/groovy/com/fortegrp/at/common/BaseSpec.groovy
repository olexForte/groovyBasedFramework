package com.fortegrp.at.common

import com.fortegrp.at.common.annotation.SkipSetup
import com.fortegrp.at.common.env.Environment
import groovy.io.FileType
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import jxl.WorkbookSettings
import org.apache.commons.lang3.StringUtils

import java.awt.*
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

import static com.fortegrp.at.common.utils.LogHelper.logInfo

/**
 * Created by keverett on 1/7/14.
 */

trait BaseSpec {
    static int testCounter = 1

    def isFirstRun() {
        (Environment.forkNumber == 1) && (testCounter == 1)
    }

    /**
     * Method for kicking off browser or mobile application
     */
    def startApplication() {

    }

    def setup() {
        logInfo("Iteration #$testCounter")
        startApplication()
        setupProject()

        //skipping per-method setup method if @SkipSetup annotation present
        if (!isMethodMarkedWithAnnotation(SkipSetup.canonicalName)) {
            setupTest()
        }
    }

    def setupProject() {
    }

    def setupTest() {
    }

    def cleanup() {
        testCounter += 1
        cleanupTest()
        cleanupProject()
    }

    def cleanupProject() {
    }

    def cleanupTest() {
    }

    def getCurrentMethodAnnotations() {
        specificationContext.currentIteration.getDescription().getAnnotations()
    }

    def isMethodMarkedWithAnnotation(String annotation) {
        def result = false
        getCurrentMethodAnnotations().each {
            if (it.toString().contains(annotation)) {
                result = true
            }
        }
        result
    }
}
