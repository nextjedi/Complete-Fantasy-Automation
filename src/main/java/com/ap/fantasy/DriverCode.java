package com.ap.fantasy;

import com.ap.fantasy.creation.CreateTeam;
import com.ap.fantasy.creation.FetchDetails;
import com.ap.fantasy.creation.Helper;
import com.ap.fantasy.dao.MatchRepository;
import com.ap.fantasy.generation.Strategy;
import com.ap.fantasy.model.FantasyTeamTO;
import com.ap.fantasy.model.MatchDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
@Service
public class DriverCode {
    Logger logger = Logger.getLogger(DriverCode.class.getName());
    FetchDetails fetchDetails = new FetchDetails();
    @Autowired
    private MatchRepository matchRepository;


//    todo schedule run it every day at 12 am
    public List<MatchDetails> matchesOfTheDay() throws MalformedURLException, InterruptedException {
        List<MatchDetails> matches = matchRepository.findByTimeAfter(java.sql.Date.valueOf(java.time.LocalDate.now()));

        if(matches.isEmpty()){
            matches = new ArrayList<>();
        }
        var upComingMatches =matches.stream()
                .filter(match -> match.getTime().after(Date.from(Instant.now())))
                .collect(Collectors.toList());
        if(upComingMatches.size() == 0){
            upComingMatches.addAll(readNewMatches(matches));
        }

        return upComingMatches;
    }
    public MatchDetails iplMatch(MatchDetails currentMatch){
        List<MatchDetails> matches = matchRepository.findByTimeAfter(java.sql.Date.valueOf(java.time.LocalDate.now()));
            try {
                var match = fetchDetails.getEventMatch(currentMatch);
                if(matches.contains(match)){
                    matches.remove(match);
                    matches.add(match);
                }
                matchRepository.saveAll(matches);
                return match;
            } catch (MalformedURLException | InterruptedException e) {
                logger.warning(e.getMessage());
                throw new RuntimeException(e);
            }
    }
    List<MatchDetails> readNewMatches(List<MatchDetails> oldMatches) throws MalformedURLException, InterruptedException {
        List<MatchDetails> matches =fetchDetails.fetch(null);
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

    public void iplFlow() throws MalformedURLException, InterruptedException {
        Instant start = Instant.now();
        MatchDetails match = iplMatch(null);
        if(!match.getTime().before(Date.from(Instant.now().plus(Duration.ofMinutes(30))))){
            while (match.getIsLineupOut()){
                match =iplMatch(match);
                Helper.wait(60);
            }
            createTeam(match, true);
        }else{
            createTeam(match, false);
        }

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
