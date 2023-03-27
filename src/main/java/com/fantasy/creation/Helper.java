package com.fantasy.creation;

import com.fantasy.model.*;
import com.google.common.base.CharMatcher;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        boolean isPlaying =false;

        Map<String, String> pls = playerDetails.stream().collect(Collectors.toMap(WebElement::getTagName, WebElement::getText));
        Player player = new Player(pls,type);
        if(player.getName().isBlank() && player.getTeam() ==null){
            return null;
        }
        return player;
    }

    public static void write(List<MatchDetails> matches){


        try {
            FileOutputStream f = new FileOutputStream(new File(LocalDate.now() +".txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(matches);
            o.close();
            f.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
    public static void write(MatchDetails match){


        try {
            FileOutputStream f = new FileOutputStream(new File(match.getTeams().get(0)+"-"+match.getTeams().get(1)+LocalDate.now()+".txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(match);
            o.close();
            f.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
    public static List<MatchDetails> read(String match) {
        try {
            FileInputStream fi = new FileInputStream(new File(match));
            ObjectInputStream oi = new ObjectInputStream(fi);
            List<MatchDetails> matches = (List<MatchDetails>) oi.readObject();
            return matches;

        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static MatchDetails read(MatchDetails match){
        try {
            FileInputStream fi = new FileInputStream(new File(match.getTeams().get(0)+"-"+match.getTeams().get(1)+LocalDate.now()+".txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            match = (MatchDetails) oi.readObject();
            return match;

        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public static void scroll(PointOption source, PointOption destination, AppiumDriver driver ) throws InterruptedException {
        TouchAction actions = new TouchAction(driver);
        actions.longPress(source).moveTo(destination).release().perform();
        TimeUnit.SECONDS.sleep(2);
    }

    public static MatchDetails parseMatchDetails(List<String> texts) {
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
                if(!StringUtils.isNumeric(time[0])){
                    continue;
                }
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

    public static PlayerTypeButton findPlayerTypeButtons(AppiumDriver driver){
        WebElement wkWeb = driver.findElementByAccessibilityId("btnWK");
        WebElement batWeb = driver.findElementByAccessibilityId("btnBAT");
        WebElement arWeb = driver.findElementByAccessibilityId("btnAR");
        WebElement bowlWeb = driver.findElementByAccessibilityId("btnBOWL");
        return new PlayerTypeButton(wkWeb,batWeb,arWeb,bowlWeb);
    }
    public static List<Player> fetchPlayer(AppiumDriver driver, PlayerType type) throws InterruptedException {
        Set<Player> players = new HashSet<>();
        boolean flag = true;
        PointOption source = null;
        PointOption destination = null;
        boolean behindFlag = false;
        WebElement preview = driver.findElementByAccessibilityId("team_preview_icon");
        int i=0;
        do {
            String playerRow = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+i+"-selected";
            try {
                WebElement pls = driver.findElementByAccessibilityId(playerRow);
                behindFlag =Helper.isPointInRectangle(pls.getRect(),preview.getLocation());
                List<WebElement> playerDetails = pls.findElements(By.className("android.widget.TextView"));
                if(i==0){
                    source = PointOption.point(pls.getLocation());
                }else if (i==3){
                    destination = PointOption.point(pls.getLocation());
                }
                if(playerDetails.size() ==0 ||behindFlag){

                    if(!(source ==null || destination== null)){
                        TouchAction actions = new TouchAction(driver);
                        actions.longPress(destination).moveTo(source).release().perform();
                        TimeUnit.SECONDS.sleep(2);
                    }
                    continue;

                }
                Player player =Helper.parsePlayer(playerDetails,type);
                if(player.isValid()){
                    players.add(player);
                    i++;
                }
            }catch (NoSuchElementException ex){
                flag = false;
            }


        }while (flag);
        return players.stream().toList();
    }

    public static void travelContest(){

    }
    public static void readTeamPlayer(){

    }

    public static boolean SelectTeamToEdit(int teamNo, AppiumDriver driver){
        List<WebElement> teamCards = driver.findElementsByXPath("//android.view.ViewGroup[@content-desc=\"TeamCard\"]");
//        todo: for each team card find team name and edit team button
        for(WebElement teamCard:teamCards){
            WebElement name = teamCard.findElement(By.xpath("//android.widget.TextView[@content-desc=\"tv_teamName\"]"));
            String tNo =teamCard.findElement(By.xpath("//android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[2]/android.widget.TextView[2]")).getText();
            if(Integer.parseInt(tNo.replaceAll("[^0-9\\.]", ""))==teamNo){
                WebElement editButton = teamCard.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"my-teams-edit-team\"]"));
                editButton.click();
                return true;
            }


        }
//        todo scroll
        return false;
    }

    public static void clearPlayers(AppiumDriver driver,PlayerType type) throws IOException, InterruptedException {
        List<Integer> colors = new ArrayList<>();
        Set<Player> players = new HashSet<>();
        boolean flag = true;
        PointOption source = null;
        PointOption destination = null;
        boolean behindFlag = false;
        int i=0;
        WebElement preview = driver.findElementByAccessibilityId("team_preview_icon");
        do {
            String playerRow = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+i+"-unselected";
            try {
                MobileElement pls = (MobileElement) driver.findElementByAccessibilityId(playerRow);
                behindFlag =Helper.isPointInRectangle(pls.getRect(),preview.getLocation());
                List<MobileElement> playerDetails = pls.findElements(By.className("android.widget.TextView"));
                if(i==0){
                    source = PointOption.point(pls.getLocation());
                }else if (i==3){
                    destination = PointOption.point(pls.getLocation());
                }
                if(playerDetails.size() ==0 ||behindFlag){

                    if(!(source ==null || destination== null)){
                        TouchAction actions = new TouchAction(driver);
                        actions.longPress(destination).moveTo(source).release().perform();
                        TimeUnit.SECONDS.sleep(2);
                    }
                    continue;

                }else {
                    i++;
                }
                MobileElement button = pls.findElementByXPath("(//android.view.ViewGroup[@content-desc=\"add-remove-player-button\"])[1]");
                if(isSelected(button,driver)){
                    button.click();
                }
//                bull
            }catch (NoSuchElementException ex){
                flag = false;
            }


        }while (flag);
    }



    public static int findMatch(MatchDetails nextMatch,AppiumDriver driver, Boolean isContest) throws InterruptedException {

        PointOption destination = PointOption.point(driver.findElementByAccessibilityId("tagCricket").getLocation().moveBy(0,500));
        for(int i = 0; i<6; i++){
            MatchDetails matchDetails = new MatchDetails();
            try{
//                todo: improve scrolling and acessibility id
//                WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
//                driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])").get(2).findElements(By.className("android.widget.TextView")).get(0).getText();
                List<AndroidElement> matchesc = driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])");
                PointOption source = null;
                for(AndroidElement matchCard: matchesc){
                    List<MobileElement> texts = matchCard.findElementsByClassName("android.widget.TextView");
                    matchDetails =parseMatchDetails(texts.stream().map(WebElement::getText).collect(Collectors.toList()));
                    if(nextMatch!=null &&nextMatch.equals(matchDetails)){
                        matchCard.click();
                        TimeUnit.SECONDS.sleep(5);
                        if(isContest){
                            driver.findElementByAccessibilityId("btnContests").click();
                            return 0;
//                            click on contest
                        }else {
                            WebElement myTeam = driver.findElementByAccessibilityId("btnMy Teams");
                            myTeam.click();
                            return Integer.parseInt(myTeam.findElement(By.className("android.widget.TextView")).getText().replaceAll("[^0-9\\.]", ""));
//                            click on teams
                        }
                    }
                    source = PointOption.point(matchCard.getCenter());

                }
                scroll(source,destination,driver);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

//
        }
        return -1;
    }

    public static boolean isPointInRectangle(Rectangle rectangle, Point point){
        boolean y = rectangle.getY()<point.y && rectangle.getY()+rectangle.getHeight() >point.y;
        return y;
    }

    public static boolean isSelected(MobileElement elem,AppiumDriver driver) throws IOException {
        org.openqa.selenium.Point point = elem.getCenter();
        int centerX = point.getX();
        int centerY = point.getY();

        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

        BufferedImage image = ImageIO.read(scrFile);
// Getting pixel color by position x and y
        int clr = image.getRGB(centerX,centerY);
        int red   = (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue  =  clr & 0x000000ff;
        System.out.println("Red Color value = "+ red);
        System.out.println("Green Color value = "+ green);
        System.out.println("Blue Color value = "+ blue);
        return clr == -2273265;
    }
}
