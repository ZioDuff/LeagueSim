package org.generation.italy.LeagueSim.service;

import java.util.List;

public record SimulationOutcome(
        int homeGoals,
        int awayGoals,
        List<PlayerStatLine> homeLines,
        List<PlayerStatLine> awayLines
) {
}
