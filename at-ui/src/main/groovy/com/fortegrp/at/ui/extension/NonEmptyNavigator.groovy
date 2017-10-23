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
    Navigator value(value) {
        logInfo("Set value '" + value + "' to element [" + getElementLocator() + "]")
        super.value(value)
    }

    @Override
    Navigator leftShift(value) {
        logInfo("Set value '" + value + "' to element [" + getElementLocator() + "]")
        super.leftShift(value)
    }

    def getElementLocator() {
        def locator = this.contextElements[0].foundBy.toString()
        locator.substring(locator.indexOf('css selector: ') + 14)
    }

    boolean isSelected() {
        firstElement().isSelected()
    }

    boolean isDisabled() {
        def value = getAttribute("disabled")
        // Different drivers return different values here
        (value == "disabled" || value == "true" || hasClass('disabled'))
    }

    boolean isMaximized() {
        getWidth() / browser.driver.manage().window().getSize().getWidth() > 0.95
    }

    boolean isRequired() {
        getAttribute("required").equals("true")
    }
}