package com.fantasy.creation;

import com.fantasy.creation.model.Player;
import com.fantasy.creation.model.PlayerType;
import com.google.common.base.CharMatcher;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;

import javax.management.MBeanAttributeInfo;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class fetchDetails {
    public static void main(String[] args) throws InterruptedException, MalformedURLException {
        AppiumDriver driver = CreateDriverSession.getDriver();
        TimeUnit.SECONDS.sleep(15);
        for(int i=1;i<2;i++){
            try{
                WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
                System.out.println("Match_Card_"+i);
                System.out.println(match.isDisplayed());
                List<WebElement> texts = match.findElements(By.className("android.widget.TextView"));
                for(WebElement text:texts){
                    System.out.println(text.getText());
                }
                match.click();
                TimeUnit.SECONDS.sleep(5);
//                TODO:find contest and create team button
//                fetch contest
//                fetch players
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
                d = driver.findElementsByClassName("android.widget.Button");
                for(var team:d){
                    System.out.println(team.getText());
                    if(team.getText().equals("CREATE A TEAM")){
                        System.out.println(team.getText());
                        team.click();
                        break;
                    }
                }
                TimeUnit.SECONDS.sleep(8);
                System.out.println("Start fetching");
                String time = driver.findElementById("com.app.dream11Pro:id/tvCountDownTimer").getText();
                String toss = "";
                String pitch="";
                String goodFor="";
                String avg="";
                String venue="";
                WebElement fc =driver.findElementByAccessibilityId("fc_Banner");
                WebElement fcs = fc.findElement(By.className("androidx.recyclerview.widget.RecyclerView"));
                List<WebElement> fcsv = fcs.findElements(By.className("android.view.ViewGroup"));
                for(WebElement view:fcsv){
                    switch (view.findElements(By.className("android.widget.TextView")).get(0).getText()){
                        case "Pitch":
                            pitch=view.findElements(By.className("android.widget.TextView")).get(1).getText();
                            break;
                        case "Good for":
                            goodFor = view.findElements(By.className("android.widget.TextView")).get(1).getText();
                            break;
                        case "Avg. Score":
                            avg = view.findElements(By.className("android.widget.TextView")).get(1).getText();
                            break;
                        case "Venue":
                            venue =view.findElements(By.className("android.widget.TextView")).get(1).getText();
                            break;

                    }
                }

                //players fetch
                List<Player> players = new ArrayList<>();
                WebElement typeH = driver.findElementByClassName("android.widget.HorizontalScrollView");
                List<WebElement> types =typeH.findElements(By.className("android.widget.TextView"));
                WebElement wkWeb = null;
                WebElement batWeb = null;
                WebElement arWeb = null;
                WebElement bowlWeb = null;
                for(WebElement ty : types){
                    switch (ty.getText()){
                        case "WK":
                            wkWeb = ty;
                            break;
                        case "BAT":
                            batWeb = ty;
                            break;
                        case "AR":
                            arWeb = ty;
                            break;
                        case "BOWL":
                            bowlWeb = ty;
                            break;
                    }

                }
                long startTime = System.nanoTime();



                batWeb.click();
                TimeUnit.SECONDS.sleep(3);
                players.addAll(fetchPlayer(driver,PlayerType.BAT));

                arWeb.click();
                TimeUnit.SECONDS.sleep(3);
                players.addAll(fetchPlayer(driver,PlayerType.AR));

                bowlWeb.click();
                TimeUnit.SECONDS.sleep(3);
                players.addAll(fetchPlayer(driver,PlayerType.BOWL));

                wkWeb.click();
                TimeUnit.SECONDS.sleep(3);
                players.addAll(fetchPlayer(driver,PlayerType.WK));
                long endTime = System.nanoTime();
                long duration = (endTime - startTime);






//                d = driver.findElementsByClassName("android.widget.Button");
//                for(var team:d){
//                    System.out.println(d.size());
//                    if(team.getText().equals("CREATE A TEAM")){
//                        System.out.println(team.getText());
//                        team.click();
//                        break;
//                    }
//                }





//                contest details
                List<MobileElement> contests = driver.findElementsByXPath("(//android.widget.LinearLayout[@content-desc=\"Contest_Cards\"])");
                System.out.println(contests.size());
                for(MobileElement contest : contests){
                    System.out.println("contest");
                    System.out.println(contest.findElementsByClassName("android.widget.Button").get(0).getText());
                    System.out.println(contest.findElementByClassName("android.view.ViewGroup").findElementByClassName("android.widget.TextView").getText());
                }

            }catch (NoSuchElementException ex){
                ex.getMessage();
            }
        }
    }

    public static List<Player> fetchPlayer(AppiumDriver driver, PlayerType type) throws InterruptedException {
        Set<Player> players = new HashSet<>();
        boolean flag = false;
        PointOption source = null;
        PointOption destination = null;
        do {
        List<WebElement> pls = driver.findElementsByClassName("androidx.recyclerview.widget.RecyclerView");
        flag = !(pls.size()==3);

        for(WebElement p:pls){

                List<WebElement> ps = p.findElements(By.className("android.view.ViewGroup"));
                for (WebElement temp : ps) {

                    if (temp.getAttribute("content-desc") == null) {
                        break;
                    }
                    if (temp.getAttribute("content-desc").equals("create_team_player_row")) {
                        //fetch player details
                        List<WebElement> playerDetails = temp.findElements(By.className("android.widget.TextView"));
                        Player player = parsePlayer(playerDetails, type);
                        if(players.size()==0){
                            source = PointOption.point(temp.getLocation());
                        }else if (players.size()==2){
                            destination = PointOption.point(temp.getLocation());
                        }

                        players.add(player);

                    }

                }

        }
            if(!(source ==null || destination== null)){
                TouchAction actions = new TouchAction(driver);
                actions.longPress(destination).moveTo(source).release().perform();
                TimeUnit.SECONDS.sleep(2);
                actions.tap(destination).perform();
                TimeUnit.SECONDS.sleep(1);
//                actions.longPress(PointOption.point(598,1584)).moveTo(PointOption.point(520,977)).release().perform();
            }
        }while (flag);
        return players.stream().toList();
    }

    public static Player parsePlayer(List<WebElement> playerDetails, PlayerType type){
        String name = "";
        float sel = 0;
        int points = 0;
        float credit = 0;
        String team = "";
        boolean isPlaying =false;
        for (WebElement p:playerDetails){
            String text = p.getText();
            if(text.startsWith("Sel")){
                // parse selected by
                text = text.replaceAll("[^0-9\\.]", "");
                sel = Float.valueOf(text);
            } else if (NumberUtils.isCreatable(text)) {
                float d =Float.valueOf(text);
                if(d<13 && d>5){
                    // TODO: need a better logic
                    credit = d;
                }else {
                    points = (int) d;
                }
            }else if(playerDetails.indexOf(p) == 0){
                name = text;
            } else if ( CharMatcher.ascii().matchesAllOf(text)) {
                // team
                team = text;
            }else {
                // lineup  | announced | played
                text = text.replaceAll("\\P{Print}", "").strip();
                if(text.startsWith("Ann")){
                    isPlaying = true;
                }
            }
        }

        Player player = new Player(name,credit,type,isPlaying,sel,points,team, -1);
        return player;
    }
}
