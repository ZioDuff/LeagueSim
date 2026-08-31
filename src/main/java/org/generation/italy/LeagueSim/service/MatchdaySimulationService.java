package org.generation.italy.LeagueSim.service;

import org.generation.italy.LeagueSim.domain.Match;
import org.generation.italy.LeagueSim.domain.Matchday;
import org.generation.italy.LeagueSim.domain.Player;
import org.generation.italy.LeagueSim.domain.PlayerMatchStats;
import org.generation.italy.LeagueSim.repository.MatchRepository;
import org.generation.italy.LeagueSim.repository.MatchdayRepository;
import org.generation.italy.LeagueSim.repository.PlayerMatchStatsRepository;
import org.generation.italy.LeagueSim.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Orchestrates the simulation of every {@link Match} in a given {@link Matchday}:
 * runs {@link MatchSimulationService} for each fixture, persists the resulting
 * score and per-player statistics, then closes the matchday.
 */
@Service
public class MatchdaySimulationService {

    private final MatchdayRepository matchdayRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final MatchSimulationService matchSimulationService;

    public MatchdaySimulationService(MatchdayRepository matchdayRepository, MatchRepository matchRepository,
                                      PlayerRepository playerRepository,
                                      PlayerMatchStatsRepository playerMatchStatsRepository,
                                      MatchSimulationService matchSimulationService) {
        this.matchdayRepository = matchdayRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.playerMatchStatsRepository = playerMatchStatsRepository;
        this.matchSimulationService = matchSimulationService;
    }

    @Transactional
    public Matchday simulate(int matchdayNumber) {
        Matchday matchday = matchdayRepository.findByNumber(matchdayNumber)
                .orElseThrow(() -> new NoSuchElementException("Matchday " + matchdayNumber + " not found"));

        for (Match match : matchRepository.findByMatchday(matchday)) {
            if (match.isPlayed()) {
                continue;
            }
            simulateMatch(match);
        }

        matchday.markClosed();
        return matchdayRepository.save(matchday);
    }

    private void simulateMatch(Match match) {
        List<Player> homeRoster = playerRepository.findByTeam(match.getHomeTeam());
        List<Player> awayRoster = playerRepository.findByTeam(match.getAwayTeam());

        SimulationOutcome outcome = matchSimulationService.simulate(
                match.getHomeTeam(), match.getAwayTeam(), homeRoster, awayRoster);

        match.recordResult(outcome.homeGoals(), outcome.awayGoals());
        matchRepository.save(match);

        saveStats(match, outcome.homeLines());
        saveStats(match, outcome.awayLines());
    }

    private void saveStats(Match match, List<PlayerStatLine> lines) {
        for (PlayerStatLine line : lines) {
            playerMatchStatsRepository.save(new PlayerMatchStats(
                    line.player(), match, line.rating(), line.goals(), line.goalsConceded(),
                    line.ownGoals(), line.assists(), line.penaltySaved(), line.penaltyFailed(),
                    line.cleanSheet(), line.yellowCards(), line.redCard(), line.starter()));
        }
    }
}
