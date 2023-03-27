package com.fantasy.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Player implements Serializable {
    private String name;
    private float credit;
    private PlayerType type;
    private boolean isPlaying;
    private float selectedBy;
    private int points;
    private Team team;
    private int battingOrder;
    private int bowlingOvers;
    private boolean isKeyBowler;
    private boolean isRunCandidate;

    public Player() {
    }

    public Player(String name, float credit, PlayerType type, boolean isPlaying, float selectedBy, int points, Team team, int battinOrder) {
        this.name = name;
        this.credit = credit;
        this.type = type;
        this.isPlaying = isPlaying;
        this.selectedBy = selectedBy;
        this.points = points;
        this.team = team;
        this.battingOrder = battinOrder;
    }

    public Player(Map<String,String> playerMap,PlayerType type) {
        this.name = playerMap.getOrDefault("player_name","");
        this.credit = Float.valueOf(playerMap.getOrDefault("players-credits","0"));
        this.type = type;
        this.isPlaying = false;
        this.selectedBy = Float.valueOf(playerMap.getOrDefault("select-by-percent","").replaceAll("[^0-9\\.]", ""));
        this.points = Integer.parseInt(playerMap.getOrDefault("player-points","0"));
        this.team = Team.valueOf(playerMap.getOrDefault("squad_name","").replaceAll("-",""));
        this.battingOrder = -1;
    }
    public boolean isValid(){
        return !(name.isBlank() || team == null || credit == 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.toLowerCase().equals(player.getName().toLowerCase()) && type.equals(player.getType()) && points == player.getPoints();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public int getBowlingOvers() {
        return bowlingOvers;
    }

    public void setBowlingOvers(int bowlingOvers) {
        this.bowlingOvers = bowlingOvers;
    }

    public boolean isKeyBowler() {
        return isKeyBowler;
    }

    public void setKeyBowler(boolean keyBowler) {
        isKeyBowler = keyBowler;
    }

    public boolean isRunCandidate() {
        return isRunCandidate;
    }

    public void setRunCandidate(boolean runCandidate) {
        isRunCandidate = runCandidate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public float getSelectedBy() {
        return selectedBy;
    }

    public void setSelectedBy(float selectedBy) {
        this.selectedBy = selectedBy;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getBattingOrder() {
        return battingOrder;
    }

    public void setBattingOrder(int battingOrder) {
        this.battingOrder = battingOrder;
    }
}
