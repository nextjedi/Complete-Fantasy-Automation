package com.ap.fantasy.creation.dream11;

import com.ap.fantasy.creation.Helper;
import com.ap.fantasy.model.Constant;
import com.ap.fantasy.model.MatchDetails;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.logging.Logger;

import static com.ap.fantasy.model.Constant.*;
import static com.ap.fantasy.model.Constant.DIGIT_ONLY_REGEX;

public class MyTeamsPage {
    private static final Logger log = Logger.getLogger(MyTeamsPage.class.getName());
    public static void clickOnCreateTeam(AppiumDriver<?> driver,int teamCount) throws InterruptedException {
        log.info("Clicking on create team");
        WebElement createFirstTeam;
        try {
            if(teamCount == 0){
                createFirstTeam = driver.findElementByAccessibilityId(Constant.CREATE_TEAM_BUTTON);
            }else {
                createFirstTeam = driver.findElementByAccessibilityId(Constant.CREATE_TEAM_AFTER_FIRST_BUTTON);
            }
            createFirstTeam.click();
            Helper.wait(3);
            log.info("Clicked on create team");
        }catch (NoSuchElementException e){
            log.warning("Create team button not found");
            Helper.notFound(Constant.CREATE_TEAM_BUTTON);
        }
    }

    public static boolean selectTeamToEdit(int teamNo, AppiumDriver<AndroidElement> driver){
        int currentTeam = 0;
        do {

            List<AndroidElement> teamCards = driver.findElementsByAccessibilityId(TEAM_CARD);
            for (AndroidElement teamCard : teamCards) {
                try {
                    List<MobileElement> teamNames = teamCard.findElementsByClassName(CLASS_TEXT_VIEW);
                    boolean flag = false;
                    for (MobileElement teamCandidate:teamNames){
                        String text = teamCandidate.getText();
                        if(text.startsWith("(T")){
                            currentTeam = Integer.parseInt(text.replaceAll(DIGIT_ONLY_REGEX, ""));
                            flag = true;
                            break;
                        }
                    }
                    log.info(currentTeam +" - "+teamNo);
                    if(!flag){
                        continue;
                    }
                    if (currentTeam == teamNo) {
                        AndroidElement editButton = (AndroidElement) teamCard.findElementByAccessibilityId("my-teams-edit-team");
                        editButton.click();
                        return true;
                    } else if (teamNo - currentTeam >= 3) {
                        try {
                            Helper.scroll(PointOption.point(teamCard.getLocation()), PointOption.point(teamCard.getLocation().moveBy(0, (int) (teamCard.getRect().height*-2.5))), driver,false);
                            break;
                        } catch (InterruptedException ignored) {

                        }
                    }else if (currentTeam >teamNo ){
                        try {
                            Helper.scroll(PointOption.point(teamCard.getLocation()), PointOption.point(teamCard.getLocation().moveBy(0, (int) (teamCard.getRect().height*2.5))), driver,false);
                            break;
                        } catch (InterruptedException ignored) {

                        }

                    }
                }catch (NoSuchElementException ex){
                    System.out.println("element not found");
                }


            }
        }while (currentTeam <=20);
        return false;
    }
}
