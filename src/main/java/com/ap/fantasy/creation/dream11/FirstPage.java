package com.ap.fantasy.creation.dream11;

import com.ap.fantasy.creation.CreateDriverSession;
import com.ap.fantasy.creation.Helper;
import com.ap.fantasy.model.MatchDetails;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.ap.fantasy.model.Constant.*;

public class FirstPage {
    private static final Logger log = Logger.getLogger(FirstPage.class.getName());

    public static List<MatchDetails> travelMatches(AppiumDriver<AndroidElement> driver,MatchDetails nextMatch) throws InterruptedException, MalformedURLException {
        List<MatchDetails> matches = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);
        WebElement cricket = driver.findElementByAccessibilityId(TAG_CRICKET);
        PointOption<?> destination = PointOption.point(cricket.getLocation().moveBy(0, 500));
        for(int i = 0; i<8; i++){
            MatchDetails matchDetails;
            try{
                List<AndroidElement> matchesCard = driver.findElementsByAccessibilityId(MATCH_CARD);
                PointOption<?> source = null;
                for(AndroidElement matchCard: matchesCard){
                    matchDetails =MatchDetails.fromAndroidElement(matchCard);
                    if(nextMatch!=null &&nextMatch.equals(matchDetails)){
                        matchCard.click();
                        Helper.wait(5);
                    }else{
                        if(matchDetails !=null)
                            matches.add(matchDetails);
                    }
                    source = PointOption.point(matchCard.getCenter());

                }
//                Helper.scroll(source,destination,driver,false);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        return matches;
    }

    public static List<MatchDetails> readMatches(AppiumDriver<AndroidElement> driver) throws InterruptedException, MalformedURLException {
        return travelMatches(driver,null);
    }

    public static MatchDetails getIntoMatch(AppiumDriver<AndroidElement> driver, MatchDetails nextMatch) throws InterruptedException, MalformedURLException {
        var matches = travelMatches(driver, nextMatch);
        if(matches.isEmpty()){
            return null;
        }
        return matches.get(0);
    }
    public static void SelectMyMatchesMatch(AppiumDriver<AndroidElement> driver) throws InterruptedException, MalformedURLException {
        var elements =driver.findElementsByClassName(CLASS_TEXT_VIEW);
        for(var androidElement:elements){
            if(androidElement.getText().equalsIgnoreCase("My Matches")){
                androidElement.click();
                break;
            }
        }
    }
}
