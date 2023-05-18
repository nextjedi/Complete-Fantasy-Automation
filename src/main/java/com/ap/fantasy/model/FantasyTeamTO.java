package com.ap.fantasy.model;

import java.util.List;
import java.util.stream.Collectors;

public class FantasyTeamTO {
    private List<String> wk;
    private List<String> bat;
    private List<String> bowl;
    private List<String> al;
    private String captain;
    private String vcaptain;

    public FantasyTeamTO(FantasyTeam fantasyTeam) {

        this.wk = fantasyTeam.getPlayers().stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.WK)).map(playerPlaying -> playerPlaying.getName().toLowerCase()).collect(Collectors.toList());
        this.bat = fantasyTeam.getPlayers().stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BAT)).map(playerPlaying -> playerPlaying.getName().toLowerCase()).collect(Collectors.toList());
        this.bowl = fantasyTeam.getPlayers().stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.BOWL)).map(playerPlaying -> playerPlaying.getName().toLowerCase()).collect(Collectors.toList());
        this.al = fantasyTeam.getPlayers().stream().filter(playerPlaying -> playerPlaying.getType().equals(PlayerType.AR)).map(playerPlaying -> playerPlaying.getName().toLowerCase()).collect(Collectors.toList());
        this.captain = fantasyTeam.getCaptain().getName();
        this.vcaptain = fantasyTeam.getvCaptain().getName();
    }

    public List<String> getWk() {
        return wk;
    }

    public void setWk(List<String> wk) {
        this.wk = wk;
    }

    public List<String> getBat() {
        return bat;
    }

    public void setBat(List<String> bat) {
        this.bat = bat;
    }

    public List<String> getBowl() {
        return bowl;
    }

    public void setBowl(List<String> bowl) {
        this.bowl = bowl;
    }

    public List<String> getAl() {
        return al;
    }

    public void setAl(List<String> al) {
        this.al = al;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    public String getVcaptain() {
        return vcaptain;
    }

    public void setVcaptain(String vcaptain) {
        this.vcaptain = vcaptain;
    }
}
