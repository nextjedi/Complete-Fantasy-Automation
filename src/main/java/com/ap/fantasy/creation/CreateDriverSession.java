package com.ap.fantasy.creation;

import com.ap.fantasy.model.Udid;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class CreateDriverSession {
    private CreateDriverSession() {
    }
    public static AndroidDriver<AndroidElement> getDriver(String udid, int port) throws MalformedURLException {
        if(udid.isBlank()){
            udid = Udid.R5CT31D3G4F.name();
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
        caps.setCapability("appium:waitForIdleTimeout",0);
        caps.setCapability("appium:disableWindowAnimation",true);
        caps.setCapability("appium:forceAppLaunch",true);
        caps.setCapability("newCommandTimeout", 10000);
        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE,"com.dream11.fantasy.cricket.football.kabaddi");
        caps.setCapability("appActivity", "com.app.dream11.dream11.ReactHomeActivity");
        URL url = new URL("http://0.0.0.0:"+port+"/");
        return new AndroidDriver<>(url,caps);





    }
}
