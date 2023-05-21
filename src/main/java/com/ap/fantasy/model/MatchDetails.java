package com.ap.fantasy.model;

import com.ap.fantasy.creation.Helper;
import io.appium.java_client.android.AndroidElement;
import jakarta.persistence.*;
import lombok.Data;
import org.openqa.selenium.NoSuchElementException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Entity
public class MatchDetails implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private Date time;
    private float prizePool;
    @Enumerated(EnumType.STRING)
    private Team first;
    @Enumerated(EnumType.STRING)
    private Team Second;
    @Enumerated(EnumType.STRING)
    private PitchType pitch;
    @Enumerated(EnumType.STRING)
    private BowlingType bowlingType;
    private int avgScore;
    private String venue;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "match_player",
            joinColumns = @JoinColumn(name = "match_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "player_id",referencedColumnName = "id")
    )
    private List<Player> players;
    private Boolean isLineupOut;
    private String tournamentName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatchDetails(){}
    public static MatchDetails fromAndroidElement(AndroidElement matchCard) {
        try {
            MatchDetails matchDetails =  new MatchDetails();
            matchDetails.setTime(Helper.parseTime(matchCard.findElementByAccessibilityId("timer").getText()));


            var amount = matchCard.findElementByAccessibilityId("megaContestAmount").getText();
            matchDetails.setPrizePool(Helper.amountInWordToNumber(amount));
            Team team = Team.findByName(matchCard.findElementByAccessibilityId("txtMatchNameLeft").getText().replaceAll("-", ""));
            Team team1 = Team.findByName(matchCard.findElementByAccessibilityId("txtMatchNameRight").getText().replaceAll("-", ""));
            if(team == null || team1 == null){
                return null;
            }
            matchDetails.setFirst(team);
            matchDetails.setSecond(team1);
            matchDetails.setTournamentName( matchCard.findElementByAccessibilityId("tour-name").getText());
            try {
                matchCard.findElementByAccessibilityId("lineupOuttxt");
                matchDetails.setIsLineupOut(true);
            } catch (NoSuchElementException e) {
                matchDetails.setIsLineupOut(false);
            }
            return matchDetails;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public MatchDetails(Date time, Team first, Team second, PitchType pitch, int avgScore, String venue, List<Player> players) {
        this.time = time;
        this.first = first;
        Second = second;
        this.pitch = pitch;
        this.avgScore = avgScore;
        this.venue = venue;
        this.players = players;
    }

    public List<Team> getTeams(){
        return List.of(first,Second);
    }
    public void setTeams(List<Team> teams){
        if (teams.size() != 2) throw new IllegalArgumentException("Teams size should be 2");
        first = teams.get(0);
        Second = teams.get(1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchDetails that = (MatchDetails) o;
        return List.of(first,Second).containsAll(List.of(that.first,that.Second));
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, first, Second);
    }
}
