package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CreateTeam {
    Logger logger = Logger.getLogger("create team");
    public void init(List<FantasyTeamTO> teams, MatchDetails matchDetails, boolean recreateFlag) throws MalformedURLException {
//        todo: create multiple driver session
        List<AppiumDriver> drivers = new ArrayList<>();
        drivers.add(CreateDriverSession.getDriver(Udid.R52R40L8X6Z.name(),4723));
        drivers.add(CreateDriverSession.getDriver(Udid.R52R40L8XAY.name(),4724));
        drivers.add(CreateDriverSession.getDriver(Udid.R5CT31D3G4F.name(),4725));
//        todo: distribute teams and call create team for each set
//        make the create team call multi threaded
        List<List<FantasyTeamTO>> team = new ArrayList<>();
        team.add(teams.subList(0,20));
        team.add(teams.subList(21,40));
        team.add(teams.subList(0,20));
        final Iterator<List<FantasyTeamTO>> teamIt = team.iterator();
        final Iterator<AppiumDriver> driverIt = drivers.iterator();
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    AppiumDriver driver = driverIt.next();
//                    System.out.println("Look at me, look at me..."+driver.getCapabilities().getCapability(MobileCapabilityType.UDID));
//                    create(teamIt.next(), driver,matchDetails,recreateFlag);
//                    System.out.println("Look ma, no hands");
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    AppiumDriver driver = driverIt.next();
//                    System.out.println("Look at me, look at me..."+driver.getCapabilities().getCapability(MobileCapabilityType.UDID));
//                    create(teamIt.next(), driver,matchDetails,recreateFlag);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        }).start();
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

        int numberOfTeamsAlreadyCreated=Helper.getEventMatchToCreateTeam(matchDetails,driver,false);
        logger.info("Number of teams already created "+numberOfTeamsAlreadyCreated);
        logger.info("recreate flag status "+recreateFlag);
        if(numberOfTeamsAlreadyCreated !=-1){
            if(!recreateFlag){
                if(numberOfTeamsAlreadyCreated ==20){
                    return;
                }
            }
            int cn =1;
            int skip = numberOfTeamsAlreadyCreated;
            skip=0;
            for(FantasyTeamTO team:teams){
                Instant matchs = Instant.now();
//                todo recreate flag condition
//                todo: if team already edited
                if(cn<skip){
                    cn++;
                    continue;
                }

                if(cn >20){
                    logger.info("Done");
                    break;
                }
                logger.info(String.valueOf(team.getAl().size() +team.getBowl().size()+team.getBat().size()+team.getWk().size()));
                if(recreateFlag){
                    Helper.SelectTeamToEdit(cn,driver);
                }else {
                    try {
                        WebElement createButton = driver.findElementByAccessibilityId("create-team-btn");
                        createButton.click();
                    }catch (NoSuchElementException e){
                        logger.info("0 team exist create team fails" +e.getMessage());
                    }
                    try {
                        WebElement createButton = driver.findElementByAccessibilityId("my-teams-fab");
                        createButton.click();
                    }catch (NoSuchElementException e){
                        logger.info("my team fab not found "+e.getMessage());
                    }
                }


                TimeUnit.SECONDS.sleep(4);


                //        fetch buttons
                PlayerTypeButton button = Helper.findPlayerTypeButtons(driver);
                assert (!(button.getWkWeb()!= null && button.getBatWeb() != null && button.getArWeb()!=null && button.getBowlWeb() !=null));
                logger.info("Buttons found");
                if(recreateFlag){
                    button.getWkWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.WK,button.getWkWeb());

                    button.getBatWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.BAT,button.getBatWeb());

                    button.getArWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.AR,button.getArWeb());

                    button.getBowlWeb().click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    Helper.clearPlayers(driver, PlayerType.BOWL,button.getBowlWeb());
                }

                button.getWkWeb().click();
                logger.info("Selecting wicketkeepers");
                TimeUnit.MILLISECONDS.sleep(200);
                selectPlayers(driver, PlayerType.WK,team.getWk());

                button.getBatWeb().click();
                logger.info("Selecting batsmen");
                TimeUnit.MILLISECONDS.sleep(200);
                selectPlayers(driver, PlayerType.BAT,team.getBat());

                button.getArWeb().click();
                logger.info("Selecting al rounders");
                TimeUnit.MILLISECONDS.sleep(200);
                selectPlayers(driver, PlayerType.AR,team.getAl());

                button.getBowlWeb().click();
                logger.info("Selecting bowlers");
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
                    Instant matche = Instant.now();
                    System.out.println(matche.minusSeconds(matchs.getEpochSecond())+matchDetails.getTeams().get(0).toString());
                }else {
                    System.out.println("error resolve");
                }
                cn++;
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
            String playerRow = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+i+"-unselected";
            String playerRowAlternate = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+i+"-selected";

            try {
                WebElement pls = null;
                try {
                    pls = driver.findElementByAccessibilityId(playerRow);
                }catch (NoSuchElementException ex){
                    pls = driver.findElementByAccessibilityId(playerRowAlternate);
                    System.out.println(playerRowAlternate);
                }
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
                        System.out.println("player selected");
                    }
                    i++;
                }
            } catch (NoSuchElementException ex) {
                System.out.println(ex.getMessage());
                flag = false;
            }


        } while (flag && players.size()!=0);
    }



}
