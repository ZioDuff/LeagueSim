package org.generation.italy.LeagueSim.config;

import org.generation.italy.LeagueSim.service.FixtureGenerationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Generates the season calendar on first startup, if none exists yet.
 */
@Component
public class StartupFixtureRunner implements ApplicationRunner {

    private final FixtureGenerationService fixtureGenerationService;

    public StartupFixtureRunner(FixtureGenerationService fixtureGenerationService) {
        this.fixtureGenerationService = fixtureGenerationService;
    }

    @Override
    public void run(ApplicationArguments args) {
        fixtureGenerationService.generateSeasonIfEmpty();
    }
}
