package com.ap.fantasy.creation.dream11;

import com.ap.fantasy.creation.Helper;
import com.ap.fantasy.model.MatchDetails;
import com.ap.fantasy.model.Player;
import com.ap.fantasy.model.PlayerType;
import com.ap.fantasy.model.PlayerTypeButton;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.ap.fantasy.model.Constant.*;

public class CreateOrEditTeam {
    private static final Logger log = Logger.getLogger(CreateOrEditTeam.class.getName());
    public static void fetchMatchStats(AppiumDriver<?> driver){
        WebElement fc =driver.findElementByAccessibilityId(FAN_CODE_STRIP_VIEW);
        List<WebElement> textsElement = fc.findElements(By.className(CLASS_TEXT_VIEW));
        List<String> texts = textsElement.stream().map(WebElement::getText).toList();
//        todo: fetch stats in order
    }

    public static PlayerTypeButton findPlayerTypeButtons(AppiumDriver<AndroidElement> driver){
//        check for null values before returning
        AndroidElement wkWeb = driver.findElementByAccessibilityId("btnWK");
        AndroidElement batWeb = driver.findElementByAccessibilityId("btnBAT");
        AndroidElement arWeb = driver.findElementByAccessibilityId("btnAR");
        AndroidElement bowlWeb = driver.findElementByAccessibilityId("btnBOWL");
        return new PlayerTypeButton(wkWeb,batWeb,arWeb,bowlWeb);
    }

    public static List<Player> fetchPlayer(AppiumDriver<?> driver, PlayerType type) throws InterruptedException {
        log.info("Start reading players");
        Set<Player> players = new HashSet<>();
        boolean flag = true;
        PointOption<?> source = null;
        PointOption<?> destination = null;
        boolean behindFlag;
        WebElement preview = driver.findElementByAccessibilityId(PREVIEW);
        int i=0;
        do {
            String playerRow = PLAYER_ROW_PREFIX+HYPHEN+i+HYPHEN+SELECTED;
            try {
                WebElement pls = driver.findElementByAccessibilityId(playerRow);
                behindFlag = Helper.isPointInRectangle(pls.getRect(),preview.getLocation());
                List<WebElement> playerDetails = pls.findElements(By.className(CLASS_TEXT_VIEW));
                if(i==0){
                    source = PointOption.point(pls.getLocation());
                }else if (i==3){
                    destination = PointOption.point(pls.getLocation());
                }
                if(playerDetails.size() ==0 ||behindFlag){
                    Helper.scroll(destination,source,driver,false);
                    continue;

                }
                Player player =Helper.parsePlayer(playerDetails,type,i);
                if(player != null && player.isValid()){
                    log.info("successfully read the player "+player.getName() );
                    players.add(player);
                    i++;
                }
            }catch (NoSuchElementException ex){
                flag = false;
                log.info("Parsing player completed of type "+type);
            }
        }while (flag);
        log.info("number of players parsed: "+players.size());
        return players.stream().toList();
    }

//    todo: complete this method with a return type if needed
    public static void fetchLineup(AppiumDriver<?> driver, MatchDetails match) {
        driver.findElementByAccessibilityId("line_ups_icon").click();
        var lineup =driver.findElementByAccessibilityId(ANNOUNCED_LINEUPS);
        List<String> playerCandidate = new ArrayList<>();
        lineup.findElements(By.className(CLASS_SCROLL_VIEW)).
                forEach(scrolls -> scrolls.findElements(By.className(CLASS_TEXT_VIEW)).
                        forEach(textView -> playerCandidate.add(textView.getText())));
        var players = match.getPlayers();
        int tCount = 0;
        int t2Count = 0;
        for(String player: playerCandidate){
            var playerOptional =players.stream().filter(p -> p.getName().equalsIgnoreCase(player)).findFirst();
            if(playerOptional.isPresent()){
                var p = playerOptional.get();
                p.setPlaying(true);
                if(p.getTeam().equals(match.getTeams().get(0))) {
                    p.setBattingOrder(++tCount);
                }else{
                    if(p.getTeam().equals(match.getTeams().get(1))){
                        p.setBattingOrder(++t2Count);
                    }
                }
            }
        }
    }

    public static void clearPlayers(AppiumDriver<AndroidElement> driver,PlayerType type,WebElement category) throws InterruptedException {
//        handle infinite loop
        PointOption<?> source = null;
        boolean sourceFlag = true;
        WebElement preview = driver.findElementByAccessibilityId(PREVIEW);
        log.info("Preview button found");
        do {
            List<AndroidElement> players = new ArrayList<>();
            AndroidElement pls;
            for(int i = 0; i<12; i++) {
                try {
                    String playerRow = PLAYER_ROW_PREFIX+HYPHEN+i+HYPHEN+UNSELECTED;
                    AndroidElement player = driver.findElementByAccessibilityId(playerRow);
                    players.add(player);
                    if(sourceFlag){
                        source = PointOption.point(player.getCenter());
                        sourceFlag = false;
                    }
                    log.info("found "+playerRow);
                }catch (NoSuchElementException e){
                    String playerRowAlternate = PLAYER_ROW_PREFIX+HYPHEN+i+HYPHEN+SELECTED;
                    String playerRow = PLAYER_ROW_PREFIX+HYPHEN+i+HYPHEN+UNSELECTED;
                    log.warning("player not found"+playerRow+e.getMessage());
                    try {
                        players.add(driver.findElementByAccessibilityId(playerRow));
                        log.info("found "+playerRowAlternate);
                    }catch (NoSuchElementException ex){
                        log.warning("player not found"+playerRowAlternate+ex.getMessage());
                    }
                }
            }
            log.info("Number of players found "+players.size());
            boolean behindFlag;
            for(AndroidElement player:players){
                behindFlag =Helper.isPointInRectangle(player.getRect(),preview.getLocation());
                if(!behindFlag){
                    int playerCount =findCountForPlayerType(category);
                    player.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    int updatedCount =findCountForPlayerType(category);
                    if(updatedCount>=playerCount){
                        player.click();
                    }
                }
            }
            if (source != null) {
                Helper.scroll(source,source.withCoordinates(0,-400),driver,false);
            }
        }while (findCountForPlayerType(category) !=0);
//        todo: reverse scroll deem it useless
        String playerRow = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+0+"-unselected";
        String playerRowAlternate = "CREATE_TEAM_PLAYER_ITEM_VIEW-"+0+"-selected";
        boolean reverseFlag;
        do{
            WebElement pls = null;
            try {
                pls = driver.findElementByAccessibilityId(playerRow);
                reverseFlag = true;
            }catch (NoSuchElementException ex){
                try {
                    pls = driver.findElementByAccessibilityId(playerRowAlternate);
                    reverseFlag = true;
                }catch (NoSuchElementException e){
                    reverseFlag = false;
                    Helper.scroll(source.withCoordinates(0,-400),source,driver,false);
                }
            }


        }while (!reverseFlag);
    }

    public static int findCountForPlayerType(WebElement button){
        return Integer.parseInt(button.findElement(By.className("android.widget.TextView")).getText().replaceAll(DIGIT_ONLY_REGEX, ""));
    }
}
