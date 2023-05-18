package com.ap.fantasy.model;

import com.ap.fantasy.creation.Helper;
import io.appium.java_client.android.AndroidElement;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.NoSuchElementException;

import java.io.Serializable;
import java.util.*;

@Data
public class MatchDetails implements Serializable {
    private Date time;
    private List<Team> teams;
    private float prizePool;
    private Team first;
    private Team Second;
    private PitchType pitch;

    private BowlingType bowlingType;
    private int avgScore;
    private String Venue;
    private List<Player> players;
    private Boolean isLineupOut;
    private String tournamentName;

    public MatchDetails(){}
    public static MatchDetails fromAndroidElement(AndroidElement matchCard) {
        try {
            MatchDetails matchDetails =  new MatchDetails();
            matchDetails.setTime(Helper.parseTime(matchCard.findElementByAccessibilityId("timer").getText()));


            var amount = matchCard.findElementByAccessibilityId("megaContestAmount").getText();
            matchDetails.setPrizePool(Helper.amountInWordToNumber(amount));
            List<Team> teams = new ArrayList<>();
            Team team = Team.findByName(matchCard.findElementByAccessibilityId("txtMatchNameLeft").getText().replaceAll("-", ""));
            Team team1 = Team.findByName(matchCard.findElementByAccessibilityId("txtMatchNameRight").getText().replaceAll("-", ""));
            CollectionUtils.addIgnoreNull(teams, team);
            CollectionUtils.addIgnoreNull(teams, team1);
            matchDetails.setTeams(teams);
            if (teams.size() != 2) {
                return null;
            }
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
        Venue = venue;
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchDetails that = (MatchDetails) o;
        return new HashSet<>(teams).containsAll(that.teams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teams);
    }
}
