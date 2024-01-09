package com.ap.fantasy;

import com.ap.fantasy.creation.CreateDriverSession;
import com.ap.fantasy.creation.CreateTeam;
import com.ap.fantasy.creation.FetchDetails;
import com.ap.fantasy.creation.Helper;
import com.ap.fantasy.creation.dream11.FirstPage;
import com.ap.fantasy.dao.MatchRepository;
import com.ap.fantasy.generation.Strategy;
import com.ap.fantasy.model.FantasyTeamTO;
import com.ap.fantasy.model.MatchDetails;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
public class DriverCode {
    FetchDetails fetchDetails = new FetchDetails();
    private AndroidDriver<AndroidElement> driver;
    @Autowired
    private MatchRepository matchRepository;


//    todo schedule run it every day at 12 am
    public List<MatchDetails> matchesOfTheDay() throws MalformedURLException, InterruptedException {
        log.info("fetching the match of the day");
        List<MatchDetails> matches = matchRepository.findByTimeAfter(java.sql.Date.valueOf(java.time.LocalDate.now()));

        if(matches.isEmpty()){
            matches = new ArrayList<>();
        }
        var upComingMatches =matches.stream()
                .filter(match -> match.getTime().after(Date.from(Instant.now())))
                .collect(Collectors.toList());
        if(upComingMatches.isEmpty()){
            upComingMatches.addAll(readNewMatches(matches));
        }

        return upComingMatches;
    }

    List<MatchDetails> readNewMatches(List<MatchDetails> oldMatches) throws MalformedURLException, InterruptedException {
        driver = CreateDriverSession.getDriver("",0);
        List<MatchDetails> matches = FirstPage.readMatches(driver);
        matches =matches.stream().filter(matchDetails -> matchDetails.getTournamentName().equals("TATA IPL")).collect(Collectors.toSet()).stream().toList();
        for (MatchDetails match:matches){
            if(match.getPlayers() == null || match.getPlayers().isEmpty()){
                List<MatchDetails> m = fetchDetails.fetch(match);
                match.setPlayers(m.get(0).getPlayers());
            }
        }
        oldMatches.addAll(matches);
        matchRepository.saveAll(oldMatches);
        return matches;
    }

//    todo run it as soon as the data arrives
//    todo run at the designated time
    public void createTeam(MatchDetails matchDetails, boolean recreateFlag) throws MalformedURLException {

        Strategy strategy =new Strategy();
        List<FantasyTeamTO> teams =strategy.blackBox(matchDetails);
//        todo create teams/ edit teams
        CreateTeam team = new CreateTeam();
        team.init(teams,matchDetails,recreateFlag);

    }
    public void normalFlow() throws MalformedURLException, InterruptedException {
        Instant start = Instant.now();
        List<MatchDetails> matches = matchesOfTheDay();

        for (MatchDetails match : matches
        ) {
            Instant matchStart = Instant.now();
            createTeam(match, false);
            Instant matchEnd = Instant.now();
            System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());
        }

        for (MatchDetails match : matches
        ) {
            Instant matchStart = Instant.now();
            createTeam(match, true);
            Instant matchEnd = Instant.now();
            System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());
        }
    }
    public void pastMatch() throws MalformedURLException, InterruptedException {
        Instant start = Instant.now();
        List<MatchDetails> matches = matchesOfTheDay();

        for (MatchDetails match : matches
        ) {
            Instant matchStart = Instant.now();
            createTeam(match, false);
            Instant matchEnd = Instant.now();
            System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());
        }

        for (MatchDetails match : matches
        ) {
            Instant matchStart = Instant.now();
            createTeam(match, true);
            Instant matchEnd = Instant.now();
            System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());
        }
    }

    public void finalTeam(long matchId) throws MalformedURLException, InterruptedException {
        MatchDetails match = matchRepository.findById(matchId).get();
        while (!match.getIsLineupOut()){
            if(match.getTime().before(Date.from(Instant.now()))){
                return;
            }
            // todo: fetch lineup when team
//            todo:save updated team to db

            Helper.wait(60*2);
        }
        createTeam(match, true);

    }

}
