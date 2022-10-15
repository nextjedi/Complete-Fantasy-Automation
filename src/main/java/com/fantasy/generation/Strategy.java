package com.fantasy.generation;

import com.fantasy.model.*;
import org.paukov.combinatorics3.Generator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Strategy {
//        todo: a lot of todos after completing automation
    private final static String COMMA_DELIMITER = ",";
    Team first = Team.MI;
    Team second = Team.RR;



    public List<FantasyTeamTO> blackBox(MatchDetails matchDetails){
//        match simulator based on batting and bowling order getting master list of teams
//        different strategies
        List<Player> players = matchDetails.getPlayers().stream().filter(player -> player.isPlaying()).collect(Collectors.toList());

        first = matchDetails.getFirst();
        second = matchDetails.getSecond();
        List<FantasyTeam> teams = cvcBased(players);



        return teams.stream().map(fantasyTeam -> new FantasyTeamTO(fantasyTeam)).collect(Collectors.toList());
    }

    private List<FantasyTeam> cvcBased(List<Player> players){
//        todo: combo of wks
        List<Player> wks = players.stream().filter(player -> player.getType().equals(PlayerType.WK)).collect(Collectors.toList());
        List<List<Player>> wicketKeeprs =Generator.subset(wks).simple().stream().filter(playerPlayings -> playerPlayings.size() <=2).toList();

//        todo: combo of ars
        List<Player> ars = players.stream().filter(player -> player.getType().equals(PlayerType.AR)).collect(Collectors.toList());
        List<List<Player>> allRounders =Generator.subset(ars).simple().stream().filter(playerPlayings -> playerPlayings.size() <=3).toList();
//        12 combo
//        todo:list of cvc candidate 6c2
        List<String> cvc = Arrays.asList("S Verma","C Atapattu","D Sharma","S Rana","O Ranasinghe","I Ranaweera","H Kaur");
        List<Player> cvcs = players.stream().filter(player -> cvc.contains(player.getName())).collect(Collectors.toList());
        List<List<Player>> captains = Generator.subset(cvcs).simple().stream().filter(playerPlayings -> playerPlayings.size() == 2).toList();
        List<FantasyTeam> teams = new ArrayList<>();


        Iterator<List<Player>> wkI = wicketKeeprs.iterator();
        Iterator<List<Player>> arI = allRounders.iterator();

        for(List<Player> c : captains){
            Set<Player> pls = c.stream().collect(Collectors.toSet());
            if(!wkI.hasNext()){
                wkI = wicketKeeprs.iterator();
            }
            if(!arI.hasNext()){
                arI = allRounders.iterator();
            }
            pls.addAll(wkI.next());
            pls.addAll(arI.next());
            FantasyTeam team = new FantasyTeam(pls,c.get(0),c.get(1));
            teams.add(team);
        }
        List<Player> bats = players.stream().filter(player -> player.getType().equals(PlayerType.BAT) && player.getSelectedBy() >35).collect(Collectors.toList());

        List<Player> bowls = players.stream().filter(player -> player.getType().equals(PlayerType.BOWL) && player.getSelectedBy() >35).collect(Collectors.toList());
        List<List<Player>> baaters = Generator.subset(bats).simple().stream().filter(playerPlayings -> playerPlayings.size() == 4).toList();
        List<List<Player>> bowlers = Generator.subset(bowls).simple().stream().filter(playerPlayings -> playerPlayings.size() == 4).toList();
        Iterator<List<Player>> batI = baaters.iterator();
        Iterator<List<Player>> bowlI = bowlers.iterator();
        List<FantasyTeam> fteams = new ArrayList<>();
        for(FantasyTeam team:teams){
            if(!batI.hasNext()){
                batI = baaters.iterator();
            }if(!bowlI.hasNext()){
                bowlI = bowlers.iterator();
            }
            List<Player> batTemp = batI.next();
            List<Player> bowlTemp = bowlI.next();

            Set<Player> tplayer = team.getPlayers();
            tplayer.addAll(batTemp);
            tplayer.addAll(bowlTemp);


            int batCount = (int) tplayer.stream().filter(player -> player.getType().equals(PlayerType.BAT)).count();
            if(batCount >4){
                float mini= 1021;
                for(Player p:tplayer.stream().filter(player -> player.getType().equals(PlayerType.BAT)).collect(Collectors.toList())){
                    if(mini >p.getSelectedBy()){
                        mini = p.getSelectedBy();
                    }
                }
                float finalMini = mini;
                tplayer.removeIf(player -> player.getSelectedBy() == finalMini);

            }
            int bowlCount =(int) tplayer.stream().filter(player -> player.getType().equals(PlayerType.BOWL)).count();
            if(bowlCount>4){
                float mini= 1021;
                for(Player p:tplayer.stream().filter(player -> player.getType().equals(PlayerType.BOWL)).collect(Collectors.toList())){
                    if(mini >p.getSelectedBy()){
                        mini = p.getSelectedBy();
                    }
                }
                float finalMini = mini;
                tplayer.removeIf(player -> player.getSelectedBy() == finalMini);
            }
            int indCount = (int) tplayer.stream().filter(player -> player.getTeam().equals(Team.INW)).count();
            if(tplayer.size()>11){
                while (tplayer.size() !=11){
                    List<Player> playrRemove = tplayer.stream().filter(player -> player.getType().equals(PlayerType.BAT) || player.getType().equals(PlayerType.BOWL)).collect(Collectors.toList());
                    float mini= 1021;
                    for(Player p:playrRemove){
                        if(mini >p.getSelectedBy()){
                            mini = p.getSelectedBy();
                        }
                    }
                    float finalMini = mini;
                    tplayer.removeIf(player -> player.getSelectedBy() == finalMini);
                }
            }


            team.setPlayers(tplayer);
            if (team.isValid()){
                FantasyTeam te = new FantasyTeam(tplayer, team.getCaptain(), team.getvCaptain());
                fteams.add(te);
            }

            
        }



//        todo 6+5
//        todo 7+4


        return fteams;
    }

    private List<FantasyTeam> matchSimulator(){
//
        return null;
    }
    /*below are different team making strategies*/
    private List<FantasyTeam> op5(List<Player> players){
//        get openers
//        get key bowlers
//        get run scorer of first inning candidate
//        get wicket keeper and fielders
        

        List<FantasyTeam> fantasyTeams =new ArrayList<>();
//        get opener from second inning
        List<Player> openers =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(second) && (playerPlaying.getBattingOrder() < 3)).collect(Collectors.toList());
//        get key bowlers from second batting
        List<Player> keyBowlers =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(second) && (playerPlaying.isKeyBowler())).collect(Collectors.toList());
//        get key run scoring candidate
        List<Player> runCandidate =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(first) && (playerPlaying.isRunCandidate())).collect(Collectors.toList());
        List<List<Player>> bowlers = Generator.subset(keyBowlers).simple().stream().filter(playerPlayings -> playerPlayings.size() >= 5).toList();
        List<List<Player>> runners = Generator.subset(runCandidate).simple().stream().filter(playerPlayings -> playerPlayings.size() >= 4).toList();

        bowlers.forEach(bowlerList -> runners.forEach(scorer -> {
            Set<Player> teamPlayers;
            teamPlayers = Stream.of(bowlerList,scorer,openers).flatMap(Collection::stream).collect(Collectors.toSet());
            FantasyTeam fantasyTeam = new FantasyTeam( teamPlayers,openers.get(0),openers.get(1));
            if(fantasyTeam.isValid()){
                fantasyTeams.add(fantasyTeam);
            }
        }));
        Iterator<FantasyTeam> it = fantasyTeams.iterator();
        boolean flag = true;
        while (it.hasNext()){
            FantasyTeam team = it.next();
            if (flag){
                Player cap = team.getCaptain();
                team.setCaptain(team.getvCaptain());
                team.setvCaptain(cap);
            }
            flag = !flag;
        }


        return fantasyTeams;
    }

    private List<FantasyTeam> sunnyRandom(List<Player> players){

        List<Player> rr =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(second)).collect(Collectors.toList());
        List<Player> mi =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(first)).collect(Collectors.toList());
        List<FantasyTeam> teams = new ArrayList<>();
        while (teams.size() < 20)
        {
            List<Player> temp = new ArrayList<>();
            for(int i=0;i<11;i++)
            {
                Player p1 = rr.get(i);
                Player p2 = mi.get(i);
                int r = ((int) (Math.random()*100) % 2);
                if(r == 0)
                    temp.add(p1);
                else
                    temp.add(p2);
            }
            int c =  ((int) (Math.random()*100) % 6);
            int vc =  ((int) (Math.random()*100) % 6);
            while(c==vc)
                vc =  ((int) (Math.random()*100) % 6);
            FantasyTeam fantasyTeam2 = new FantasyTeam(temp.stream().collect(Collectors.toSet()), temp.get(c),temp.get(vc));
            if(fantasyTeam2.isValid()){
                teams.add(fantasyTeam2);
            }

        }
        return teams;
    }

    private List<FantasyTeam> top3plus4(List<Player> players){
//        top 3 openers from first team and combination of 2 or 3 players
//        4 out of 5 bowlers from 1st team
//        0 or 1 bowler from second team
//        4 or 5 runners from second team

        List<Player> openers =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(second) && (playerPlaying.getBattingOrder() <= 3)).collect(Collectors.toList());
//        get key bowlers from second batting
        List<Player> keyBowlers =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(second) && (playerPlaying.isKeyBowler())).collect(Collectors.toList());
//        get key run scoring candidate
        List<Player> runCandidate =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(first) && (playerPlaying.isRunCandidate())).collect(Collectors.toList());
//        potential bowler from first innings
        List<Player> firstBowler =players.stream().filter(playerPlaying -> playerPlaying.getTeam().equals(first) && (playerPlaying.isKeyBowler())).collect(Collectors.toList());

        List<List<Player>> opening = Generator.subset(openers).simple().stream().filter(playerPlayings -> playerPlayings.size()>1).toList();
        List<List<Player>> bowlers = Generator.subset(keyBowlers).simple().stream().filter(playerPlayings -> playerPlayings.size() >= 4).toList();
        List<List<Player>> runners = Generator.subset(runCandidate).simple().stream().filter(playerPlayings -> playerPlayings.size() <6 && playerPlayings.size()>3).toList();
        List<List<Player>> firstBowlers = Generator.subset(firstBowler).simple().stream().filter(playerPlayings -> playerPlayings.size()<2).toList();
//
        return null;
    }

    private List<FantasyTeam> strategy(){
//        op5
//        false 1
//        false 2
//        top 3 + 4
//        o drop 2+3 1,3 overs
//        o drop plus false 1
        return null;
    }
}