package com.fantasy.generation;

import com.fantasy.model.*;
import org.paukov.combinatorics3.Generator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Strategy {
//        todo: a lot of todos after completing automation
    Team first;
    Team second;



    public List<FantasyTeamTO> blackBox(MatchDetails matchDetails){
//        match simulator based on batting and bowling order getting master list of teams
//        different strategies
        List<Player> players = matchDetails.getPlayers().stream().filter(Player::isPlaying).collect(Collectors.toList());
        players = matchDetails.getPlayers();
        players = matchDetails.getPlayers().stream().filter(player -> player.getSelectedBy()>15).collect(Collectors.toList());

        first = matchDetails.getFirst();
        second = matchDetails.getSecond();
        List<FantasyTeam> teams = cvcBased(players);




        return teams.stream().map(FantasyTeamTO::new).collect(Collectors.toList());
    }

    private List<FantasyTeam> orderBased(List<Player> players){
        List<String> indBat= Arrays.asList("L Rahul","V Kohli","R Sharma","S Yadav");
        List<String> pakBat= Arrays.asList("B Azam","M Rizwan", "S Masood","F Zaman");
        List<Player> indBats = players.stream().filter(player -> indBat.contains(player.getName())).collect(Collectors.toList());
        List<Player> pakBats = players.stream().filter(player -> pakBat.contains(player.getName())).collect(Collectors.toList());
        indBats.addAll(pakBats);
        List<List<Player>> batsMens =Generator.subset(indBats).simple().stream().filter(playerPlayings -> playerPlayings.size() <6 && playerPlayings.size()>3).toList();
        batsMens =batsMens.stream().filter(players1 -> {
            return (players1.stream().filter(player -> player.getTeam().equals(Team.IND)).count() < 4) &&
                    (players1.stream().filter(player -> player.getTeam().equals(Team.PAK)).count() < 4);
        }).collect(Collectors.toList());


        List<String> indBowl= Arrays.asList("B Kumar", "M Shami","A Patel","A Singh");
        List<String> pakBowl= Arrays.asList("S Afridi","H Rauf","M Nawaz","S Khan");
        List<Player> indBowls = players.stream().filter(player -> indBowl.contains(player.getName())).collect(Collectors.toList());
        List<Player> pakBowls = players.stream().filter(player -> pakBowl.contains(player.getName())).collect(Collectors.toList());
        indBowls.addAll(pakBowls);
        List<List<Player>> bowlers =Generator.subset(indBowls).simple().stream().filter(playerPlayings -> playerPlayings.size() <6 && playerPlayings.size()>3).toList();
        bowlers =bowlers.stream().filter(players1 -> {
            return (players1.stream().filter(player -> player.getTeam().equals(Team.IND)).count() < 4) &&
                    (players1.stream().filter(player -> player.getTeam().equals(Team.PAK)).count() < 3);
        }).collect(Collectors.toList());


        Iterator<List<Player>> bowlI = bowlers.iterator();
        for(List<Player> batsman:batsMens){
            if(bowlI.hasNext()){
                batsman.addAll(bowlI.next());
            }else {
                bowlI = bowlers.iterator();
            }
        }
//        todo: analyze
//        todo: discard one sided combos
        for(List<Player> batsman:batsMens){
            Optional<Player> p =players.stream().filter(player -> player.getName().equals("M Rizwan")).findFirst();
            Optional<Player> p2 = players.stream().filter(player -> player.getName().equals("D Karthik")).findFirst();
            if(p.isPresent()){
                if(!batsman.contains(p.get())){
                    batsman.add(p2.get());
                }
            }
        }



        List<FantasyTeam> teams = new ArrayList<>();
//        todo: fill team

        //        todo:list of cvc candidate 6c2
//        List<String> cvc = Arrays.asList("M Rizwan","B Azam","L Rahul","S Masood","S Yadav","V Kohli","H Pandya","H Rauf");
//        List<Player> cvcs = players.stream().filter(player -> cvc.contains(player.getName())).collect(Collectors.toList());
        List<Player> cvcs = players.stream().sorted((o1, o2) -> (int) (o2.getSelectedBy() - o1.getSelectedBy())).collect(Collectors.toList()).subList(0,6);
        List<List<Player>> captains = Generator.subset(cvcs).simple().stream().filter(playerPlayings -> playerPlayings.size() == 2).toList();
//        todo: captain reverse

        Iterator<List<Player>> cvcI = captains.iterator();
        for (List<Player> p:batsMens){
            int playerAdded =0;
            int addCount = 11-p.size();

            long bat = p.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BAT)).count();
            long wk = p.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.WK)).count();
            long al = p.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.AR)).count();
            long bowl = p.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BOWL)).count();
            if(bowl <3){
                int bCount =11-p.size();

                List<Player> bwlers = players.stream().filter(player -> player.getType().equals(PlayerType.BOWL)).collect(Collectors.toList());
                bwlers =bwlers.stream().filter(player -> !p.contains(player)).collect(Collectors.toList());
                p.addAll(bwlers.subList(1,1+bCount));


            }
            if(al ==0){
                Optional<Player> p2 = players.stream().filter(player -> player.getName().equals("H Pandya")).findFirst();
                if(p2.isPresent()){
                    p.add(p2.get());
                    playerAdded++;
                }
                if(p.size() !=11){
                    p2 = players.stream().filter(player -> player.getName().equals("S Khan")).findFirst();
                    if(p2.isPresent()){
                        p.add(p2.get());
                        playerAdded++;
                    }
                }
                if(p.size() !=11){
                    p2 = players.stream().filter(player -> player.getName().equals("A Patel")).findFirst();
                    if(p2.isPresent()){
                        p.add(p2.get());
                        playerAdded++;
                    }
                }
            }

            int indCount = (int) p.stream().filter(player -> player.getTeam().equals(Team.IND)).count();
            if(indCount<4){
                List<Player> temp = players.stream().filter(player -> player.getTeam().equals(Team.IND)).collect(Collectors.toList());
                temp =temp.stream().filter(player -> !p.contains(player)).collect(Collectors.toList());
                temp = temp.subList(0,4-indCount);
                p.addAll(temp);
            }
            int pakCount = (int) p.stream().filter(player -> player.getTeam().equals(Team.PAK)).count();
            if(indCount<4){
                List<Player> temp = players.stream().filter(player -> player.getTeam().equals(Team.PAK)).collect(Collectors.toList());
                temp =temp.stream().filter(player -> !p.contains(player)).collect(Collectors.toList());
                temp = temp.subList(0,4-indCount);
                p.addAll(temp);
            }
            if(p.size()<=11){
                List<Player> temp = players.stream().filter(player -> !p.contains(player)).collect(Collectors.toList());
                temp = temp.subList(0,11-p.size());
                p.addAll(temp);
            }

            if(p.size() ==11){
//                validate team add or discard
                boolean flag = false;
                while (!flag){
                    if(cvcI.hasNext()){
                        List<Player> caps = cvcI.next();
                        if(p.contains(caps.get(0)) && p.contains(caps.get(1))){
                            flag = true;
                            FantasyTeam team = new FantasyTeam(p.stream().collect(Collectors.toSet()), caps.get(0),caps.get(1));
                            if(team.isValid()){
                                teams.add(team);
                            }
                        }
                    }else {
                        cvcI = captains.iterator();
                    }

                }

            }
        }

//        playersize
//        team count
//



        return teams;
    }
    private List<FantasyTeam> cvcBased(List<Player> players){
//        todo: combo of wks
        List<Player> wks = players.stream().filter(player -> player.getType().equals(PlayerType.WK)).collect(Collectors.toList());
        List<List<Player>> wicketKeeprs =Generator.subset(wks).simple().stream().filter(playerPlayings -> playerPlayings.size() >0 &&  playerPlayings.size() <=2).toList();

//        todo: batsmen combo
//        List<String> batsmen = Arrays.asList("P Nissanka","P Stirling","D de Silva","A Balbirnie","G Dockrell","B Rajapaksa");
//        List<Player> batsmenPlayer = players.stream().filter(player -> batsmen.contains(player.getName())).collect(Collectors.toList());
        List<Player> batsmenPlayer = players.stream().filter(player -> player.getType().equals(PlayerType.BAT)).collect(Collectors.toList());
        List<List<Player>> batsMens =Generator.subset(batsmenPlayer).simple().stream().filter(playerPlayings -> playerPlayings.size() ==4).toList();


//        todo: combo of ars
        List<Player> ars = players.stream().filter(player -> player.getType().equals(PlayerType.AR)).collect(Collectors.toList());
        List<List<Player>> alrounder =Generator.subset(ars).simple().stream().filter(playerPlayings -> playerPlayings.size() <=2).toList();

        Iterator<List<Player>> wkI = wicketKeeprs.iterator();
        Iterator<List<Player>> btI = batsMens.iterator();
        Iterator<List<Player>> arI = alrounder.iterator();
        List<List<Player>> teamPlayers = new ArrayList<>();

        for(int i=0;i<60;i++){
            if(!wkI.hasNext()){
                wkI = wicketKeeprs.iterator();
            }
            if(!btI.hasNext()){
                btI = batsMens.iterator();
            }
            if(!arI.hasNext()){
                arI =alrounder.iterator();
            }
            List<Player> p = new ArrayList<>();
            p.addAll(wkI.next());
            p.addAll(btI.next());
            p.addAll(arI.next());
            teamPlayers.add(p);

        }




//        todo:list of cvc candidate 6c2
//        List<String> cvc = Arrays.asList("G Philips","D Conway","D Warner","G Maxwell");
//        List<Player> cvcs = players.stream().filter(player -> cvc.contains(player.getName())).collect(Collectors.toList());
        List<Player> cvcs = players.stream().sorted((o1, o2) -> (int) (o2.getSelectedBy() - o1.getSelectedBy())).collect(Collectors.toList()).subList(0,6);
        List<List<Player>> captains = Generator.subset(cvcs).simple().stream().filter(playerPlayings -> playerPlayings.size() == 2).toList();
//        todo: captain reverse
        List<FantasyTeam> teams = new ArrayList<>();

        Iterator<List<Player>> cvcI = captains.iterator();
        for(List<Player> p :teamPlayers){
            if(!cvcI.hasNext()){
                cvcI = captains.iterator();
            }
            List<Player> c = cvcI.next();
            while(!p.contains(c.get(0)) && p.contains(c.get(1))){
                if(!cvcI.hasNext()){
                    cvcI = captains.iterator();
                }
                c=cvcI.next();
            }
            FantasyTeam team =new FantasyTeam(p.stream().collect(Collectors.toSet()),p.get(0),p.get(1));
            teams.add(team);
        }

//        todo: bowlers 3 or 4
//        List<String> batsmenbowlers = Arrays.asList("M Theekshana","B Fernando","L Kumara","J Little","M Adair");
        List<Player> bowls = players.stream().filter(player -> player.getType().equals(PlayerType.BOWL)).collect(Collectors.toList());
        List<List<Player>> bowlers3 =Generator.subset(bowls).simple().stream().filter(playerPlayings -> playerPlayings.size() ==3).toList();
        List<List<Player>> bowlers4 =Generator.subset(bowls).simple().stream().filter(playerPlayings -> playerPlayings.size() ==4).toList();

        Iterator<List<Player>> b3I = bowlers3.iterator();
        Iterator<List<Player>> b4I = bowlers4.iterator();
        List<FantasyTeam> teamf = new ArrayList<>();
        for(FantasyTeam team:teams){
            if(team.getPlayers().size()<7){
                continue;
            }
            boolean flag = true;
            do {
                Set<Player> pls = team.getPlayers();
                if (pls.size() == 7) {
                    if (!b4I.hasNext()) {
                        b4I = bowlers4.iterator();
                    }
                    FantasyTeam temp = new FantasyTeam(Stream.concat(pls.stream(),b4I.next().stream()).collect(Collectors.toSet()), team.getCaptain(), team.getvCaptain());
                    if (temp.isValid()) {
                        flag = false;
                        teamf.add(temp);
                    }

                } else {
                    if (!b3I.hasNext()) {
                        b3I = bowlers3.iterator();
                    }
                    FantasyTeam temp = new FantasyTeam(Stream.concat(pls.stream(),b3I.next().stream()).collect(Collectors.toSet()), team.getCaptain(), team.getvCaptain());
                    if (temp.isValid()) {
                        flag = false;
                        teamf.add(temp);
                    }
                }
            }while(flag);
        }
        return teamf;
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
}