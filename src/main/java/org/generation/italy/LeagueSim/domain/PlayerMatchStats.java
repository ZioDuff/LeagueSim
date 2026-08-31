package org.generation.italy.LeagueSim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "player_match_stats")
public class PlayerMatchStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_player_match_stats_id")
    @SequenceGenerator(name = "seq_player_match_stats_id", sequenceName = "seq_player_match_stats_id", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private BigDecimal rating;

    @Column(nullable = false)
    private int goals;

    @Column(name = "goals_conceded", nullable = false)
    private int goalsConceded;

    @Column(name = "own_goals", nullable = false)
    private int ownGoals;

    @Column(nullable = false)
    private int assists;

    @Column(name = "penalty_saved", nullable = false)
    private int penaltySaved;

    @Column(name = "penalty_failed", nullable = false)
    private int penaltyFailed;

    @Column(name = "clean_sheet", nullable = false)
    private boolean cleanSheet;

    @Column(name = "yellow_cards", nullable = false)
    private int yellowCards;

    @Column(name = "red_card", nullable = false)
    private boolean redCard;

    @Column(nullable = false)
    private boolean starter;

    protected PlayerMatchStats() {
    }

    public PlayerMatchStats(Player player, Match match, BigDecimal rating, int goals, int goalsConceded,
                             int ownGoals, int assists, int penaltySaved, int penaltyFailed,
                             boolean cleanSheet, int yellowCards, boolean redCard, boolean starter) {
        this.player = player;
        this.match = match;
        this.rating = rating;
        this.goals = goals;
        this.goalsConceded = goalsConceded;
        this.ownGoals = ownGoals;
        this.assists = assists;
        this.penaltySaved = penaltySaved;
        this.penaltyFailed = penaltyFailed;
        this.cleanSheet = cleanSheet;
        this.yellowCards = yellowCards;
        this.redCard = redCard;
        this.starter = starter;
    }

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Match getMatch() {
        return match;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public int getGoals() {
        return goals;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }

    public int getOwnGoals() {
        return ownGoals;
    }

    public int getAssists() {
        return assists;
    }

    public int getPenaltySaved() {
        return penaltySaved;
    }

    public int getPenaltyFailed() {
        return penaltyFailed;
    }

    public boolean isCleanSheet() {
        return cleanSheet;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public boolean isRedCard() {
        return redCard;
    }

    public boolean isStarter() {
        return starter;
    }
}
