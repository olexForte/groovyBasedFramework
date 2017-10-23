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
    def random = new Random()
    def user
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

    def getRandomNum(min, max) {
        random.nextInt((max - min) + 1) + min
    }

    def getRandomItemFromArray(arrayOfItems) {
        def randomGenerator = new Random()
        def getRandomNumber = randomGenerator.nextInt(arrayOfItems.size())
        return arrayOfItems[getRandomNumber]
    }

/**
 * Get level of similarity of 2 strings (in per cents)
 *
 * @param ethalonStr
 * @param actualStr
 *
 * @return similarity level
 */
    def getStringSimilarity(ethalonStr, actualStr) {
        int similarityLevel = getStringSimilarityFromDistance(ethalonStr, actualStr)
        logInfo("String similarity level is " + similarityLevel)
        similarityLevel
    }

/**
 * Get level of similarity of 2 lists
 *
 * @param ethalonList
 * @param actualList
 *
 * @return similarity level
 */
    def getListSimilarity(ethalonList, actualList) {
        def intersectList = ethalonList.intersect(actualList.unique()).size() * 100 / ethalonList.size()
        logInfo("Highlight list similarity level is " + intersectList)
        intersectList.intValue()
    }

/**
 * Transform string distance to the similarity
 *
 * @param ethalonStr
 * @param actualStr
 * @return similarity level
 */
    int getStringSimilarityFromDistance(ethalonStr, actualStr) {
        if (ethalonStr.length() < actualStr.length()) {
            def swap = ethalonStr; ethalonStr = actualStr; actualStr = swap
        }
        int bigLen = ethalonStr.length()
        if (bigLen == 0) {
            return 100.0
        }
        ((bigLen - StringUtils.getLevenshteinDistance(ethalonStr, actualStr)) * 100 / bigLen).intValue()
    }

    def writeValueToClipboard(String value) {
        Toolkit toolkit = Toolkit.getDefaultToolkit()
        Clipboard clipboard = toolkit.getSystemClipboard()
        StringSelection strSel = new StringSelection(value)
        clipboard.setContents(strSel, null)
    }

    def getClipboardContents() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor)
    }

    def getFilesInDownloadDirectory() {
        def list = []
        Environment.getDownloadDir().eachFileRecurse(FileType.FILES) { file ->
            list << file
        }
        list
    }

    def readXLS(File inputFile) {
        def result = []
        WorkbookSettings ws = new WorkbookSettings()
        try {
            ws.setLocale(new Locale("en", "EN"))
            Workbook w = Workbook.getWorkbook(inputFile, ws)

            for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
                Sheet s = w.getSheet(sheet)
                Cell[] row = null
                for (int i = 0; i < s.getRows(); i++) {
                    StringBuilder fileContent = new StringBuilder()
                    row = s.getRow(i)
                    if (row.length > 0) {
                        fileContent.append(row[0].getContents())
                        for (int j = 1; j < row.length; j++) {
                            fileContent.append(',')
                            fileContent.append(row[j].getContents())
                        }
                    }
                    result.add(fileContent.toString())
                }
            }
        }
        catch (Exception e) {
            logInfo('Error occured during thr xls file reading' + e.getMessage())
        }
        return result
    }
}
