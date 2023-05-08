package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.fantasy.model.Constant.*;

public class Helper {
    private Helper(){};
    public static final Logger log = Logger.getLogger("Helper.class");

    public static Boolean selectMatch(AppiumDriver driver, MatchDetails matchDetails) throws InterruptedException {
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


    public static Player parsePlayer(List<WebElement> playerDetails, PlayerType type, int playerRow){
//        todo: check at later stage
        Map<String, String> pls = playerDetails.stream().collect(Collectors.toMap(WebElement::getTagName, WebElement::getText));
        Player player = new Player(pls,type,playerRow);
        if(player.getName().isBlank() && player.getTeam() ==null){
            return null;
        }
        return player;
    }

    public static void TeamsRecreatedRead(){

    }
    public static void TeamsRecreatedWrite(){

    }


    public static void write(List<MatchDetails> matches){
        try {
            FileOutputStream f = new FileOutputStream(FILENAME);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(matches);
            o.close();
            f.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
    public static List<MatchDetails> read() {
        try {
            String match = FILENAME;
            FileInputStream fi = new FileInputStream(match);
            ObjectInputStream oi = new ObjectInputStream(fi);
            return (List<MatchDetails>) oi.readObject();

        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void scroll(PointOption source, PointOption destination, AppiumDriver driver,boolean isTap ) throws InterruptedException {
        if((source ==null || destination== null)){{
            notFound("Source or destination is null");
        }}
        TouchAction actions = new TouchAction(driver);
        actions.longPress(source).moveTo(destination).release().perform();
        wait(2);
        if(isTap){
            actions.tap(source).perform();
        }
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
                t =t.replaceAll("[^0-9.]", "");
                float n =Float.parseFloat(t);
                n*=crore;
                matchDetails.setPrizePool(n);
            }else if(t.contains("Lakh")){
                t =t.replaceAll("[^0-9.]", "");
                float n =Float.parseFloat(t);
                n*=lakh;
                matchDetails.setPrizePool(n);
            }else if((t.contains("h")||t.contains("m")) && t.length()<8){
//                todo: improve time parse
                String[] time = t.split(" ");
                if(!StringUtils.isNumeric(time[0])){
                    continue;
                }
                int num = Integer.parseInt(time[0].replaceAll("[^0-9.]", ""));
                int num1 = 0;
                if(time.length ==2)
                    num1 = Integer.parseInt(time[1].replaceAll("[^0-9.]", ""));
                int sec;
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
    public static List<Player> fetchPlayer(AppiumDriver driver, PlayerType type) throws InterruptedException {
        log.info("Start reading players");
        Set<Player> players = new HashSet<>();
        boolean flag = true;
        PointOption source = null;
        PointOption destination = null;
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

    public static void travelContest(){

    }
    public static void readTeamPlayer(){

    }

    public static boolean SelectTeamToEdit(int teamNo, AppiumDriver<AndroidElement> driver){
        int scrollOffset = 500;
        int currentTeam = 0;
        do {

            List<AndroidElement> teamCards = driver.findElementsByAccessibilityId("TeamCard");
            for (AndroidElement teamCard : teamCards) {
                try {
                    List<MobileElement> teamNames = teamCard.findElementsByClassName(CLASS_TEXT_VIEW);
                    for (MobileElement teamCandidate:teamNames){
                        String text = teamCandidate.getText();
                        if(text.startsWith("(T")){
                            currentTeam = Integer.parseInt(text.replaceAll("[^0-9.]", ""));
                            break;
                        }
                    }
                    log.info(currentTeam +" - "+teamNo);
                    if (currentTeam == teamNo) {
                        AndroidElement editButton = (AndroidElement) teamCard.findElementByAccessibilityId("my-teams-edit-team");
                        editButton.click();
                        return true;
                    } else if (teamNo - currentTeam >= 4) {
                        try {
                            scroll(PointOption.point(teamCard.getLocation()), PointOption.point(teamCard.getLocation().moveBy(0, scrollOffset)), driver,false);
                        } catch (InterruptedException ignored) {

                        } finally {
                            break;
                        }
                    }else if (currentTeam >teamNo ){
                        try {
                            scroll(PointOption.point(teamCard.getLocation()), PointOption.point(teamCard.getLocation().moveBy(0, (int) (scrollOffset*-0.5))), driver,false);
                        } catch (InterruptedException ignored) {

                        } finally {
                            break;
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
        PointOption source = null;
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
                    if(updatedCount<playerCount){
                    }else {
                        player.click();
                    }
                }
            }
            Helper.scroll(source,source.withCoordinates(0,-400),driver,false);
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

    public static void clickOnCreateTeam(AppiumDriver driver) throws InterruptedException {
        WebElement teams = driver.findElementByAccessibilityId(Constant.MY_TEAMS);
        if(teams == null){
            Helper.notFound(Constant.MY_TEAMS);
        }
        teams.click();
        TimeUnit.SECONDS.sleep(3);
        WebElement createFirstTeam = null;
        try {
            createFirstTeam= driver.findElementByAccessibilityId(Constant.CREATE_TEAM_BUTTON);
        }catch (NoSuchElementException e){
            log.warning("Create team button not found");
            try {
                createFirstTeam= driver.findElementByAccessibilityId(CREATE_TEAM_AFTER_FIRST_BUTTON);
            }catch (NoSuchElementException ex){
                Helper.notFound(Constant.CREATE_TEAM_BUTTON);
            }
        }


        createFirstTeam.click();
        Helper.wait(3);
    }

    public static void fetchMatchStats(AppiumDriver driver){
        WebElement fc =driver.findElementByAccessibilityId(FAN_CODE_STRIP_VIEW);
        List<WebElement> textsElement = fc.findElements(By.className(CLASS_TEXT_VIEW));
        List<String> texts = textsElement.stream().map(WebElement::getText).collect(Collectors.toList());
//        todo: fetch stats in order
    }



    public static int findMatch(MatchDetails nextMatch,AppiumDriver driver, Boolean isContest) throws InterruptedException {


        WebElement cricket = driver.findElementByAccessibilityId("tagCricket");
        PointOption destinationOff = PointOption.point(cricket.getLocation());
        PointOption destination = PointOption.point(cricket.getLocation().moveBy(0,500));
        Helper.scroll(destinationOff,destination,driver,false);
        for(int i = 0; i<6; i++){
            MatchDetails matchDetails = new MatchDetails();
            try{
//                todo: improve scrolling and accessibility id
                List<AndroidElement> matchesCard = driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])");
                PointOption source = null;
                for(AndroidElement matchCard: matchesCard){
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
                            return Integer.parseInt(myTeam.findElement(By.className("android.widget.TextView")).getText().replaceAll(DIGIT_ONLY_REGEX, ""));
//                            click on teams
                        }
                    }
                    source = PointOption.point(matchCard.getCenter());

                }
                scroll(source,destination,driver,false);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

//
        }
        return -1;
    }

    public static int getEventMatchToCreateTeam(MatchDetails matchDetails,AppiumDriver driver,boolean isContest) throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        try {
            WebElement timeElement = driver.findElementByAccessibilityId(MATCH_CARD_START_TIME);
            List<Team> teams = new ArrayList<>();
            teams.add(Team.valueOf(driver.findElementByAccessibilityId(TEAM1).getText()));
            teams.add(Team.valueOf(driver.findElementByAccessibilityId(TEAM2).getText()));
            matchDetails.setTeams(teams);
            String time = timeElement.getText();
            String timer = driver.findElementByAccessibilityId(TIMER).getText();
            if(matchDetails.getTeams().equals(teams)){
                timeElement.click();
                Helper.wait(5);
            }else {
                notFound("Match not found");
            }

            if(isContest){
                driver.findElementByAccessibilityId(CONTEST_BUTTON).click();
                return 0;
//                            click on contest
            }else {
                WebElement myTeam = driver.findElementByAccessibilityId(MY_TEAMS);
                int teamCount = Integer.parseInt(myTeam.findElement(By.className(CLASS_TEXT_VIEW)).getText().replaceAll(DIGIT_ONLY_REGEX, ""));
                myTeam.click();
                return teamCount;
            }


        } catch (Exception ex) {
            log.warning(ex.getMessage());
        }
        return -2;
    }

    public static boolean isPointInRectangle(Rectangle rectangle, Point point){
        return rectangle.getY()<point.y && rectangle.getY()+rectangle.getHeight() >point.y;
    }
}
