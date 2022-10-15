package com.fantasy.creation;

import com.fantasy.model.MatchDetails;
import com.fantasy.model.Player;
import com.fantasy.model.PlayerType;
import com.fantasy.model.Team;
import com.google.common.base.CharMatcher;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static Boolean selectMatch(AppiumDriver driver, MatchDetails matchDetails) throws InterruptedException {
//        todo: add scrolling features
//      todo: a lot of todos here
        for(int i=1;i<2;i++){
            WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
            System.out.println("Match_Card_"+i);
            System.out.println(match.isDisplayed());
            List<WebElement> texts = match.findElements(By.className("android.widget.TextView"));
            List<Team> teamsPlaying = new ArrayList<>();
            for(WebElement text:texts){
                System.out.println(text.getText());
                if(EnumUtils.isValidEnumIgnoreCase(Team.class,text.getText().replaceAll("-",""))){
                    teamsPlaying.add(Team.valueOf(text.getText().replaceAll("-","")));
                }
            }
            if(teamsPlaying.contains(matchDetails.getFirst())){
                match.click();
                TimeUnit.SECONDS.sleep(5);
                List<MobileElement> d = driver.findElementsByClassName("android.widget.TextView");
                MobileElement teams = null;
                for(var team:d){
                    if(team.getText().equals("My Teams")){
                        System.out.println(team.getText());
                        teams = team;
                        break;
                    }
                }
                teams.click();
                TimeUnit.SECONDS.sleep(3);
                return true;

            }
        }
        return false;
    }

    public static Player parsePlayer(List<WebElement> playerDetails, PlayerType type){
        String name = "";
//        todo: verify and improve logic
        float sel = 0;
        int points = 0;
        float credit = 0;
        String team = "";
        boolean isPlaying =false;
        for (WebElement p:playerDetails){
            String text = p.getText();
            if(EnumUtils.isValidEnumIgnoreCase(Team.class,text.replaceAll("-",""))){
                team = text.replaceAll("-","");
            }else if(text.startsWith("Sel")){
                // parse selected by
                text = text.replaceAll("[^0-9\\.]", "");
                sel = Float.valueOf(text);
            } else if (NumberUtils.isCreatable(text)) {
                float d =Float.valueOf(text);
                if(d<13 && d>5){
                    // TODO: need a better logic
//                    check for decimal point
                    credit = d;
                }else {
                    points = (int) d;
                }
            } else if ( CharMatcher.ascii().matchesAllOf(text)) {
                // team
                name = text;

            }else {
                // lineup  | announced | played
                text = text.replaceAll("\\P{Print}", "").strip();
                if(text.startsWith("Ann") || text.startsWith("Pla") ){
                    isPlaying = true;
                }
            }
        }

        Player player = new Player(name,credit,type,isPlaying,sel,points,team, -1);
        return player;
    }
}
