package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateTeam {
    public void init(List<FantasyTeamTO> teams, MatchDetails matchDetails) throws MalformedURLException {
//        todo: create multiple driver session
        List<AppiumDriver> drivers = new ArrayList<>();
//        drivers.add(CreateDriverSession.getDriver(Uuid.R5CT31D3G4F.name(),4726));
        drivers.add(CreateDriverSession.getDriver(Uuid.MFM7A6LVH6YTMR8D.name(),4724));
        drivers.add(CreateDriverSession.getDriver(Uuid.b3c76eb6.name(),4725));
//        todo: distribute teams and call create team for each set
//        make the create team call multi threaded
        List<List<FantasyTeamTO>> team = new ArrayList<>();
        team.add(teams.subList(0,20));
//        team.add(teams.subList(10,40));
        team.add(teams.subList(10,teams.size()-1));
        team.add(teams.subList(20,teams.size()-1));
        final Iterator<List<FantasyTeamTO>> teamIt = team.iterator();
        drivers.parallelStream().forEach(driver -> {
            try {
                create(teamIt.next(), driver,matchDetails);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
    private void create(List<FantasyTeamTO> teams, AppiumDriver driver, MatchDetails matchDetails) throws InterruptedException, IOException {

        boolean garbageFlag = true;

//        call helper function to reach the match
        if(selectProMatch(driver,matchDetails)){

            List<MobileElement> d = driver.findElementsByClassName("android.widget.TextView");
            MobileElement tms = null;
            for(var team:d){
                if(team.getText().equals("My Teams")){
                    System.out.println(team.getText());
                    tms = team;
                    break;
                }
            }

            if(tms != null) {
                tms.click();
                TimeUnit.SECONDS.sleep(3);
            }

            boolean recreateFlag = true;

            int cn =0;
            int skip = 10;
            for(FantasyTeamTO team:teams){
                if(cn<skip){
                    cn++;
                    continue;
                }
                cn++;
                if(cn ==20){
                    System.out.println("done");
                    break;
                }
                System.out.println(team.getAl().size() +team.getBowl().size()+team.getBat().size()+team.getWk().size());
                d = driver.findElementsByClassName("android.widget.Button");
                for(var text:d){
                    System.out.println(text.getText());
                    if(text.getText().equals("CREATE A TEAM") || text.getText().equals("CREATE TEAM")){
                        System.out.println(text.getText());
                        recreateFlag = false;
                        text.click();
                        TimeUnit.SECONDS.sleep(3);
                        break;
                    }
                }

                if(recreateFlag){

                    boolean breakFlag = false;
//                    todo: scroll
                    PointOption source = null;
                    PointOption destination = null;
                    boolean onceFlag = true;
                    do {
                        List<WebElement> teamCardsLocator = driver.findElementsByClassName("android.widget.LinearLayout");

                        for (WebElement card : teamCardsLocator) {
                            String cd = card.getAttribute("content-desc");
                            if (cd != null && cd.equals("my_teams")) {
                                List<WebElement> names = card.findElements(By.className("android.widget.TextView"));
                                for (WebElement n : names) {
                                    if (n.getText().startsWith("Strange")) {
//                                    fetch count
                                        String name = n.getText();
                                        int beg = name.indexOf("(");
                                        int end = name.indexOf(")");
                                        name = name.substring(beg + 2, end);
                                        if (NumberUtils.isCreatable(name)) {
                                            int teamNo =Integer.valueOf(name);
                                            if(teamNo ==1){
                                                source= PointOption.point(card.getLocation());
                                            }else if(teamNo == 3 && onceFlag){
                                                onceFlag = false;
                                                destination = PointOption.point(card.getLocation());
                                            }
                                            if (teamNo == cn) {
                                                card.findElements(By.className("android.widget.ImageView")).get(1).click();
//                                                todo: time
                                                TimeUnit.SECONDS.sleep(2);
                                                breakFlag = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (breakFlag) {
                                    break;
                                }
                            }
                        }
//                        scroll
                        if(!(source ==null || destination== null || breakFlag)){
                            TouchAction actions = new TouchAction(driver);
                            actions.longPress(destination).moveTo(source).release().perform();
                            TimeUnit.SECONDS.sleep(2);
//                            actions.tap(destination).perform();
//                            TimeUnit.SECONDS.sleep(1);
                        }
                    }while (!breakFlag);


                }

                //        fetch buttons

                WebElement pointsWeb = null;
                WebElement wkWeb = null;
                WebElement batWeb = null;
                WebElement arWeb = null;
                WebElement bowlWeb = null;
//                added null check
                do {
                    WebElement typeH = driver.findElementByClassName("android.widget.HorizontalScrollView");
                    List<WebElement> types =typeH.findElements(By.className("android.widget.TextView"));
                    for (WebElement ty : types) {
                        if(wkWeb!= null && batWeb != null && arWeb!=null && bowlWeb !=null){
                            break;
                        }
                        switch (ty.getText()) {
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
                            case "POINTS":
                                pointsWeb = ty;
                        }
                    }
                }while (!(wkWeb!= null && batWeb != null && arWeb!=null && bowlWeb !=null));

                if(recreateFlag){
                    if(pointsWeb != null)
                        pointsWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver,PlayerType.WK);

                    batWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver,PlayerType.BAT);

                    arWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver, PlayerType.AR);

                    bowlWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver, PlayerType.BOWL);
                }


                if(garbageFlag){
                    if(pointsWeb != null)
                        pointsWeb.click();
                    wkWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver,PlayerType.WK);

                    batWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver,PlayerType.BAT);

                    arWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver, PlayerType.AR);

                    bowlWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    clearPlayers(driver, PlayerType.BOWL);
                }else{
//                    todo: click selected by
                    wkWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    selectPlayers(driver, PlayerType.WK,team.getWk());

                    batWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    selectPlayers(driver, PlayerType.BAT,team.getBat());

                    arWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    selectPlayers(driver, PlayerType.AR,team.getAl());

                    bowlWeb.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    selectPlayers(driver, PlayerType.BOWL,team.getBowl());
                }



                List<WebElement> nextTexts = driver.findElementsByClassName("android.widget.TextView");
                for(WebElement n:nextTexts){
                    if(n.getText().equals("NEXT")){
                        if(n.isEnabled()){
                            n.click();
                            TimeUnit.SECONDS.sleep(1);
                            break;
                        }else {
                            System.out.println("error resolve");
                        }
                    }
                }

                if(garbageFlag){
                    WebElement rec = driver.findElementByClassName("androidx.recyclerview.widget.RecyclerView");
                    List<WebElement> views = rec.findElements(By.className("android.view.ViewGroup"));

                    boolean capFlag = false;
                    boolean vCapFlag = false;
                    boolean onceFlag = true;
                    boolean onceVFlag = true;
                        int count = 0;
                        int cCount=cn%5;
                        int vCount= cn/5+1;
                        if(cCount == vCount){
                            cCount++;
                        }
                        for (WebElement v : views) {
                            List<WebElement> texts = v.findElements(By.className("android.widget.TextView"));
                            count++;
                            WebElement cap = null;
                            WebElement vCap = null;

                            for (WebElement t : texts) {
                                if (t.getText().equals("C")) {
                                    cap = t;
                                } else if (t.getText().equals("VC")) {
                                    vCap = t;
                                } else if (count == cCount) {
                                    capFlag = true;
                                } else if (count == vCount) {
                                    vCapFlag = true;
                                }
                            }
                            if (capFlag && onceFlag) {
                                cap.click();
                                onceFlag = false;
                            } else if (vCapFlag && onceVFlag) {
                                vCap.click();
                                onceVFlag = false;
                            }

                            if (capFlag && vCapFlag) {
                                break;
                            }

                        }

                }else {
                    PointOption source = null;
                    PointOption destination = null;
                    System.out.println(team.getCaptain() + "cap");
                    System.out.println(team.getVcaptain() + "vcap");
                    boolean capFlag = false;
                    boolean vCapFlag = false;
                    boolean onceFlag = true;
                    boolean onceVFlag = true;
                    int count = 0;
                    do {
                        WebElement rec = driver.findElementByClassName("androidx.recyclerview.widget.RecyclerView");
                        List<WebElement> views = rec.findElements(By.className("android.view.ViewGroup"));
                        count = 0;
                        for (WebElement v : views) {
                            if (count == 1) {
                                source = PointOption.point(v.getLocation());
                            } else if (count == 5) {
                                destination = PointOption.point(v.getLocation());
                            }
                            count++;

                            List<WebElement> texts = v.findElements(By.className("android.widget.TextView"));
                            WebElement cap = null;
                            WebElement vCap = null;

                            for (WebElement t : texts) {
                                if (t.getText().equals("C")) {
                                    cap = t;
                                } else if (t.getText().equals("VC")) {
                                    vCap = t;
                                } else if (t.getText().equals(team.getCaptain())) {
                                    capFlag = true;
                                } else if (t.getText().equals(team.getVcaptain())) {
                                    vCapFlag = true;
                                }
                            }
                            if (capFlag && onceFlag) {
                                cap.click();
                                onceFlag = false;
                            } else if (vCapFlag && onceVFlag) {
                                vCap.click();
                                onceVFlag = false;
                            }

                            if (capFlag && vCapFlag) {
                                break;
                            }

                        }

                        if (!(source == null || destination == null || (capFlag && vCapFlag))) {
                            TouchAction actions = new TouchAction(driver);
                            actions.longPress(destination).moveTo(source).release().perform();
                            TimeUnit.MILLISECONDS.sleep(500);
                            actions.tap(destination).perform();
                            TimeUnit.MILLISECONDS.sleep(200);
//                actions.longPress(PointOption.point(598,1584)).moveTo(PointOption.point(520,977)).release().perform();
                        }
                    } while (!(capFlag && vCapFlag));
                }
                List<WebElement> saveButs = driver.findElementsByClassName("android.widget.TextView");
                for(WebElement n:saveButs){
                    if(n.getText().equals("SAVE")){
                        if(n.isEnabled()){
                            n.click();
                            TimeUnit.SECONDS.sleep(3);
                        }else {
                            System.out.println("error resolve");
                        }
                    }
                }


//                travel and parse players
//                find save button
//                done

            }





        }




//        travel players
//        keep tapping
    }

    public Boolean selectProMatch(AppiumDriver driver, MatchDetails matchDetails) throws MalformedURLException, InterruptedException {
        TimeUnit.SECONDS.sleep(3);
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
                return true;
            }

        }
        return false;
    }
    public static void selectPlayers(AppiumDriver driver, PlayerType type, List<String> players) throws InterruptedException {
        int index=0;
        int count =0;
        int size = players.size();
        boolean flag = false;
        PointOption source = null;
        PointOption destination = null;
        do {
            List<WebElement> pls = driver.findElementsByClassName("androidx.recyclerview.widget.RecyclerView");
            flag = !(pls.size()==3);

            for(WebElement p:pls){

                List<WebElement> ps = p.findElements(By.className("android.view.ViewGroup"));
                if(ps.size()>4){
                    ps = ps.subList(0,4);
                }

                for (WebElement temp : ps) {

                    if (temp.getAttribute("content-desc") == null) {
                        break;
                    }
                    if (temp.getAttribute("content-desc").equals("create_team_player_row")) {
                        //fetch player details
                        if(index ==0){
                            source = PointOption.point(temp.getLocation());
                        }else if (index== 2){
                            destination = PointOption.point(temp.getLocation());
                        }
                        index++;
                        List<WebElement> playerDetails = temp.findElements(By.className("android.widget.TextView"));
                        Player player = Helper.parsePlayer(playerDetails, type);
                        if(player == null){
                            continue;
                        }
                        if(players.contains(player.getName().toLowerCase())){
                            temp.click();
                            count++;
                            players.remove(player.getName().toLowerCase());
                        }
                        if(count==size){
                            return;
                        }

                    }

                }

            }
            if(!(source ==null || destination== null)){
                TouchAction actions = new TouchAction(driver);
                actions.longPress(destination).moveTo(source).release().perform();
                TimeUnit.MILLISECONDS.sleep(500);
                actions.tap(destination).perform();
                TimeUnit.MILLISECONDS.sleep(100);
//                actions.longPress(PointOption.point(598,1584)).moveTo(PointOption.point(520,977)).release().perform();
            }
        }while (flag || players.isEmpty());
    }


    public static void clearPlayers(AppiumDriver driver,PlayerType type) throws IOException, InterruptedException {
        int count =0;
        int size = 3;
        if(type.equals(PlayerType.WK)){
            size= 2;
        }
        boolean flag = false;
        List<WebElement> pls = driver.findElementsByClassName("androidx.recyclerview.widget.RecyclerView");
        for(WebElement p:pls){
            List<WebElement> ps = p.findElements(By.className("android.view.ViewGroup"));

            for (WebElement temp : ps) {

                if (temp.getAttribute("content-desc") == null) {
                    break;
                }
                if (temp.getAttribute("content-desc").equals("create_team_player_row")) {
                    //fetch player details
                    List<WebElement> playerDetails = temp.findElements(By.className("android.widget.TextView"));
                    temp.click();
                    count++;
                    if(count==size){
                        return;
                    }
                }
            }

        }
    }

}
