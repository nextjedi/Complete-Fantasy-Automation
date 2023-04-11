package com.fantasy.creation;

import com.fantasy.model.Udid;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class CreateDriverSession {
    public static AndroidDriver<AndroidElement> getDriver(String udid, int port) throws MalformedURLException {
//        todo: standard set of desired capabilities
//        todo: multiple emulator self launch
//        manage open emulators
//        todo: etc
        if(udid.isBlank()){
//            default phone
            udid = Udid.R52R40L8X6Z.name();
        }
        if(port==0){
            port = 4723;
        }


        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(CapabilityType.PLATFORM_NAME,"Android");
        caps.setCapability(MobileCapabilityType.DEVICE_NAME,"any"+udid);
        caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
        caps.setCapability(MobileCapabilityType.UDID,udid);
        caps.setCapability(MobileCapabilityType.FULL_RESET, false);
        caps.setCapability(MobileCapabilityType.NO_RESET, true);
        caps.setCapability("newCommandTimeout", 10000);

        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE,"com.app.dream11Pro");
        caps.setCapability("appActivity", "com.app.dream11.dream11.SplashActivity");
        URL url = new URL("http://0.0.0.0:"+port+"/wd/hub");
        return new AndroidDriver<>(url,caps);





    }
}
