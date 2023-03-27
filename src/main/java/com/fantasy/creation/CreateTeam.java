package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateTeam {
    public void init(List<FantasyTeamTO> teams, MatchDetails matchDetails, boolean recreateFlag) throws MalformedURLException {
//        todo: create multiple driver session
        List<AppiumDriver> drivers = new ArrayList<>();
        drivers.add(CreateDriverSession.getDriver(Uuid.R5CT31D3G4F.name(),4723));
//        drivers.add(CreateDriverSession.getDriver(Uuid.MFM7A6LVH6YTMR8D.name(),4724));
//        drivers.add(CreateDriverSession.getDriver(Uuid.b3c76eb6.name(),4725));
//        todo: distribute teams and call create team for each set
//        make the create team call multi threaded
        List<List<FantasyTeamTO>> team = new ArrayList<>();
        team.add(teams.subList(0,20));
//        team.add(teams.subList(21,40));
//        team.add(teams.subList(41,60));
        final Iterator<List<FantasyTeamTO>> teamIt = team.iterator();
        drivers.parallelStream().forEach(driver -> {
            try {
                create(teamIt.next(), driver,matchDetails,recreateFlag);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
    private void create(List<FantasyTeamTO> teams, AppiumDriver driver, MatchDetails matchDetails, boolean recreateFlag) throws InterruptedException, IOException {

        int numberOfTeamsAlreadyCreated=Helper.findMatch(matchDetails,driver,false);
        if(numberOfTeamsAlreadyCreated !=-1){
            int cn =1;
            int skip = numberOfTeamsAlreadyCreated;
            for(FantasyTeamTO team:teams){
//                todo recreate flag condition
//                todo: if team already edited
                if(cn<skip && !recreateFlag){
                    cn++;
                    continue;
                }
                cn++;
                if(cn >20){
                    System.out.println("done");
                    break;
                }
                System.out.println(team.getAl().size() +team.getBowl().size()+team.getBat().size()+team.getWk().size());
                if(recreateFlag){
                    Helper.SelectTeamToEdit(cn,driver);
                }else {
                    try {
                        WebElement createButton = driver.findElementByAccessibilityId("create-team-btn");
                        createButton.click();
                    }catch (NoSuchElementException e){
                        System.out.println("0 team exist create team fails" +e.getMessage());
                    }
                    try {
                        WebElement createButton = driver.findElementByAccessibilityId("my-teams-fab");
                        createButton.click();
                    }catch (NoSuchElementException e){
                        System.out.println("20 teams already created \n" + e.getMessage());
                    }
                }


                TimeUnit.SECONDS.sleep(3);


                //        fetch buttons
                PlayerTypeButton button = Helper.findPlayerTypeButtons(driver);
                assert (!(button.getWkWeb()!= null && button.getBatWeb() != null && button.getArWeb()!=null && button.getBowlWeb() !=null));
                if(recreateFlag){
                    button.getWkWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.WK);

                    button.getBatWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.BAT);

                    button.getArWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.AR);

                    button.getBowlWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.BOWL);
                }

                button.getWkWeb().click();
                TimeUnit.MILLISECONDS.sleep(200);
                selectPlayers(driver, PlayerType.WK,team.getWk());

                button.getBatWeb().click();
                TimeUnit.MILLISECONDS.sleep(200);
                selectPlayers(driver, PlayerType.BAT,team.getBat());

                button.getArWeb().click();
                TimeUnit.MILLISECONDS.sleep(200);
                selectPlayers(driver, PlayerType.AR,team.getAl());

                button.getBowlWeb().click();
                TimeUnit.MILLISECONDS.sleep(200);
                selectPlayers(driver, PlayerType.BOWL,team.getBowl());

//                click next
                driver.findElementByAccessibilityId("create-team-NEXT-button").click();
                TimeUnit.SECONDS.sleep(1);

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
                    List<AndroidElement> players = driver.findElementsByAccessibilityId("special-player-selector-item");
//                    todo: scroll cvc check
                    for(AndroidElement player:players){
                        String curPlayer =player.findElementByAccessibilityId("player-name").getText();
                        if(count ==0){
                            destination = PointOption.point(player.getLocation());
                        }else if (count ==4){
                            source = PointOption.point(player.getLocation());
                        }else if (count >6){
                            continue;
                        }
                        if(!capFlag){
                            if(curPlayer.equals(team.getCaptain())){
                                player.findElementByAccessibilityId("Captain-element").click();
                                capFlag = true;
                            }
                        }
                        if(!vCapFlag){
                            if(curPlayer.equals(team.getVcaptain())){
                                player.findElementByAccessibilityId("Vice Captain-element").click();
                                vCapFlag = true;
                            }
                        }
                        if(capFlag && vCapFlag){
                            break;
                        }
                        count++;
                    }

                    if (!(source == null || destination == null || (capFlag && vCapFlag))) {
                        TouchAction actions = new TouchAction(driver);
                        actions.longPress(destination).moveTo(source).release().perform();
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                } while (!(capFlag && vCapFlag));

                WebElement saveButton = driver.findElementByAccessibilityId("create-team-SAVE-button");
                if(saveButton.isEnabled()){
                    saveButton.click();
                    TimeUnit.SECONDS.sleep(3);
                }else {
                    System.out.println("error resolve");
                }
            }
        }
    }
    public static void selectPlayers(AppiumDriver driver, PlayerType type, List<String> players) throws InterruptedException {
//        todo: make robust
//        todo: handle infinite loop possibility
        int index = 0;
        int count = 0;
        int size = players.size();
        boolean flag = true;
        PointOption source = null;
        PointOption destination = null;
        boolean behindFlag = false;
        int i = 0;
        WebElement preview = driver.findElementByAccessibilityId("team_preview_icon");
        do {
            String playerRow = "CREATE_TEAM_PLAYER_ITEM_VIEW-" + i + "-selected";
            try {
                WebElement pls = driver.findElementByAccessibilityId(playerRow);
                behindFlag =Helper.isPointInRectangle(pls.getRect(),preview.getLocation());
                List<WebElement> playerDetails = pls.findElements(By.className("android.widget.TextView"));
                if (i == 0) {
                    source = PointOption.point(pls.getLocation());
                } else if (i == 3) {
                    destination = PointOption.point(pls.getLocation());
                }
                if (playerDetails.size() == 0 || behindFlag) {
                    if (!(source == null || destination == null)) {
                        TouchAction actions = new TouchAction(driver);
                        actions.longPress(destination).moveTo(source).release().perform();
                        TimeUnit.SECONDS.sleep(2);
                    }
                    continue;

                }
                Player player = Helper.parsePlayer(playerDetails, type);
                if (player.isValid() ){
                    if(players.contains(player.getName().toLowerCase())){

                        pls.click();
//                    todo verify if clicked
                        count++;
                        players.remove(player.getName().toLowerCase());
                    }
                    i++;
                }
            } catch (NoSuchElementException ex) {
                flag = false;
            }


        } while (flag && players.size()!=0);
    }



}
