package com.ap.fantasy.creation.dream11;

import io.appium.java_client.AppiumDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.ap.fantasy.model.Constant.CLASS_TEXT_VIEW;
@Slf4j
public class CompletedMatchesPage {
    public static void readMatches(AppiumDriver<?> driver){
        var cls =driver.findElementsByClassName("androidx.viewpager.widget.ViewPager");
        var scrl = cls.get(0).findElement(By.className("android.widget.ScrollView"));
        var views = scrl.findElements(By.className("android.view.ViewGroup"));
        for( var view :views){
            var texts =view.findElements(By.className(CLASS_TEXT_VIEW));
            for(var t : texts){
                log.info(t.getText());
                log.info(t.getTagName());
//                todo get overview details of each match and scroll and save them in db
//                todo get total details of each match and update db
            }
        }
    }
}
