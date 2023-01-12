package com.fantasy.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MatchDetails implements Serializable {
    private Date time;
    private List<Team> teams;
    private int prizePool;
    private Team first;
    private Team Second;
    private PitchType pitch;

    private BowlingType bowlingType;
    private int avgScore;
    private String Venue;
    private List<Player> players;

    public MatchDetails(){}

    public MatchDetails(Date time, Team first, Team second, PitchType pitch, int avgScore, String venue, List<Player> players) {
        this.time = time;
        this.first = first;
        Second = second;
        this.pitch = pitch;
        this.avgScore = avgScore;
        Venue = venue;
        this.players = players;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public int getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(int prizePool) {
        this.prizePool = prizePool;
    }

    public BowlingType getBowlingType() {
        return bowlingType;
    }

    public void setBowlingType(BowlingType bowlingType) {
        this.bowlingType = bowlingType;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Team getFirst() {
        return first;
    }

    public void setFirst(Team first) {
        this.first = first;
    }

    public Team getSecond() {
        return Second;
    }

    public void setSecond(Team second) {
        Second = second;
    }

    public PitchType getPitch() {
        return pitch;
    }

    public void setPitch(PitchType pitch) {
        this.pitch = pitch;
    }

    public int getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(int avgScore) {
        this.avgScore = avgScore;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
