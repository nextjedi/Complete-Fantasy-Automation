package com.ap.fantasy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import static com.ap.fantasy.model.Constant.DIGIT_ONLY_REGEX;


@Data
@Entity
public class Player implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private int rowId;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player() {
    }

    public Player(String name, float credit, PlayerType type, boolean isPlaying, float selectedBy, int points, Team team, int battingOrder) {
        this.name = name;
        this.credit = credit;
        this.type = type;
        this.isPlaying = isPlaying;
        this.selectedBy = selectedBy;
        this.points = points;
        this.team = team;
        this.battingOrder = battingOrder;
    }

    public Player(Map<String,String> playerMap,PlayerType type,int playerRow) {
        this.name = playerMap.getOrDefault("player_name","");
        this.credit = Float.parseFloat(playerMap.getOrDefault("players-credits","0"));
        this.type = type;
        this.isPlaying = false;
        this.selectedBy = Float.parseFloat(playerMap.getOrDefault("select-by-percent","").replaceAll(DIGIT_ONLY_REGEX, ""));
        this.points = Integer.parseInt(playerMap.getOrDefault("player-points","0"));
        this.team = Team.valueOf(playerMap.getOrDefault("squad_name","").replaceAll("-",""));
        this.battingOrder = -1;
        rowId = playerRow;
    }
    public boolean isValid(){
        return !((name!=null && name.isBlank()) || team == null || credit == 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equalsIgnoreCase(player.getName()) && type.equals(player.getType()) && points == player.getPoints();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


}
