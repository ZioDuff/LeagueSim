package org.generation.italy.LeagueSim.web.dto;

import org.generation.italy.LeagueSim.domain.PlayerMatchStats;

import java.math.BigDecimal;

public record PlayerMatchResultDto(
        Long playerId,
        BigDecimal rating,
        int goals,
        int goalsConceded,
        int ownGoals,
        int assists,
        int penaltySaved,
        int penaltyFailed,
        boolean cleanSheet,
        int yellowCards,
        boolean redCard
) {
    public static PlayerMatchResultDto from(PlayerMatchStats stats) {
        return new PlayerMatchResultDto(
                stats.getPlayer().getId(),
                stats.getRating(),
                stats.getGoals(),
                stats.getGoalsConceded(),
                stats.getOwnGoals(),
                stats.getAssists(),
                stats.getPenaltySaved(),
                stats.getPenaltyFailed(),
                stats.isCleanSheet(),
                stats.getYellowCards(),
                stats.isRedCard()
        );
    }
}
