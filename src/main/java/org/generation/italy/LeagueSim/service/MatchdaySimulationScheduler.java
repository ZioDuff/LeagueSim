package org.generation.italy.LeagueSim.service;

import org.generation.italy.LeagueSim.domain.Matchday;
import org.generation.italy.LeagueSim.repository.MatchdayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Daily job that simulates every {@link Matchday} whose date has arrived (or
 * passed) and that hasn't been closed yet, so results are ready before other
 * services (e.g. fantafootball) read them.
 */
@Component
public class MatchdaySimulationScheduler {

    private static final Logger log = LoggerFactory.getLogger(MatchdaySimulationScheduler.class);

    private final MatchdayRepository matchdayRepository;
    private final MatchdaySimulationService matchdaySimulationService;

    public MatchdaySimulationScheduler(MatchdayRepository matchdayRepository,
                                        MatchdaySimulationService matchdaySimulationService) {
        this.matchdayRepository = matchdayRepository;
        this.matchdaySimulationService = matchdaySimulationService;
    }

    @Scheduled(cron = "${leaguesim.simulation.cron:0 0 23 * * *}", zone = "${leaguesim.simulation.zone:Europe/Rome}")
    public void simulateDueMatchdays() {
        List<Matchday> dueMatchdays = matchdayRepository.findByDateLessThanEqualAndClosedFalse(LocalDate.now());
        for (Matchday matchday : dueMatchdays) {
            try {
                matchdaySimulationService.simulate(matchday.getNumber());
                log.info("Simulated matchday {} (date {})", matchday.getNumber(), matchday.getDate());
            } catch (Exception e) {
                log.error("Failed to simulate matchday {} (date {})", matchday.getNumber(), matchday.getDate(), e);
            }
        }
    }
}
