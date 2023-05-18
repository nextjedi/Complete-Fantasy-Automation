package com.ap.fantasy.generation;

import com.ap.fantasy.model.*;
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
//        players = matchDetails.getPlayers().stream().filter(player -> player.getSelectedBy()>15).collect(Collectors.toList());

        first = matchDetails.getTeams().get(0);
        second = matchDetails.getTeams().get(1);
        List<FantasyTeam> teams = garbageStrategy(players);




        return teams.stream().map(FantasyTeamTO::new).collect(Collectors.toList());
    }

//    first 4 batsmen
//    key 5 / 6 bowler with potential to take wicket
//    players to consider from other team *
    private List<FantasyTeam> wick2Wick7(List<Player> players){
//        todo define winning team or both
//        ind
//        todo chose top 4 batsmen and define 6 bowlers
        List<String> batsmen = Arrays.asList("L Rahul","R Sharma","V Kohli","S Yadav");
        List<String> bowlers = Arrays.asList("B Kumar","A Singh","M Shami","A Patel","H Pandya");
        List<Player> batsmenPlayer = players.stream().filter(player -> batsmen.contains(player.getName())).collect(Collectors.toList());
        List<Player> bowlerPlayer = players.stream().filter(player -> bowlers.contains(player.getName())).collect(Collectors.toList());
//        todo 2+3+4 combos of batsmen
        List<List<Player>> batsMenCombo =Generator.subset(batsmenPlayer).simple().stream().filter(playerPlayings -> playerPlayings.size() >=2 && playerPlayings.size() <4).toList();
//        todo remaining bowlers to complete 7
        List<List<Player>> bowlerCombo4 =Generator.subset(batsmenPlayer).simple().stream().filter(playerPlayings -> playerPlayings.size() ==4).toList();
        List<List<Player>> bowlerCombo5 =Generator.subset(batsmenPlayer).simple().stream().filter(playerPlayings -> playerPlayings.size() ==5).toList();
        List<FantasyTeam> teams= new ArrayList<>();
//        bowl iterator
        Iterator<List<Player>> bowl4It = bowlerCombo4.iterator();
        Iterator<List<Player>> bowl5It = bowlerCombo5.iterator();
        for(List<Player> bats:batsMenCombo){
            while (!bowl5It.hasNext()){
                bowl5It = bowlerCombo5.iterator();
            }
            while (!bowl4It.hasNext()){
                bowl4It = bowlerCombo4.iterator();
            }
            Set<Player> teamPlayers = new HashSet<>(bats);
            List<Player> bowls = null;
            if(bats.size() ==2){
                bowls = bowl5It.next();
            }else{
                bowls = bowl4It.next();
            }
            teamPlayers.addAll(bowls);
//            todo think c vccmd
            FantasyTeam team = new FantasyTeam(teamPlayers, bats.get(0), bowls.get(0));
            teams.add(team);

        }
//        todo wicket keeper
//        todo think harder
        List<Player> wks = players.stream().filter(player -> player.getType().equals(PlayerType.WK)).collect(Collectors.toList()).stream().filter(player -> player.getSelectedBy()>50).collect(Collectors.toList());
        for(FantasyTeam team:teams){
            team.getPlayers().addAll(wks);
        }
//        todo 4 players from other team to be completed later
//        top 6 player
        List<Player> other = players.stream().filter(player -> player.getSelectedBy()>30).collect(Collectors.toList());
        List<List<Player>> otherCombo =Generator.subset(batsmenPlayer).simple().stream().filter(playerPlayings -> playerPlayings.size() ==4).toList();


        return null;
    }

    private List<FantasyTeam> orderBased(List<Player> players){
        List<String> indBat= Arrays.asList("L Rahul","V Kohli","R Sharma","S Yadav");
        List<String> pakBat= Arrays.asList("B Azam","M Rizwan", "S Masood","F Zaman");
        List<Player> indBats = players.stream().filter(player -> indBat.contains(player.getName())).collect(Collectors.toList());
        List<Player> pakBats = players.stream().filter(player -> pakBat.contains(player.getName())).collect(Collectors.toList());
        indBats.addAll(pakBats);
        List<List<Player>> batsMens =Generator.subset(indBats).simple().stream().filter(playerPlayings -> playerPlayings.size() <6 && playerPlayings.size()>3).toList();
        batsMens =batsMens.stream().filter(players1 -> (players1.stream().filter(player -> player.getTeam().equals(Team.IND)).count() < 4) &&
                (players1.stream().filter(player -> player.getTeam().equals(Team.PAK)).count() < 4)).collect(Collectors.toList());


        List<String> indBowl= Arrays.asList("B Kumar", "M Shami","A Patel","A Singh");
        List<String> pakBowl= Arrays.asList("S Afridi","H Rauf","M Nawaz","S Khan");
        List<Player> indBowls = players.stream().filter(player -> indBowl.contains(player.getName())).collect(Collectors.toList());
        List<Player> pakBowls = players.stream().filter(player -> pakBowl.contains(player.getName())).collect(Collectors.toList());
        indBowls.addAll(pakBowls);
        List<List<Player>> bowlers =Generator.subset(indBowls).simple().stream().filter(playerPlayings -> playerPlayings.size() <6 && playerPlayings.size()>3).toList();
        bowlers =bowlers.stream().filter(players1 -> (players1.stream().filter(player -> player.getTeam().equals(Team.IND)).count() < 4) &&
                (players1.stream().filter(player -> player.getTeam().equals(Team.PAK)).count() < 3)).collect(Collectors.toList());


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
        List<String> wk = Arrays.asList("D Conway", "M Dhoni", "M Wade");
        List<Player> wks = players.stream().filter(player -> player.getType().equals(PlayerType.WK))
                .filter(player -> {
            System.out.println(wk.contains(player.getName()));
            return wk.contains(player.getName());
        }).collect(Collectors.toList());
//        List<Player> wks = players.stream().filter(player -> player.getType().equals(PlayerType.WK) && player.getSelectedBy()>50).collect(Collectors.toList());
        List<List<Player>> wicketKeepers =Generator.subset(wks).simple().stream().filter(playerPlayings -> playerPlayings.size() >0 &&  playerPlayings.size() <=2).toList();

//        todo: batsmen combo
//        List<String> batsmen = Arrays.asList("S Gill","R Gaikwad","D Miller","K Williamson","A Rayudu","A Rahane","S Dube");
//        List<Player> batsmenPlayer = players.stream().filter(player -> batsmen.contains(player.getName())).collect(Collectors.toList());
        List<Player> batsmenPlayer = players.stream().filter(player -> player.getType().equals(PlayerType.BAT)).collect(Collectors.toList());
        List<List<Player>> batsMens =Generator.subset(batsmenPlayer).simple().stream().filter(playerPlayings -> playerPlayings.size() ==4 || playerPlayings.size() ==3 || playerPlayings.size() ==5).toList();


//        todo: combo of ars
//        List<String> ar = Arrays.asList("R Jadeja","B Stokes","H Pandya","M Ali","R Tewatia","D pretorius","M Santner");
//        List<Player> ars = players.stream().filter(player -> ar.contains(player.getName())).collect(Collectors.toList());
        List<Player> ars = players.stream().filter(player -> player.getType().equals(PlayerType.AR) && player.getSelectedBy() >25).collect(Collectors.toList());
        List<List<Player>> alrounder =Generator.subset(ars).simple().stream().filter(playerPlayings -> playerPlayings.size() <=3 && playerPlayings.size()>0).toList();

//        List<String> bowler = Arrays.asList("Rashid-Khan","D Chahar","Mshami","M Theekshana","A Joseph","Y Dayal","S Mavi");
//        List<Player> bowlerPlayer = players.stream().filter(player -> bowler.contains(player.getName())).collect(Collectors.toList());
        List<Player> bowlerPlayer = players.stream().filter(player -> player.getType().equals(PlayerType.BOWL)).collect(Collectors.toList());
        List<List<Player>> bowlers =Generator.subset(bowlerPlayer).simple().stream().filter(playerPlayings -> playerPlayings.size() ==4 || playerPlayings.size() ==3 || playerPlayings.size() ==5).toList();

        List<List<Player>> teamPlayers = new ArrayList<>();
        batsMens.forEach(bat -> bowlers.forEach(bowl -> alrounder.forEach(al->{
            wicketKeepers.forEach(w->{
                List<Player> team = new ArrayList<>();
                team.addAll(bat);
                team.addAll(bowl);
                team.addAll(al);
                team.addAll(w);
                if(team.size() ==11){
                    teamPlayers.add(team);
                }
            });

        })));

//        todo:list of cvc candidate 6c2
        List<String> cvc = Arrays.asList("S Gill","H Pandya","Rashid-Khan","D Conway","R Gaikwad","M Ali","R Jadeja");
        List<Player> cvcs = players.stream().filter(player -> cvc.contains(player.getName())).collect(Collectors.toList());
//        List<Player> cvcs = players.stream().sorted((o1, o2) -> (int) (o2.getSelectedBy() - o1.getSelectedBy())).collect(Collectors.toList()).subList(0,10);
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
            if(team.isValid())
                teams.add(team);
        }
        Collections.shuffle(teams);
        return teams;
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
        List<List<Player>> pls = Generator.subset(players).simple().stream().filter(playerPlayings -> playerPlayings.size() == 11).toList();
        Set<FantasyTeam> teams = new HashSet<>();
        for(List<Player> p:pls){
            FantasyTeam fantasyTeam2 = new FantasyTeam(p.stream().collect(Collectors.toSet()), p.get(0),p.get(1));
            if(fantasyTeam2.isValid()){
                teams.add(fantasyTeam2);
                if(teams.size() == 80){
                    List<FantasyTeam> t = teams.stream().toList();
                    return t.subList(25,45);
                }
            }
        }
        return teams.stream().toList();
    }

    private List<FantasyTeam> garbageStrategy(List<Player> players){

        List<Player> wk = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.WK)).sorted((o1, o2) -> (int) ((o2.getCredit() * 10) - (o1.getCredit() * 10))).collect(Collectors.toList());
        List<Player> wk1 = wk.subList(0, 3);
        List<Player> wk2 = wk.subList(0,3);

        List<Player> bat = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BAT)).sorted((o1, o2) -> (int) ((o2.getCredit() * 10) - (o1.getCredit() * 10))).collect(Collectors.toList());
        List<Player> bat1 = bat.subList(0, 3);
        List<Player> bat2 = bat.subList(1, 4);

        List<Player> bowl = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BOWL)).sorted((o1, o2) -> (int) ((o2.getCredit() * 10) - (o1.getCredit() * 10))).collect(Collectors.toList());
        List<Player> bowl1 = bowl.subList(0, 3);
        List<Player> bowl2 = bowl.subList(1, 4);

        List<Player> ar = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.AR)).sorted((o1, o2) -> (int) ((o2.getCredit() * 10) - (o1.getCredit() * 10))).collect(Collectors.toList());
        List<Player> ar1 = ar.subList(0, 2);
        List<Player> ar2 = ar.subList(1, 3);

        List<List<Player>> teamPlayers = new ArrayList<>();

        teamPlayers.add(Stream.of(wk1,bat2,bowl1,ar1).flatMap(Collection::stream)
                .collect(Collectors.toList()));
        teamPlayers.add(Stream.of(wk2,bat1,bowl2,ar2).flatMap(Collection::stream)
                .collect(Collectors.toList()));
        List<FantasyTeam> teams = new ArrayList<>();
        for (List<Player> p:teamPlayers){
            for(int i=0;i<5;i++){
                for(int j=0;j<5;j++){
                    if(i==j)
                        continue;
                    FantasyTeam team = new FantasyTeam(p.stream().collect(Collectors.toSet()), p.get(i), p.get(j));
                    if(team.isValid()){
                        teams.add(team);
                    }
                }
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