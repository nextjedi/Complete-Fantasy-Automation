package com.fantasy;

import com.fantasy.creation.CreateTeam;
import com.fantasy.creation.Helper;
import com.fantasy.creation.fetchDetails;
import com.fantasy.generation.Strategy;
import com.fantasy.model.*;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DriverCode {
    public static void main(String[] args) throws IOException, InterruptedException {

        DriverCode driverCode = new DriverCode();
        if(true){
            MatchDetails match = driverCode.fetchIplMatchOfDay();
            Instant matchStart = Instant.now();
            driverCode.createTeamIpl(match, false);
            Instant matchEnd = Instant.now();
            System.out.println(matchEnd.minusSeconds(matchStart.getEpochSecond()) + match.getTeams().get(0).toString());

        }else {

            Instant start = Instant.now();
            List<MatchDetails> matches = driverCode.matchesOfTheDay();

            for (MatchDetails match : matches
            ) {
                Instant matchs = Instant.now();
                driverCode.createTeam(match, false);
                Instant matche = Instant.now();
                System.out.println(matche.minusSeconds(matchs.getEpochSecond()) + match.getTeams().get(0).toString());
            }

            for (MatchDetails match : matches
            ) {
                Instant matchs = Instant.now();
                driverCode.createTeam(match, true);
                Instant matche = Instant.now();
                System.out.println(matche.minusSeconds(matchs.getEpochSecond()) + match.getTeams().get(0).toString());
            }
        }
    }
    public MatchDetails fetchIplMatchOfDay() throws MalformedURLException, InterruptedException {
        MatchDetails match =Helper.read(null,"match.txt");
        if(match == null){
            fetchDetails fetchDetails = new fetchDetails();
             match = fetchDetails.getEventMatch();
            Helper.write(match,"match");
        }
        return match;

    }

//    todo schedule run it every day at 12 am
    public List<MatchDetails> matchesOfTheDay() throws MalformedURLException, InterruptedException {
        List<MatchDetails> matches = Helper.read(LocalDate.now()+".txt");
        fetchDetails fetchDetails = new fetchDetails();
        MatchDetails match1 = fetchDetails.getEventMatch();
        Helper.write(match1);
        if(matches==null){
            matches = fetchDetails.fetch(null);
            matches =matches.stream().filter(matchDetails -> matchDetails.getPrizePool()>9000000).collect(Collectors.toList());
            Helper.write(matches);
        }

        for (MatchDetails match:matches){
            if(Helper.read(match)==null){
                List<MatchDetails> m = fetchDetails.fetch(match);
                Helper.write(m.get(0));
            }
        }
        return matches;
    }

//    todo run it as soon as the data arrives
//    todo run at the designated time
    public void createTeam(MatchDetails matchDetails, boolean recreateFlag) throws MalformedURLException, InterruptedException {

        matchDetails = Helper.read(matchDetails);
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



}
