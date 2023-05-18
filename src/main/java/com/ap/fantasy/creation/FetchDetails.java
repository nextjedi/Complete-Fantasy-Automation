package com.ap.fantasy.creation;

import com.ap.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
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

import static com.ap.fantasy.model.Constant.*;

public class FetchDetails {
    private final Logger log = Logger.getLogger(String.valueOf(FetchDetails.class));

    private AndroidDriver<AndroidElement> driver;

    public MatchDetails getEventMatch(MatchDetails matchDetails) throws MalformedURLException, InterruptedException {
        driver = CreateDriverSession.getDriver("", 0);
        Helper.wait(10);
//        TODO: loop for upcoming matches
        try {
            if(matchDetails != null) {
                matchDetails = new MatchDetails();
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
            }else{
                AndroidElement timeElement = driver.findElementByAccessibilityId(MATCH_CARD_START_TIME);
                timeElement.click();
                if(matchDetails.getPlayers() == null){
                    getPlayersFromMatch(matchDetails);
                }
                fetchLineup(driver, matchDetails);
                return matchDetails;
            }




        } catch (Exception ex) {
            log.warning(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public List<MatchDetails> fetch(MatchDetails nextMatch) throws InterruptedException, MalformedURLException {
        driver = CreateDriverSession.getDriver("",0);
        List<MatchDetails> matches = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);
        WebElement cricket = driver.findElementByAccessibilityId(TAG_CRICKET);
        PointOption<?> destination = PointOption.point(cricket.getLocation().moveBy(0, 500));
        for(int i = 0; i<8; i++){
            MatchDetails matchDetails;
            try{
                List<AndroidElement> matchesCard = driver.findElementsByAccessibilityId(MATCH_CARD);
                PointOption<?> source = null;
                for(AndroidElement matchCard: matchesCard){
                    matchDetails =MatchDetails.fromAndroidElement(matchCard);
                    if(nextMatch!=null &&nextMatch.equals(matchDetails)){
                        matchCard.click();
                        TimeUnit.SECONDS.sleep(5);
                        if(nextMatch.getPlayers() !=null){
                            if(matchDetails.getIsLineupOut()){
                                nextMatch.setIsLineupOut(matchDetails.getIsLineupOut());
                                fetchLineup(driver, nextMatch);
                                return Collections.singletonList(nextMatch);
                            }else {
                                return Collections.emptyList();
                            }

                        }
                        MatchDetails details =getPlayersFromMatch(matchDetails);
                        if(matchDetails.getIsLineupOut()){
                            fetchLineup(driver, details);
                        }
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

    public void fetchLineup(AppiumDriver<?> driver, MatchDetails match) {
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
}
