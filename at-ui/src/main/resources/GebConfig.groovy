import com.fortegrp.at.ui.env.Driver
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
//unexpectedPages = [ServerUnavailablePage]

// get/set baseUrl from environments closure. Here testEnv is externally-passed parameter (for environment type)
baseUrl = System.properties['baseURL']

driver = { Driver.getInstance() } // get driver for current run

if (!System.properties['geb.build.reportsDir'])  // reports dir location
    reportsDir = "reports"
