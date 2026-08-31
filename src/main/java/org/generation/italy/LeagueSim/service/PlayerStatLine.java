package org.generation.italy.LeagueSim.service;

import org.generation.italy.LeagueSim.domain.Player;

import java.math.BigDecimal;

public record PlayerStatLine(
        Player player,
        BigDecimal rating,
        int goals,
        int goalsConceded,
        int ownGoals,
        int assists,
        int penaltySaved,
        int penaltyFailed,
        boolean cleanSheet,
        int yellowCards,
        boolean redCard,
        boolean starter
) {
}
