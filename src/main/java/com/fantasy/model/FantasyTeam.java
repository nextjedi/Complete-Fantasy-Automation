package com.fantasy.model;

import java.util.Set;

public class FantasyTeam {
    private Set<Player> players;
    private Player captain;
    private Player vCaptain;

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public Player getCaptain() {
        return captain;
    }

    public void setCaptain(Player captain) {
        this.captain = captain;
    }

    public Player getvCaptain() {
        return vCaptain;
    }

    public void setvCaptain(Player vCaptain) {
        this.vCaptain = vCaptain;
    }

    public FantasyTeam(Set<Player> players, Player captain, Player vcaptain) {


        this.players = players;
        this.captain = captain;
        this.vCaptain = vcaptain;
    }
    public boolean isValid(){
//        validate before creating
//        todo: yet to complete validation
//        distinct players

        long bat = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BAT)).count();
        long wk = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.WK)).count();
        long al = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.AR)).count();
        long bowl = players.stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BOWL)).count();

        if(!((3<=bat&& bat<=6) &&(3<=bowl&& bowl<=6) && (1<=al&& al<=4) && (1<=wk&& wk<=4))){
            return false;
        }
        Team team = players.iterator().next().getTeam();
        int teamCount = (int) players.stream().filter(player -> player.getTeam().equals(team)).count();
        if(teamCount<4 || teamCount >7){
            return false;
        }



        double sum = players.stream().mapToDouble(value -> value.getCredit()).sum();
        if((players.size() != 11)||(sum >100)){
            return false;
        }
        if(players.contains(captain) && players.contains(vCaptain)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
//        all the players are same and has same cvc
        if (this == o) return true;
        if (!(o instanceof FantasyTeam)) return false;
        FantasyTeam that = (FantasyTeam) o;
        return players.equals(that.players) && captain.equals(that.captain) && vCaptain.equals(that.vCaptain);
    }

    @Override
    public String toString() {
        return "FantasyTeam{" +
                "players=" + players +
                ", captain=" + captain +
                ", vcaptain=" + vCaptain +
                '}';
    }
}
