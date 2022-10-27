package com.fantasy.creation;

import com.fantasy.model.Uuid;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class CreateDriverSession {
    public static AppiumDriver getDriver(String udid,int port) throws MalformedURLException {
//        todo: standard set of desired capabilities
//        todo: multiple emulator self launch
//        manage open emulators
//        todo: etc
        if(udid.isBlank()){
            udid = Uuid.MFM7A6LVH6YTMR8D.name();
        }
        if(port==0){
            port = 4724;
        }


        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
        caps.setCapability(MobileCapabilityType.DEVICE_NAME,"any"+udid);
        caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
        caps.setCapability(MobileCapabilityType.UDID,udid);
//        caps.setCapability(MobileCapabilityType.APP, "D:\\dream11.apk");
        caps.setCapability(MobileCapabilityType.FULL_RESET, false);
        caps.setCapability(MobileCapabilityType.NO_RESET, true);
        caps.setCapability("newCommandTimeout", 10000);

        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE,"com.app.dream11Pro");
        caps.setCapability("appActivity", "com.app.dream11.dream11.SplashActivity");
//        caps.setCapability("appWaitForLaunch","false");
//        caps.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY,"com.app.dream11.dream11.SplashActivity");
        URL url = new URL("http://0.0.0.0:"+port+"/wd/hub");
        AppiumDriver driver;
        return driver = new AndroidDriver(url,caps);





    }
}
