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
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class fetchDetails {

    private AppiumDriver driver;
    private void scroll(PointOption source, PointOption destination) throws InterruptedException {
        TouchAction actions = new TouchAction(driver);
        actions.longPress(source).moveTo(destination).release().perform();
        TimeUnit.SECONDS.sleep(2);
    }

    public List<MatchDetails> fetch(MatchDetails nextMatch) throws InterruptedException, MalformedURLException {
        driver = CreateDriverSession.getDriver("",0);
        List<MatchDetails> matches = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);

        PointOption destination = PointOption.point(driver.findElementByAccessibilityId("tagCricket").getLocation());
        for(int i = 0; i<6; i++){
//            todo: add scrolling feature
            MatchDetails matchDetails = new MatchDetails();
            try{
//                WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
//                driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])").get(2).findElements(By.className("android.widget.TextView")).get(0).getText();
                List<AndroidElement> matchesc = driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])");
                PointOption source = null;
                for(AndroidElement matchCard: matchesc){
                    List<MobileElement> texts = matchCard.findElementsByClassName("android.widget.TextView");
                    List<Team> teamsPlaying = new ArrayList<>();
                    matchDetails =parseMatchDetails(texts.stream().map(WebElement::getText).collect(Collectors.toList()));
                    if(nextMatch!=null &&nextMatch.equals(matchDetails)){
                        matchCard.click();
                        TimeUnit.SECONDS.sleep(5);
                        MatchDetails details =travelMatch(driver,matchDetails);
                        return Collections.singletonList(details);
                    }else{
                        if(matchDetails !=null)
                            matches.add(matchDetails);
                    }
                    source = PointOption.point(matchCard.getCenter());

                }
                scroll(source,destination);


//                todo improve logic




            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

//
        }
        return matches;
    }

    // convert text to details
    private MatchDetails parseMatchDetails(List<String> texts) {
        MatchDetails matchDetails = new MatchDetails();
        List<Team> teams = new ArrayList<>();
        int crore = 10000000;
        int lakh = 100000;
        for(String t:texts){
            if(EnumUtils.isValidEnumIgnoreCase(Team.class,t.replaceAll("-",""))){
                Team team = Team.valueOf(t.replaceAll("-", ""));
                teams.add(team);
//                ₹25 Crores
            }else if(t.contains("Crore")){
                t =t.replaceAll("[^0-9\\.]", "");
                float n =Float.valueOf(t);
                n*=crore;
                matchDetails.setPrizePool(n);
            }else if(t.contains("Lakh")){
                t =t.replaceAll("[^0-9\\.]", "");
                float n =Float.valueOf(t);
                n*=lakh;
                matchDetails.setPrizePool(n);
            }else if((t.contains("h")||t.contains("m")) && t.length()<8){
//                todo: improve time parse
                String[] time = t.split(" ");
                int num = Integer.parseInt(time[0].replaceAll("[^0-9\\.]", ""));
                int num1 = 0;
                if(time.length ==2)
                    num1 = Integer.parseInt(time[1].replaceAll("[^0-9\\.]", ""));
                int sec = 0;
                if(time[0].endsWith("h")){
                    sec =num*3600+num1*60;
                }else {
                    sec = num*60+num1;
                }
                Date date = Date.from(new Date().toInstant().plusSeconds(sec));
                matchDetails.setTime(date);

            }
        }
        matchDetails.setTeams(teams);
        if(matchDetails.getTeams().size()!=2){
            return null;
        }
        return matchDetails;
    }

    public static MatchDetails travelMatch(AppiumDriver driver,MatchDetails matchDetails) throws InterruptedException {


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
            WebElement fc =driver.findElementByAccessibilityId("fc_Banner");
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
        List<Player> players = new ArrayList<>();
        WebElement typeH = driver.findElementByClassName("android.widget.HorizontalScrollView");
        List<WebElement> types =typeH.findElements(By.className("android.widget.TextView"));
        WebElement wkWeb = null;
        WebElement batWeb = null;
        WebElement arWeb = null;
        WebElement bowlWeb = null;
        for(WebElement ty : types){
            switch (ty.getText()) {
                case "WK" -> wkWeb = ty;
                case "BAT" -> batWeb = ty;
                case "AR" -> arWeb = ty;
                case "BOWL" -> bowlWeb = ty;
            }

        }


        assert batWeb != null;
        batWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(fetchPlayer(driver, PlayerType.BAT));

        assert arWeb != null;
        arWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(fetchPlayer(driver,PlayerType.AR));

        assert bowlWeb != null;
        bowlWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(fetchPlayer(driver,PlayerType.BOWL));

        assert wkWeb != null;
        wkWeb.click();
        TimeUnit.SECONDS.sleep(1);
        players.addAll(fetchPlayer(driver,PlayerType.WK));

        List<WebElement> buttons =driver.findElementsByClassName("android.widget.Button");
        for(WebElement button:buttons){
            if(button.getText().equals("LINEUP")){
                if(false){
                    button.click();
                    TimeUnit.SECONDS.sleep(3);

                    List<WebElement> pls = driver.findElementsByClassName("android.widget.TextView");

//
                    List<String> playersName = players.stream().map(player -> player.getName()).collect(Collectors.toList());
                    List<WebElement> plsF = pls.stream().filter(s1 -> playersName.contains(s1.getText())).collect(Collectors.toList());
                    int count =1;
                    for(WebElement p:plsF){
//                        TODO: better logic
                        if(players.stream().map(Player::getName).collect(Collectors.toList()).contains(p.getText())){
                            Optional<Player> pl =players.stream().filter(player -> player.getName().equals(p.getText())).findFirst();
//                            pl.ifPresent(player -> player.setBattingOrder(count/2));
                            count++;
                        }
                    }

                }
            }
        }

        matchDetails.setPlayers(players);
        return matchDetails;
    }

    public static List<Player> fetchPlayer(AppiumDriver driver, PlayerType type) throws InterruptedException {
        Set<Player> players = new HashSet<>();
        boolean flag;
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
//                        todo: bowler type spin vs pace
                        Player player = Helper.parsePlayer(playerDetails, type);
                        if(players.size()==0){
                            source = PointOption.point(temp.getLocation());
                        }else if (players.size()==2){
                            destination = PointOption.point(temp.getLocation());
                        }
                        if(player != null){
                            players.add(player);
                        }

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

    public MatchDetails selectProMatch(MatchDetails matchDetails) throws MalformedURLException, InterruptedException {
        AppiumDriver driver = CreateDriverSession.getDriver("",0);
        TimeUnit.SECONDS.sleep(5);
        WebElement h = null;
        List<WebElement> hviews =driver.findElements(By.className("android.widget.HorizontalScrollView"));
        for(WebElement m : hviews){
            List<WebElement> txts =m.findElements(By.className("android.widget.TextView"));
            for(WebElement t:txts){
                String s= t.getAttribute("content-desc");
                if(s!= null &&t.getAttribute("content-desc").equals("promotional-card-match-start-time")){
                    h=m;
                }
            }
        }
        List<WebElement> texts = h.findElements(By.className("android.widget.TextView"));

        WebElement time = null;
        for(WebElement t:texts){
            System.out.println(t.getText());
            if(t.getText().contains("pm") || t.getText().contains("am")){
                time = t;
            }
            if(t.getText().equals(matchDetails.getFirst().toString())){
                time.click();
                TimeUnit.SECONDS.sleep(2);
                matchDetails =travelMatch(driver,matchDetails);
                driver.quit();
                return matchDetails;
            }

        }
        driver.quit();
        return matchDetails;
    }
}
