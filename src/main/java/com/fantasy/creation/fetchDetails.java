package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.EnumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class fetchDetails {


    public List<MatchDetails> fetch(MatchDetails nextMatch) throws InterruptedException, MalformedURLException {
        AppiumDriver driver = CreateDriverSession.getDriver("",0);
        List<MatchDetails> matches = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);
        for(int i = 1; i<7; i++){
//            todo: add scrolling feature
            MatchDetails matchDetails = new MatchDetails();

            try{
                WebElement match =driver.findElementByAccessibilityId("Match_Card_"+i);
                if(match == null){
                    continue;
                }
                List<WebElement> texts = match.findElements(By.className("android.widget.TextView"));
                List<Team> teamsPlaying = new ArrayList<>();
                for(WebElement text:texts){
                    System.out.println(text.getText());
                    if(EnumUtils.isValidEnumIgnoreCase(Team.class,text.getText().replaceAll("-",""))){
                        teamsPlaying.add(Team.valueOf(text.getText().replaceAll("-","")));
                    }
//                    todo: fetch time
//                    todo: fetch prize pool
//                    todo: fetch tournament name
                }
                matchDetails.setFirst(teamsPlaying.get(0));
                matchDetails.setSecond(teamsPlaying.get(1));
                if(teamsPlaying.size()!=2){
                    throw (new Exception("unknown teams"));
                }

//                todo improve logic
                if(nextMatch.getFirst() == null){
                    match.click();
                    TimeUnit.SECONDS.sleep(5);
                    MatchDetails details =travelMatch(driver,matchDetails);
                    return Collections.singletonList(details);
                }else{
                    matches.add(matchDetails);
                }



            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

//
        }
//        todo: filter matches
        return matches;
    }

    public static MatchDetails travelMatch(AppiumDriver driver,MatchDetails matchDetails) throws InterruptedException {


        List<MobileElement> d = driver.findElementsByClassName("android.widget.TextView");
        MobileElement teams = null;
        for(var team:d){
            if(team.getText().equals("My Teams")){
                System.out.println(team.getText());
                teams = team;
                break;
            }
        }
        assert teams != null;
        teams.click();
        TimeUnit.SECONDS.sleep(3);
        d = driver.findElementsByClassName("android.widget.Button");
        for(var team:d){
            System.out.println(team.getText());
            if(team.getText().equals("CREATE A TEAM") || team.getText().equals("CREATE TEAM")){
                System.out.println(team.getText());
                team.click();
                break;
            }
        }

        TimeUnit.SECONDS.sleep(3);
        System.out.println("Start fetching");
//        todo improve time
        String time = driver.findElementById("com.app.dream11Pro:id/tvCountDownTimer").getText();
        List<String> timeParse = List.of(time.split(" "));
        int h=0,m=0,s=0;
        int num=0;
        for(char ch : timeParse.get(0).toCharArray()){

            if(Character.isDigit(ch)){
                num =num *10+Integer.parseInt(String.valueOf(ch));
            }else{
                if(ch=='h'){
                    h=num;
                }else{
                    m=num;
                }
            }
        }
        num=0;
        for(char ch : timeParse.get(1).toCharArray()){

            if(Character.isDigit(ch)){
                num =num *10+Integer.parseInt(String.valueOf(ch));
            }else{
                if(ch=='m'){
                    m=num;
                }else{
                    s=num;
                }
            }
        }
        Date t = Date.from(Instant.now().plusSeconds(h * 3600 + m * 60 + s));
        matchDetails.setTime(t);
        String toss = "";
//                TODO: batting first and second who won the toss
//        team won the toss and elected to bat/ bowl first parse


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
