package com.ap.fantasy.creation;

import com.ap.fantasy.creation.dream11.CreateOrEditTeam;
import com.ap.fantasy.creation.dream11.MatchDetailsPage;
import com.ap.fantasy.creation.dream11.MyTeamsPage;
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
                                CreateOrEditTeam.fetchLineup(driver, nextMatch);
                                return Collections.singletonList(nextMatch);
                            }else {
                                return Collections.emptyList();
                            }

                        }
                        MatchDetails details =getPlayersFromMatch(matchDetails);
                        if(matchDetails.getIsLineupOut()){
                            CreateOrEditTeam.fetchLineup(driver, details);
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
        int teamCount =MatchDetailsPage.clickOnMyTeams(driver);
        MyTeamsPage.clickOnCreateTeam(driver,teamCount);
        CreateOrEditTeam.fetchMatchStats(driver);

        log.info("Start fetching");
        PlayerTypeButton typeButton =CreateOrEditTeam.findPlayerTypeButtons(driver);

        log.info("Start fetching player buttons searched");
        assert typeButton.getBatWeb() != null;
        typeButton.getBatWeb().click();
        log.info("Fetching Batsmen");
        Helper.wait(1);
        List<Player> players = new ArrayList<>(CreateOrEditTeam.fetchPlayer(driver, PlayerType.BAT));

        assert typeButton.getArWeb() != null;
        typeButton.getArWeb().click();
        log.info("Fetching Al-rounders");
        Helper.wait(1);
        players.addAll(CreateOrEditTeam.fetchPlayer(driver,PlayerType.AR));

        assert typeButton.getBowlWeb() != null;
        typeButton.getBowlWeb().click();
        log.info("Fetching Bowlers");
        Helper.wait(1);
        players.addAll(CreateOrEditTeam.fetchPlayer(driver,PlayerType.BOWL));

        assert typeButton.getWkWeb() != null;
        typeButton.getWkWeb().click();
        log.info("Fetching WicketKeeper");
        Helper.wait(1);
        players.addAll(CreateOrEditTeam.fetchPlayer(driver,PlayerType.WK));

        log.info("total players parsed: "+players.size());
        matchDetails.setPlayers(players);
        return matchDetails;
    }
}
