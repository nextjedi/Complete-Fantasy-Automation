package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.fantasy.model.Constant.*;

public class FetchDetails {
    private final Logger log = Logger.getLogger(String.valueOf(FetchDetails.class));

    private AndroidDriver<AndroidElement> driver;

    public MatchDetails getEventMatch() throws MalformedURLException, InterruptedException {
        driver = CreateDriverSession.getDriver("", 0);
        Helper.wait(10);
        MatchDetails matchDetails = new MatchDetails();
//        TODO: loop for upcoming matches
        try {
            AndroidElement timeElement = driver.findElementByAccessibilityId(MATCH_CARD_START_TIME);
            List<Team> teams = new ArrayList<>();
            teams.add(Team.valueOf(driver.findElementByAccessibilityId(TEAM1).getText()));
            teams.add(Team.valueOf(driver.findElementByAccessibilityId(TEAM2).getText()));
            matchDetails.setTeams(teams);
            String time = timeElement.getText();
            LocalTime timeOfMatch = LocalTime.parse(time.toLowerCase(), DateTimeFormatter.ofPattern(TIME_FORMAT_MATCH, Locale.getDefault()));
            Instant instant = timeOfMatch.atDate(LocalDate.now()).
                    atZone(ZoneId.systemDefault()).toInstant();
            matchDetails.setTime(Date.from(instant));


            timeElement.click();
            Helper.wait(3);

            return getPlayersFromMatch(matchDetails);


        } catch (Exception ex) {
            log.warning(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public List<MatchDetails> fetch(MatchDetails nextMatch) throws InterruptedException, MalformedURLException {
        driver = CreateDriverSession.getDriver("",0);
        List<MatchDetails> matches = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);
        WebElement cricket = driver.findElementByAccessibilityId("tagCricket");
        PointOption<?> destinationOff = PointOption.point(cricket.getLocation());
        PointOption<?> destination = PointOption.point(cricket.getLocation().moveBy(0, 500));
        Helper.scroll(destinationOff, destination, driver,false);
        for(int i = 0; i<6; i++){
            MatchDetails matchDetails = new MatchDetails();
            try{
                List<AndroidElement> matchesCard = driver.findElementsByXPath("(//android.view.ViewGroup[@content-desc='match-card'])");
                PointOption<?> source = null;
                for(AndroidElement matchCard: matchesCard){
                    List<MobileElement> texts = matchCard.findElementsByClassName("android.widget.TextView");
                    matchDetails =Helper.parseMatchDetails(texts.stream().map(WebElement::getText).collect(Collectors.toList()));
                    if(nextMatch!=null &&nextMatch.equals(matchDetails)){
                        matchCard.click();
                        TimeUnit.SECONDS.sleep(5);
                        MatchDetails details =getPlayersFromMatch(matchDetails);
                        return Collections.singletonList(details);
                    }else{
                        if(matchDetails !=null)
                            matches.add(matchDetails);
                    }
                    source = PointOption.point(matchCard.getCenter());

                }
                Helper.scroll(source,destination,driver,false);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        return matches;
    }

    private MatchDetails getPlayersFromMatch(MatchDetails matchDetails) throws InterruptedException {
        Helper.clickOnCreateTeam(driver);
        log.info("Start fetching");
//        todo: to be used in future
        Helper.fetchMatchStats(driver);
        PlayerTypeButton typeButton =Helper.findPlayerTypeButtons(driver);

        log.info("Start fetching player buttons searched");
        assert typeButton.getBatWeb() != null;
        typeButton.getBatWeb().click();
        log.info("Fetching Batsmen");
        Helper.wait(1);
        List<Player> players = new ArrayList<>(Helper.fetchPlayer(driver, PlayerType.BAT));

        assert typeButton.getArWeb() != null;
        typeButton.getArWeb().click();
        log.info("Fetching Al-rounders");
        Helper.wait(1);
        players.addAll(Helper.fetchPlayer(driver,PlayerType.AR));

        assert typeButton.getBowlWeb() != null;
        typeButton.getBowlWeb().click();
        log.info("Fetching Bowlers");
        Helper.wait(1);
        players.addAll(Helper.fetchPlayer(driver,PlayerType.BOWL));

        assert typeButton.getWkWeb() != null;
        typeButton.getWkWeb().click();
        log.info("Fetching WicketKeeper");
        Helper.wait(1);
        players.addAll(Helper.fetchPlayer(driver,PlayerType.WK));

        log.info("total players parsed: "+players.size());
        matchDetails.setPlayers(players);
        return matchDetails;
    }
}
