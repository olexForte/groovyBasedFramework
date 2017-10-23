package specs

import com.fortegrp.at.common.annotation.TestDoc
import com.fortegrp.at.ui.BaseUISpec
import com.fortegrp.at.ui.content.GoogleSearchPage
import spock.lang.Unroll

/**
 * basic test example
 */
class GoogleSearchSpec extends BaseUISpec{

    @Unroll
    @TestDoc('GoogleSearch')
    def "Perform google search '#request'"() {

        when: "User at Google main page"
        at GoogleSearchPage

        and: "Search for '#request'"
        search(request)

        then: "User is redirected to Dashboard Page"
        searchResultIsDisplayed()

        where:
        request |_
        "42"    |_
        "asd"   |_
    }
}