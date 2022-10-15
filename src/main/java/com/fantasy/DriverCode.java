package com.fantasy;

import com.fantasy.creation.fetchDetails;
import com.fantasy.generation.Strategy;
import com.fantasy.model.FantasyTeam;
import com.fantasy.model.FantasyTeamTO;
import com.fantasy.model.MatchDetails;
import com.fantasy.model.Player;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

public class DriverCode {
    public static void main(String[] args) throws IOException, InterruptedException {

        fetchDetails fetchDetails = new fetchDetails();
        MatchDetails matchDetails = fetchDetails.fetch();

//        TODO: generate teams
        Strategy strategy = new Strategy();
        List<FantasyTeamTO> teams = strategy.blackBox(matchDetails);

//        TODO: create teams
//        todo: include time elements
//        todo: option to store matchDetails in db
//        todo: trigger create team automatically at the match time


    }
}
