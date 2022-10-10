import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class CreateDriverSession {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
        caps.setCapability(MobileCapabilityType.DEVICE_NAME,"any");
        caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
        caps.setCapability(MobileCapabilityType.UDID,"emulator-5554");
//        caps.setCapability(MobileCapabilityType.APP, "D:\\dream11.apk");
        caps.setCapability(MobileCapabilityType.FULL_RESET, false);
        caps.setCapability(MobileCapabilityType.NO_RESET, true);

        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE,"com.app.dream11Pro");
        caps.setCapability("appActivity", "com.app.dream11.dream11.SplashActivity");
//        caps.setCapability("appWaitForLaunch","false");
        caps.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY,"com.app.dream11.dream11.SplashActivity");
        URL url = new URL("http://0.0.0.0:4723/wd/hub");
        AppiumDriver driver = new AndroidDriver(url,caps);
        TimeUnit.SECONDS.sleep(15);
        for(int i=1;i<4;i++){
            try{
                WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
                System.out.println("Match_Card_"+i);
                System.out.println(match.isDisplayed());
                List<WebElement> texts = match.findElements(By.className("android.widget.TextView"));
                for(WebElement text:texts){
                    System.out.println(text.getText());
                }
            }catch (NoSuchElementException ex){
                continue;
            }
        }




    }
}
