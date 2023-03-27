package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class fetchDetails {

    private AppiumDriver driver;

    public List<MatchDetails> fetch(MatchDetails nextMatch) throws InterruptedException, MalformedURLException {
        driver = CreateDriverSession.getDriver("",0);
        List<MatchDetails> matches = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);

        PointOption destination = PointOption.point(driver.findElementByAccessibilityId("tagCricket").getLocation().moveBy(0,500));
        for(int i = 0; i<6; i++){
            MatchDetails matchDetails = new MatchDetails();
            try{
//                WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
//                driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])").get(2).findElements(By.className("android.widget.TextView")).get(0).getText();
                List<AndroidElement> matchesc = driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])");
                PointOption source = null;
                for(AndroidElement matchCard: matchesc){
                    List<MobileElement> texts = matchCard.findElementsByClassName("android.widget.TextView");
                    List<Team> teamsPlaying = new ArrayList<>();
                    matchDetails =Helper.parseMatchDetails(texts.stream().map(WebElement::getText).collect(Collectors.toList()));
                    if(nextMatch!=null &&nextMatch.equals(matchDetails)){
                        matchCard.click();
                        TimeUnit.SECONDS.sleep(5);
                        MatchDetails details =getPlayersFromMatch(matchDetails);
                        return Collections.singletonList(details);
                    }else{
                        if(matchDetails !=null)
                            matches.add(matchDetails);
                    }
                    source = PointOption.point(matchCard.getCenter());

                }
                Helper.scroll(source,destination,driver);


//                todo improve logic




            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

//
        }
        return matches;
    }

    // convert text to details
    private MatchDetails getPlayersFromMatch(MatchDetails matchDetails) throws InterruptedException {
        List<Player> players = new ArrayList<>();
        List<MobileElement> d = driver.findElementsByClassName("android.widget.TextView");
        MobileElement teams = null;
        for(var team:d){
            if(team.getText().contains("My Teams")){
                System.out.println(team.getText());
                teams = team;
                break;
            }
        }
        assert teams != null;
        teams.click();
        TimeUnit.SECONDS.sleep(3);
        d = driver.findElementsByClassName("android.widget.TextView");
        for(var team:d){
            System.out.println(team.getText());
            if(team.getText().contains("CREATE A TEAM") || team.getText().equals("CREATE TEAM")){
                System.out.println(team.getText());
                team.click();
                break;
            }
        }

        TimeUnit.SECONDS.sleep(3);
        System.out.println("Start fetching");
//        todo improve time
        String toss = "";
//                TODO: batting first and second who won the toss
//        team won the toss and elected to bat/ bowl first parse

        try{
            WebElement fc =driver.findElementByAccessibilityId("FANCODE_STRIP_VIEW");
//            todo: solve this
            WebElement fcs = fc.findElement(By.className("androidx.recyclerview.widget.RecyclerView"));
            List<WebElement> fcsv = fcs.findElements(By.className("android.view.ViewGroup"));
            for(WebElement view:fcsv){
                switch (view.findElements(By.className("android.widget.TextView")).get(0).getText()) {
                    case "Pitch" ->
                            matchDetails.setPitch(PitchType.valueOf(view.findElements(By.className("android.widget.TextView")).get(1).getText().toUpperCase()));
                    case "Good for" ->
                            matchDetails.setBowlingType(BowlingType.valueOf(view.findElements(By.className("android.widget.TextView")).get(1).getText().toUpperCase()));
                    case "Avg. Score" ->
                            matchDetails.setAvgScore(Integer.parseInt(view.findElements(By.className("android.widget.TextView")).get(1).getText()));
//                case "Venue" ->
//                        matchDetails.setVenue(view.findElements(By.className("android.widget.TextView")).get(1).getText());
                }
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }


        //players fetch

        WebElement wkWeb = driver.findElementByAccessibilityId("btnWK");
        WebElement batWeb = driver.findElementByAccessibilityId("btnBAT");
        WebElement arWeb = driver.findElementByAccessibilityId("btnAR");
        WebElement bowlWeb = driver.findElementByAccessibilityId("btnBOWL");
        assert batWeb != null;
        batWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(Helper.fetchPlayer(driver, PlayerType.BAT));

        assert arWeb != null;
        arWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(Helper.fetchPlayer(driver,PlayerType.AR));

        assert bowlWeb != null;
        bowlWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(Helper.fetchPlayer(driver,PlayerType.BOWL));

        assert wkWeb != null;
        wkWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(Helper.fetchPlayer(driver,PlayerType.WK));


        matchDetails.setPlayers(players);
        return matchDetails;
    }
}
