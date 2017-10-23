package com.fortegrp.at.ui

import com.fortegrp.at.common.BaseSpec
import com.fortegrp.at.common.env.Environment

import geb.spock.GebReportingSpec
import org.openqa.selenium.Dimension
import org.openqa.selenium.Keys

import java.awt.*

import static com.fortegrp.at.common.utils.LogHelper.logInfo

/**
 * Created by Leo on 2/24/2017.
 */
class BaseUISpec extends GebReportingSpec implements BaseSpec {

    /**
     * Opens browser, navigates to the base url(Base url is set in a GebConfig.groovy)
     */
    def startApplication() {
        logInfo("Browser window opening...")
        browser.go(baseUrl)
    }
}
