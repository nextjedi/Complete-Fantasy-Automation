package com.fantasy;

import com.fantasy.creation.CreateTeam;
import com.fantasy.creation.Helper;
import com.fantasy.creation.FetchDetails;
import com.fantasy.generation.Strategy;
import com.fantasy.model.*;

import java.io.*;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DriverCode {
    Logger logger = Logger.getLogger(DriverCode.class.getName());
    public static void main(String[] args) throws IOException, InterruptedException {

        DriverCode driverCode = new DriverCode();
        driverCode.normalFlow();

    }

//    todo schedule run it every day at 12 am
    public List<MatchDetails> matchesOfTheDay() throws MalformedURLException, InterruptedException {
        List<MatchDetails> matches = Helper.read();

        if(matches.isEmpty()){
            matches = new ArrayList<>();
        }
        var upComingMatches =matches.stream().filter(match -> match.getTime().after(Date.from(Instant.now()))).collect(Collectors.toList());
        if(upComingMatches.size() == 0){
            upComingMatches.addAll(readNewMatches(matches));
        }
        return upComingMatches;
    }
    List<MatchDetails> readNewMatches(List<MatchDetails> oldMatches) throws MalformedURLException, InterruptedException {
        FetchDetails fetchDetails = new FetchDetails();
        List<MatchDetails> matches =fetchDetails.fetch(null);
        matches =matches.stream().filter(matchDetails -> matchDetails.getTournamentName().equals("TATA IPL")).collect(Collectors.toSet()).stream().toList();
        for (MatchDetails match:matches){
            if(match.getPlayers() == null || match.getPlayers().isEmpty()){
                List<MatchDetails> m = fetchDetails.fetch(match);
                match.setPlayers(m.get(0).getPlayers());
            }
        }
        oldMatches.addAll(matches);
        Helper.write(oldMatches);
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

}
