package com.ap.fantasy.creation;

import com.ap.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.ap.fantasy.model.Constant.*;

public class Helper {

    private Helper(){}
    public static final Logger log = Logger.getLogger("Helper.class");


    public static Player parsePlayer(List<WebElement> playerDetails, PlayerType type, int playerRow){
//        todo: check at later stage
        Map<String, String> pls = playerDetails.stream().collect(Collectors.toMap(WebElement::getTagName, WebElement::getText));
        Player player = new Player(pls,type,playerRow);
        if(player.getName().isBlank() && player.getTeam() ==null){
            return null;
        }
        return player;
    }

    public static void scroll(PointOption<?> source, PointOption<?> destination, AppiumDriver<?> driver,boolean isTap ) throws InterruptedException {
        if((source ==null || destination== null)){{
            notFound("Source or destination is null");
        }}
        TouchAction<?> actions = new TouchAction<>(driver);
        actions.longPress(source).moveTo(destination).release().perform();
        wait(2);
        if(isTap){
            actions.tap(source).perform();
        }
    }

    public static float amountInWordToNumber(String amount){
        int crore = 10000000;
        int lakh = 100000;
        float n = 0;
        if(amount.contains("Crore")){
            amount =amount.replaceAll(DIGIT_ONLY_REGEX, "");
            n =Float.parseFloat(amount);
            n*=crore;
        }else if(amount.contains("Lakh")){
            amount =amount.replaceAll(DIGIT_ONLY_REGEX, "");
            n =Float.parseFloat(amount);
            n*=lakh;
        }
        return n;
    }

    public static Date parseTime(String time){
        var timeSplit = Arrays.stream(time.split(" ")).toList();
        int second = 0;
        for (String s : timeSplit) {
            if (s.contains("h")) {
                var hour = Integer.parseInt(s.replaceAll("h", ""));
                second += hour * 60 * 60;
            }
            if (s.contains("m")) {
                var min = Integer.parseInt(s.replaceAll("m", ""));
                second += min * 60;
            }
            if (s.contains("s")) {
                var sec = Integer.parseInt(s.replaceAll("s", ""));
                second += sec;
            }
        }
        return Date.from(new Date().toInstant().plusSeconds(second));
    }

    public static PlayerTypeButton findPlayerTypeButtons(AppiumDriver<AndroidElement> driver){
//        check for null values before returning
        AndroidElement wkWeb = driver.findElementByAccessibilityId("btnWK");
        AndroidElement batWeb = driver.findElementByAccessibilityId("btnBAT");
        AndroidElement arWeb = driver.findElementByAccessibilityId("btnAR");
        AndroidElement bowlWeb = driver.findElementByAccessibilityId("btnBOWL");
        return new PlayerTypeButton(wkWeb,batWeb,arWeb,bowlWeb);
    }

    public static int findCountForPlayerType(WebElement button){
        return Integer.parseInt(button.findElement(By.className("android.widget.TextView")).getText().replaceAll(DIGIT_ONLY_REGEX, ""));

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
                behindFlag =Helper.isPointInRectangle(pls.getRect(),preview.getLocation());
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

    public static boolean selectTeamToEdit(int teamNo, AppiumDriver<AndroidElement> driver){
        int scrollOffset = -500;
        int currentTeam = 0;
        do {

            List<AndroidElement> teamCards = driver.findElementsByAccessibilityId(TEAM_CARD);
            for (AndroidElement teamCard : teamCards) {
                try {
                    List<MobileElement> teamNames = teamCard.findElementsByClassName(CLASS_TEXT_VIEW);
                    boolean flag = false;
                    for (MobileElement teamCandidate:teamNames){
                        String text = teamCandidate.getText();
                        if(text.startsWith("(T")){
                            currentTeam = Integer.parseInt(text.replaceAll(DIGIT_ONLY_REGEX, ""));
                            flag = true;
                            break;
                        }
                    }
                    log.info(currentTeam +" - "+teamNo);
                    if(!flag){
                        continue;
                    }
                    if (currentTeam == teamNo) {
                        AndroidElement editButton = (AndroidElement) teamCard.findElementByAccessibilityId("my-teams-edit-team");
                        editButton.click();
                        return true;
                    } else if (teamNo - currentTeam >= 3) {
                        try {
                            scroll(PointOption.point(teamCard.getLocation()), PointOption.point(teamCard.getLocation().moveBy(0, (int) (teamCard.getRect().height*-2.5))), driver,false);
                            break;
                        } catch (InterruptedException ignored) {

                        }
                    }else if (currentTeam >teamNo ){
                        try {
                            scroll(PointOption.point(teamCard.getLocation()), PointOption.point(teamCard.getLocation().moveBy(0, (int) (teamCard.getRect().height*2.5))), driver,false);
                            break;
                        } catch (InterruptedException ignored) {

                        }

                    }
                }catch (NoSuchElementException ex){
                    System.out.println("element not found");
                }


            }
        }while (currentTeam <=20);
        return false;
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
                    int playerCount =Helper.findCountForPlayerType(category);
                    player.click();
                    TimeUnit.MILLISECONDS.sleep(200);
                    int updatedCount =Helper.findCountForPlayerType(category);
                    if(updatedCount>=playerCount){
                        player.click();
                    }
                }
            }
            if (source != null) {
                Helper.scroll(source,source.withCoordinates(0,-400),driver,false);
            }
        }while (Helper.findCountForPlayerType(category) !=0);
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
                    scroll(source.withCoordinates(0,-400),source,driver,false);
                }
            }


        }while (!reverseFlag);
    }
    public static void notFound(String param){
        log.info("Not found "+ param);
    }

    public static void wait(int t) throws InterruptedException {
        TimeUnit.SECONDS.sleep(t);
    }
    public static int clickOnMyTeams(AppiumDriver<?> driver) throws InterruptedException {
        int teamCount = 0;
        try {
            var myTeam = driver.findElementByAccessibilityId(Constant.MY_TEAMS);
            myTeam.click();
            TimeUnit.SECONDS.sleep(3);
            teamCount =  Integer.parseInt(myTeam.findElement(By.className(CLASS_TEXT_VIEW)).getText().replaceAll(DIGIT_ONLY_REGEX, ""));
        }catch (NoSuchElementException e){
            Helper.notFound(Constant.MY_TEAMS);
        }
        return teamCount;
    }

    public static void clickOnCreateTeam(AppiumDriver<?> driver) throws InterruptedException {
        log.info("Clicking on create team");
        int teamCount = clickOnMyTeams(driver);
        WebElement createFirstTeam;
        try {
            if(teamCount == 0){
                createFirstTeam = driver.findElementByAccessibilityId(Constant.CREATE_TEAM_BUTTON);
            }else {
                createFirstTeam = driver.findElementByAccessibilityId(Constant.CREATE_TEAM_AFTER_FIRST_BUTTON);
            }
            createFirstTeam.click();
            Helper.wait(3);
            log.info("Clicked on create team");
        }catch (NoSuchElementException e){
            log.warning("Create team button not found");
            Helper.notFound(Constant.CREATE_TEAM_BUTTON);
        }
    }

    public static void fetchMatchStats(AppiumDriver<?> driver){
        WebElement fc =driver.findElementByAccessibilityId(FAN_CODE_STRIP_VIEW);
        List<WebElement> textsElement = fc.findElements(By.className(CLASS_TEXT_VIEW));
        List<String> texts = textsElement.stream().map(WebElement::getText).toList();
//        todo: fetch stats in order
    }



    public static int findMatch(MatchDetails nextMatch,AppiumDriver<AndroidElement> driver, Boolean isContest) {
        WebElement cricket = driver.findElementByAccessibilityId(TAG_CRICKET);
        PointOption<?> destination = PointOption.point(cricket.getLocation().moveBy(0, 500));
        for(int i = 0; i<8; i++){
            MatchDetails matchDetails;
            try{
//                todo: improve scrolling and accessibility id
                List<AndroidElement> matchesCard = driver.findElementsByAccessibilityId(MATCH_CARD);
                PointOption<?> source = null;
                for(AndroidElement matchCard: matchesCard){
                    matchDetails =MatchDetails.fromAndroidElement(matchCard);
                    if(nextMatch!=null &&nextMatch.equals(matchDetails)){
                        matchCard.click();
                        TimeUnit.SECONDS.sleep(5);
                        if(isContest){
                            driver.findElementByAccessibilityId(CONTEST_BUTTON).click();
                            return 0;
                        }else {
                            return Helper.clickOnMyTeams(driver);
                        }
                    }
                    source = PointOption.point(matchCard.getCenter());

                }
                scroll(source,destination,driver,false);
            }catch (Exception ex){
                log.warning("Exception while finding match "+ex.getMessage());
            }

//
        }
        return -1;
    }
    public static boolean isPointInRectangle(Rectangle rectangle, Point point){
        return rectangle.getY()<point.y && rectangle.getY()+rectangle.getHeight() >point.y;
    }
}
