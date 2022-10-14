package com.fantasy.creation;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class CreateDriverSession {
    public static AppiumDriver getDriver() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
        caps.setCapability(MobileCapabilityType.DEVICE_NAME,"any");
        caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
        caps.setCapability(MobileCapabilityType.UDID,"emulator-5554");
//        caps.setCapability(MobileCapabilityType.APP, "D:\\dream11.apk");
        caps.setCapability(MobileCapabilityType.FULL_RESET, false);
        caps.setCapability(MobileCapabilityType.NO_RESET, true);
        caps.setCapability("newCommandTimeout", 10000);

        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE,"com.app.dream11Pro");
        caps.setCapability("appActivity", "com.app.dream11.dream11.SplashActivity");
//        caps.setCapability("appWaitForLaunch","false");
        caps.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY,"com.app.dream11.dream11.SplashActivity");
        URL url = new URL("http://0.0.0.0:4723/wd/hub");
        AppiumDriver driver;
        return driver = new AndroidDriver(url,caps);





    }
}
