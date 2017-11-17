package com.fortegrp.at.ui.extension

import geb.Browser
import geb.navigator.Navigator
import org.openqa.selenium.WebElement

import static com.fortegrp.at.common.utils.LogHelper.logInfo

class NonEmptyNavigator extends geb.navigator.NonEmptyNavigator {
    NonEmptyNavigator(Browser browser, Collection<? extends WebElement> contextElements) {
        super(browser, contextElements)
    }

    @Override
    Navigator click() {
        logInfo("Click on element [" + getElementLocator() + "]")
        super.click()
    }

    @Override
    def value() {
        def result = super.value()
        logInfo("Get value from element [" + getElementLocator() + "] = '"+ result +"'")
        result
    }

    @Override
    Navigator value(value) {
        logInfo("Set value '" + value + "' to element [" + getElementLocator() + "]")
        super.value(value)
    }

    @Override
    Navigator leftShift(value) {
        logInfo("Set value '" + value + "' to element [" + getElementLocator() + "]")
        super.leftShift(value)
    }

    @Override
    String getAttribute(String name) {
        String result = super.getAttribute(name)
        logInfo("Get attribute: '" + name + "' from element [" + getElementLocator() + "] = '" + result +"'")
        result
    }

    @Override
    boolean isDisplayed(){
        logInfo("Check if element is displayed [" + getElementLocator() + "]")
        super.isDisplayed()
    }

    @Override
    String text() {
        String result = super.text()
        logInfo("Get text from element [" + getElementLocator() + "] = '" + result +"'")
        result
    }


    def getElementLocator() {
        def locator = this.contextElements[0].foundBy.toString()
        locator.substring(locator.indexOf('->')+3)
    }
}