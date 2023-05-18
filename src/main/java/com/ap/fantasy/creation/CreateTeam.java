package com.ap.fantasy.creation;

import com.ap.fantasy.model.*;
import static com.ap.fantasy.model.Constant.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class CreateTeam {
    final Logger logger = Logger.getLogger(CreateTeam.class.getName());
    public void init(List<FantasyTeamTO> teams, MatchDetails matchDetails, boolean recreateFlag) throws MalformedURLException {
//        todo: create multiple driver session
        List<AppiumDriver<AndroidElement>> drivers = new ArrayList<>();
        drivers.add(CreateDriverSession.getDriver(Udid.R5CT31D3G4F.name(),4723));
//        todo: distribute teams and call create team for each set
//        todo: parallel processing with multi core
//        todo: logs for each separately
//        make the create team call multi threaded
        List<List<FantasyTeamTO>> team = new ArrayList<>();
        team.add(teams.subList(0,20));
        team.add(teams.subList(21,40));
        team.add(teams.subList(0,20));
        final Iterator<List<FantasyTeamTO>> teamIt = team.iterator();
        drivers.parallelStream().forEach(driver -> {
            try {
                create(teamIt.next(), driver,matchDetails,recreateFlag);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


    }
    private void create(List<FantasyTeamTO> teams, AppiumDriver<AndroidElement> driver, MatchDetails matchDetails, boolean recreateFlag) throws InterruptedException {

        int numberOfTeamsAlreadyCreated=Helper.findMatch(matchDetails,driver,false);
        logger.info("Number of teams already created "+numberOfTeamsAlreadyCreated);
        logger.info("recreate flag status "+recreateFlag);
        if(numberOfTeamsAlreadyCreated !=-2){
            if(!recreateFlag && (numberOfTeamsAlreadyCreated ==20)){
                    return;

            }
            int cn =1;
            for(FantasyTeamTO team:teams){
                Instant matchStart = Instant.now();
//                todo recreate flag condition
//                todo: if team already edited
                if(!recreateFlag && (cn <= numberOfTeamsAlreadyCreated)){
                        cn++;
                        continue;

                }

                if(recreateFlag){
                    if(!Helper.selectTeamToEdit(cn,driver)){
                        Helper.notFound("edit team ->"+cn);
                    }
                }else {
                    try {
                         AndroidElement createButton = driver.findElementByAccessibilityId(CREATE_TEAM_BUTTON);
                        createButton.click();
                    }catch (NoSuchElementException e){
                        logger.info("0 team exist create team fails" +e.getMessage());
                    }
                    try {
                        WebElement createButton = driver.findElementByAccessibilityId(Constant.CREATE_TEAM_AFTER_FIRST_BUTTON);
//                        todo: if null button
                        createButton.click();
                    }catch (NoSuchElementException e){
                        logger.info("create team after first button not found "+e.getMessage());
                    }
                }
                Helper.wait(4);
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

                PointOption<?> source = null;
                PointOption<?> destination = null;
                System.out.println(team.getCaptain() + "cap");
                System.out.println(team.getVcaptain() + "vCap");
                boolean capFlag = false;
                boolean vCapFlag = false;
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
                        if(!capFlag && (curPlayer.equals(team.getCaptain()))){
                                player.findElementByAccessibilityId("Captain-element").click();
                                capFlag = true;

                        }
                        if(!vCapFlag && (curPlayer.equals(team.getVcaptain()))){
                                player.findElementByAccessibilityId("Vice Captain-element").click();
                                vCapFlag = true;

                        }
                        if(capFlag && vCapFlag){
                            break;
                        }
                        count++;
                    }

                    if (!(source == null || destination == null || (capFlag && vCapFlag))) {
                        TouchAction<?> actions = new TouchAction<>(driver);
                        actions.longPress(destination).moveTo(source).release().perform();
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                } while (!(capFlag && vCapFlag));

                WebElement saveButton = driver.findElementByAccessibilityId(SAVE_TEAM_BUTTON);
                WebElement preview = driver.findElementByAccessibilityId("team_preview_icon");
                var left =preview.getLocation();
                var right = saveButton.getLocation();
                TouchAction<?> action= new TouchAction<>(driver);
                PointOption<?> backupButton = PointOption.point((left.getX()+right.getX())/2, left.getY());
                action.tap(backupButton).perform();
                TimeUnit.SECONDS.sleep(1);
//                select top 4 backup players
                if(!recreateFlag){
                    driver.findElementByAccessibilityId("player-row-index-0-selected").click();
                    driver.findElementByAccessibilityId("player-row-index-1-selected").click();
                    driver.findElementByAccessibilityId("player-row-index-2-selected").click();
                    driver.findElementByAccessibilityId("player-row-index-3-selected").click();
                }
                saveButton = driver.findElementByAccessibilityId(SAVE_TEAM_BUTTON);
                if(saveButton.isEnabled()){
                    saveButton.click();
                    TimeUnit.SECONDS.sleep(3);
                    Instant matchEnd = Instant.now();
                    System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond())+matchDetails.getTeams().get(0).toString());
                }else {
                    System.out.println("error resolve");
                }
                cn++;
            }
        }
    }
    public static void selectPlayers(AppiumDriver<?> driver, PlayerType type, List<String> players) throws InterruptedException {
//        todo: make robust
//        todo: handle infinite loop possibility
        boolean flag = true;
        PointOption<?> source = null;
        PointOption<?> destination = null;
        boolean behindFlag;
        int i = 0;
        WebElement preview = driver.findElementByAccessibilityId("team_preview_icon");
        do {
            String playerRow = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+i+"-unselected";
            String playerRowAlternate = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+i+"-selected";

            try {
                WebElement pls;
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
                if (playerDetails.isEmpty() || behindFlag) {
                    if (!(source == null || destination == null)) {
                        TouchAction<?> actions = new TouchAction<>(driver);
                        actions.longPress(destination).moveTo(source).release().perform();
                        TimeUnit.SECONDS.sleep(2);
                    }
                    continue;

                }
                Player player = Helper.parsePlayer(playerDetails, type,i);
                if (player != null && player.isValid()) {
                    if (players.contains(player.getName().toLowerCase())) {

                        pls.click();

                        players.remove(player.getName().toLowerCase());
                        System.out.println("player selected");
                    }
                    i++;
                }
            } catch (NoSuchElementException ex) {
                System.out.println(ex.getMessage());
                flag = false;
            }


        } while (flag && !players.isEmpty());
    }



}
