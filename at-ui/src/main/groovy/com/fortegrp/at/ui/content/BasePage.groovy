package com.fortegrp.at.ui.content

import com.fortegrp.at.common.utils.LogHelper
import geb.Page
import org.openqa.selenium.WebElement

import static com.fortegrp.at.common.utils.LogHelper.logInfo

/**
 * Created by bray on 2/10/14.
 */

class BasePage extends Page {

    @Override
    void to(Map params, Object... args) {
        logInfo("Navigate to page " + getClass().simpleName)
        super.to(params, args)
    }

    @Override
    void onLoad(Page previousPage) {
        logInfo("Loading page " + browser.getCurrentUrl())
        super.onLoad(previousPage)
    }

    @Override
    boolean verifyAt() {
        logInfo("Verify at page " + getClass().simpleName)
        return super.verifyAt()
    }

    def focusOnElement(WebElement element){
        //sleep(1000)
        driver.executeScript("arguments[0].focus()", element)
        //sleep(1000)
    }

    def clickOnElementUsingJS(WebElement element){
        driver.executeScript("arguments[0].click()", element)
    }

    /**
     * Scrolls to the web element
     * Web element is placed in the middle of the screen after function is applied
     * @param element
     * @return
     */
    def scrollToElementUsingJS(WebElement element){
        try {
            int position = element.location.y
            int screenHeight = driver.manage().window().size.height
            js.exec('window.scrollTo(0,' + (position - screenHeight / 2).toString() + ')')
            return true
        } catch (Exception e){
            LogHelper.logInfo("WARNING - Scroll failed" )
            return false
        }
    }
}
