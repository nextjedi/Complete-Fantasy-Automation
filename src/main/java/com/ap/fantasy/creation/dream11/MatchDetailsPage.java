package com.ap.fantasy.creation.dream11;

import com.ap.fantasy.creation.FetchDetails;
import com.ap.fantasy.creation.Helper;
import com.ap.fantasy.model.Constant;
import com.ap.fantasy.model.MatchDetails;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.ap.fantasy.model.Constant.*;

public class MatchDetailsPage {
    private static final Logger log = Logger.getLogger(MatchDetails.class.getName());

    public static int clickOnMyTeams(AppiumDriver<?> driver) throws InterruptedException {
        int teamCount = 0;
        try {
            var myTeam = driver.findElementByAccessibilityId(Constant.MY_TEAMS);
            log.info("Clicking on my team");
            myTeam.click();
            TimeUnit.SECONDS.sleep(3);
            teamCount =  Integer.parseInt(myTeam.findElement(By.className(CLASS_TEXT_VIEW)).getText().replaceAll(DIGIT_ONLY_REGEX, ""));
            log.info("Team count: " + teamCount);
        }catch (NoSuchElementException e){
            log.warning("No team found");
            Helper.notFound(Constant.MY_TEAMS);
        }
        return teamCount;
    }
    public static void clickOnContest(AppiumDriver<?> driver){
        driver.findElementByAccessibilityId(CONTEST_BUTTON).click();
    }
}
