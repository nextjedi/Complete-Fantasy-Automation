package com.fantasy.creation;

import com.fantasy.model.*;
import com.google.common.base.CharMatcher;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class fetchDetails {
    public MatchDetails fetch() throws InterruptedException, MalformedURLException {
        AppiumDriver driver = CreateDriverSession.getDriver();
        MatchDetails matchDetails = new MatchDetails();
        TimeUnit.SECONDS.sleep(15);
        for(int i=1;i<2;i++){
//            TODO: make it compatible for recording multiple matches
//            todo: add sctrolling feature
            matchDetails = new MatchDetails();
            WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
            System.out.println("Match_Card_"+i);
            System.out.println(match.isDisplayed());
            List<WebElement> texts = match.findElements(By.className("android.widget.TextView"));

            List<Team> teamsPlaying = new ArrayList<>();
//            todo: teams enum vs db
            for(WebElement text:texts){
                System.out.println(text.getText());
                if(EnumUtils.isValidEnumIgnoreCase(Team.class,text.getText().replaceAll("-",""))){
                    teamsPlaying.add(Team.valueOf(text.getText().replaceAll("-","")));
                }
            }
            match.click();
//            todo: implicit vs explicit wait
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
            d = driver.findElementsByClassName("android.widget.Button");
            for(var team:d){
                System.out.println(team.getText());
                if(team.getText().equals("CREATE A TEAM")){
                    System.out.println(team.getText());
                    team.click();
                    break;
                }
            }

            TimeUnit.SECONDS.sleep(3);
            System.out.println("Start fetching");
            String time = driver.findElementById("com.app.dream11Pro:id/tvCountDownTimer").getText();
//                TODO: parse time
            String toss = "";
//                TODO: batting first and second who won the toss
            matchDetails.setFirst(teamsPlaying.get(0));
            matchDetails.setSecond(teamsPlaying.get(1));

            WebElement fc =driver.findElementByAccessibilityId("fc_Banner");
            WebElement fcs = fc.findElement(By.className("androidx.recyclerview.widget.RecyclerView"));
            List<WebElement> fcsv = fcs.findElements(By.className("android.view.ViewGroup"));
            for(WebElement view:fcsv){
                switch (view.findElements(By.className("android.widget.TextView")).get(0).getText()){
                    case "Pitch":
                        matchDetails.setPitch(PitchType.valueOf(view.findElements(By.className("android.widget.TextView")).get(1).getText().toUpperCase()));
                        break;
                    case "Good for":
                        matchDetails.setBowlingType(BowlingType.valueOf(view.findElements(By.className("android.widget.TextView")).get(1).getText().toUpperCase()));
                        break;
                    case "Avg. Score":
                        matchDetails.setAvgScore(Integer.valueOf(view.findElements(By.className("android.widget.TextView")).get(1).getText()));
                        break;
                    case "Venue":
                        matchDetails.setVenue(view.findElements(By.className("android.widget.TextView")).get(1).getText());
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
            players.addAll(fetchPlayer(driver, PlayerType.BAT));

            arWeb.click();
            TimeUnit.SECONDS.sleep(3);
            players.addAll(fetchPlayer(driver,PlayerType.AR));

            bowlWeb.click();
            TimeUnit.SECONDS.sleep(3);
            players.addAll(fetchPlayer(driver,PlayerType.BOWL));

            wkWeb.click();
            TimeUnit.SECONDS.sleep(3);
            players.addAll(fetchPlayer(driver,PlayerType.WK));

//                TODO: lineup batting order
            List<WebElement> buttons =driver.findElementsByClassName("android.widget.Button");
            for(WebElement button:buttons){
                if(button.getText().equals("LINEUP")){
                    if(button.isEnabled()){
//                        TODO: fetch lineup // batting order
                        List<WebElement> pls = driver.findElementsByClassName("android.widget.TextView");
                        for(WebElement p:pls){
//                            if(p.getText().equals())
                        }

                    }
                }
            }

//            driver.quit();
            matchDetails.setPlayers(players);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            duration = TimeUnit.SECONDS.convert(duration,TimeUnit.NANOSECONDS);
        }
        return matchDetails;
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
                        Player player = Helper.parsePlayer(playerDetails, type);
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
            }
        }while (flag);
        return players.stream().toList();
    }


}
