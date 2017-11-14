import com.fortegrp.at.ui.env.Driver
import com.fortegrp.at.ui.extension.NonEmptyNavigator
import geb.Browser
import geb.navigator.EmptyNavigator
import org.openqa.selenium.WebElement

//   Geb Configuration file

// Geb timeouts
waiting {
    timeout = 30
    retryInterval = 1
    slow { timeout = 50 }
}

// Geb Settings
cacheDriver = true
cacheDriverPerThread = true
quitCachedDriverOnShutdown = true
autoClearCookies = true
atCheckWaiting = true


// get/set baseUrl from environments closure. Here testEnv is externally-passed parameter (for environment type)
baseUrl = System.properties['baseURL']


// get driver for current run
driver = { Driver.getInstance() }

// look into: com.fortegrp.at.ui.extension.NonEmptyNavigator
// custom inner navigation factory for logging the clicks, and other actions with elements
innerNavigatorFactory = { Browser browser, List<WebElement> elements ->
    elements ? new NonEmptyNavigator(browser, elements) : new EmptyNavigator(browser)
}

// reports dir location
if (!System.properties['geb.build.reportsDir'])
    reportsDir = "reports"
