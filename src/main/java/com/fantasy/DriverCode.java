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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DriverCode {
    public static void main(String[] args) throws IOException, InterruptedException {

        MatchDetails match= new MatchDetails();
        match.setFirst(Team.IND);
        match.setSecond(Team.PAK);

        DriverCode driverCode = new DriverCode();

        Instant start = Instant.now();
//        driverCode.matchesOfTheDay(match);
        match =Helper.read(match);
        Strategy strategy =new Strategy();
        Instant end =Instant.now();
        System.out.println("read matches"+Duration.between(start,end));
        start = Instant.now();

        List<FantasyTeamTO> teams =strategy.blackBox(match);
        end =Instant.now();
        System.out.println("strategy"+Duration.between(start,end));

        int count = match.getPlayers().size();
        CreateTeam team = new CreateTeam();
        start = Instant.now();
        team.init(teams,match);
        end =Instant.now();
        System.out.println( "create teams"+Duration.between(start,end));

    }

//    todo schedule run it every day at 12 am
    public void matchesOfTheDay(MatchDetails match) throws MalformedURLException, InterruptedException {
        fetchDetails fetchDetails = new fetchDetails();
        match = fetchDetails.selectProMatch(match);
        Helper.write(match);

//        todo write matches to file / db
    }
//    todo run it as soon as the data arrives
//    todo run at the designated time
    public void createTeam(MatchDetails matchDetails) throws MalformedURLException, InterruptedException {
//        todo fetch team
        fetchDetails fetchDetails = new fetchDetails();
        List<MatchDetails> matches = fetchDetails.fetch(matchDetails);
//        todo implement strategy
//        todo verify if present
        Strategy strategy =new Strategy();
        List<FantasyTeamTO> teams =strategy.blackBox(matches.get(0));
//        todo create teams/ edit teams
        CreateTeam team = new CreateTeam();
        team.init(teams,matches.get(0));
    }



}
