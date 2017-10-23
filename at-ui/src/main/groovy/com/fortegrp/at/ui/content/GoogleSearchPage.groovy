package com.fortegrp.at.ui.content

import org.openqa.selenium.Keys

class GoogleSearchPage extends BasePage {

    static at = {
        waitFor {
            searchField.displayed
        }
    }

    static content = {
        searchField(wait: true) { $("input[name='q']") }
        serchResultStatus(wait: true){$('div#resultStats')}
    }

    def search(String request) {
        searchField.value(request + Keys.ENTER)
    }

    def searchResultIsDisplayed(){
        serchResultStatus.displayed
    }
}
