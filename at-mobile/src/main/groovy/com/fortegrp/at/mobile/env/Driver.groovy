package com.fortegrp.at.mobile.env

import com.fortegrp.at.common.env.Environment
import io.selendroid.client.SelendroidDriver
import io.selendroid.common.SelendroidCapabilities
import org.uiautomation.ios.IOSCapabilities
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver

/**
 * Created by Leo on 2/24/2017.
 */
class Driver extends com.fortegrp.at.ui.env.Driver {
    static synchronized getInstance() {
        if (driverInstance == null) {
            switch (Environment.browserType) {
                case "remote_ios":
                    def remoteUrl = Environment.remoteUrl.toURL()
                    IOSCapabilities safari = IOSCapabilities.iphone("Safari")
                    safari.setCapability(IOSCapabilities.SIMULATOR, false)
                    safari.setCapability(IOSCapabilities.UUID, "888cd66b8cb92989b8a2176f845a6a2441e3fdd0")
                    safari.setCapability("javascriptEnabled", true)
                    driverInstance = new RemoteIOSDriver(remoteUrl, safari)
//                    driverInstance.manage().timeouts().setScriptTimeout(SCRIPT_TIMEOUT, TimeUnit.SECONDS)
                    driverInstance
                    break
                case "android":
                    def selendroid = Environment.getConfig().selendroid
                    def device = Environment.getConfig().device
                    def remoteUrl = new URL("http", device.host, device.port, "/wd/hub")
                    SelendroidCapabilities capabilities =
                            new SelendroidCapabilities(selendroid.mainActivity)
//                    capabilities.setModel(device.name)
                    driverInstance = new SelendroidDriver(remoteUrl, capabilities)
                    driverInstance
                    break
            }
        } else {
            driverInstance
        }
    }

}
