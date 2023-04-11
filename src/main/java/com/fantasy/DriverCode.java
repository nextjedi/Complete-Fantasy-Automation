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
import java.util.stream.Collectors;

public class DriverCode {
    public static void main(String[] args) throws IOException, InterruptedException {

        DriverCode driverCode = new DriverCode();
        List<MatchDetails> matches = driverCode.fetchIplMatches();
        Instant matchStart = Instant.now();
        driverCode.createTeamIpl(matches.get(0), true);
//        Instant matchEnd = Instant.now();
//        System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());

    }
    public List<MatchDetails> fetchIplMatches() throws MalformedURLException, InterruptedException {
        List<MatchDetails> matches =Helper.read();
        if(matches == null){
            matches = new ArrayList<>();
        }
        matches =matches.stream().distinct().collect(Collectors.toList());
        matches.stream().sorted((o1, o2) -> o1.getTime().before(o2.getTime())?1:0);
        if(matches.get(0).getTime().before(Date.from(Instant.now()))){
            FetchDetails fetchDetails = new FetchDetails();
            MatchDetails match = fetchDetails.getEventMatch();
            matches.removeAll(matches);
//            todo: better read time of match logic
            match.setTime(Date.from(match.getTime().toInstant().plus(Duration.ofDays(1))));
            matches.add(match);
            Helper.write(matches);
            return matches;
        }
        if(!matches.isEmpty() && !matches.get(0).getPlayers().isEmpty()){
            return matches;
        }
        FetchDetails fetchDetails = new FetchDetails();
        MatchDetails match = fetchDetails.getEventMatch();
        matches.removeAll(matches);
        matches.add(match);
        Helper.write(matches);

        return matches;

    }

//    todo schedule run it every day at 12 am
//    public List<MatchDetails> matchesOfTheDay() throws MalformedURLException, InterruptedException {
//        List<MatchDetails> matches = Helper.read(LocalDate.now()+".txt");
//        FetchDetails fetchDetails = new FetchDetails();
//        MatchDetails match1 = fetchDetails.getEventMatch();
//        Helper.write(match1);
//        if(matches==null){
//            matches = fetchDetails.fetch(null);
//            matches =matches.stream().filter(matchDetails -> matchDetails.getPrizePool()>9000000).collect(Collectors.toList());
//            Helper.write(matches);
//        }
//
//        for (MatchDetails match:matches){
//            if(Helper.read(match)==null){
//                List<MatchDetails> m = fetchDetails.fetch(match);
//                Helper.write(m.get(0));
//            }
//        }
//        return matches;
//    }

//    todo run it as soon as the data arrives
//    todo run at the designated time
    public void createTeam(MatchDetails matchDetails, boolean recreateFlag) throws MalformedURLException, InterruptedException {

        matchDetails = (MatchDetails) Helper.read();
        Strategy strategy =new Strategy();
        List<FantasyTeamTO> teams =strategy.blackBox(matchDetails);
//        todo create teams/ edit teams
        CreateTeam team = new CreateTeam();
//        team.init(teams,matchDetails,recreateFlag);
        if(recreateFlag) {
            team.init(teams, matchDetails, true);
        }

    }

    public void createTeamIpl(MatchDetails matchDetails, boolean recreateFlag) throws MalformedURLException, InterruptedException {
        Strategy strategy =new Strategy();
        List<FantasyTeamTO> teams =strategy.blackBox(matchDetails);
//        todo create teams/ edit teams
        CreateTeam team = new CreateTeam();
        team.init(teams,matchDetails,recreateFlag);
//        if(recreateFlag){
//            team.init(teams.subList(0,20),matchDetails,recreateFlag);
//        }else {
//            team.init(teams.subList(20,40),matchDetails,recreateFlag);
//        }

    }

//    public void normalFlow() throws MalformedURLException, InterruptedException {
//        Instant start = Instant.now();
//        List<MatchDetails> matches = matchesOfTheDay();
//
//        for (MatchDetails match : matches
//        ) {
//            Instant matchStart = Instant.now();
//            createTeam(match, false);
//            Instant matchEnd = Instant.now();
//            System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());
//        }
//
//        for (MatchDetails match : matches
//        ) {
//            Instant matchStart = Instant.now();
//            createTeam(match, true);
//            Instant matchEnd = Instant.now();
//            System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());
//        }
//    }

}
