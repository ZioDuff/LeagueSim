package org.generation.italy.LeagueSim.service;

import org.generation.italy.LeagueSim.domain.Player;
import org.generation.italy.LeagueSim.domain.Position;
import org.generation.italy.LeagueSim.domain.Team;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchSimulationServiceTest {

    private final MatchSimulationService service = new MatchSimulationService();

    @RepeatedTest(50)
    void simulateProducesConsistentAndPlausibleStats() {
        Team home = new Team("Home FC", 70);
        Team away = new Team("Away FC", 55);
        List<Player> homeRoster = roster(home);
        List<Player> awayRoster = roster(away);

        SimulationOutcome outcome = service.simulate(home, away, homeRoster, awayRoster);

        assertEquals(11, outcome.homeLines().size());
        assertEquals(11, outcome.awayLines().size());

        assertEquals(outcome.homeGoals(), goalsScoredBy(outcome.homeLines()) + ownGoalsConcededBy(outcome.awayLines()));
        assertEquals(outcome.awayGoals(), goalsScoredBy(outcome.awayLines()) + ownGoalsConcededBy(outcome.homeLines()));

        for (PlayerStatLine line : outcome.homeLines()) {
            assertRatingInRange(line.rating());
            assertEquals(line.player().getPosition() == Position.P ? outcome.awayGoals() : 0, line.goalsConceded());
            assertEquals(outcome.awayGoals() == 0, line.cleanSheet());
        }
        for (PlayerStatLine line : outcome.awayLines()) {
            assertRatingInRange(line.rating());
            assertEquals(line.player().getPosition() == Position.P ? outcome.homeGoals() : 0, line.goalsConceded());
            assertEquals(outcome.homeGoals() == 0, line.cleanSheet());
        }
    }

    private void assertRatingInRange(BigDecimal rating) {
        assertTrue(rating.compareTo(BigDecimal.valueOf(4.0)) >= 0, "rating too low: " + rating);
        assertTrue(rating.compareTo(BigDecimal.valueOf(10.0)) <= 0, "rating too high: " + rating);
    }

    private int goalsScoredBy(List<PlayerStatLine> lines) {
        return lines.stream().mapToInt(PlayerStatLine::goals).sum();
    }

    private int ownGoalsConcededBy(List<PlayerStatLine> lines) {
        return lines.stream().mapToInt(PlayerStatLine::ownGoals).sum();
    }

    private List<Player> roster(Team team) {
        List<Player> players = new ArrayList<>();
        int shirt = 1;
        for (int i = 0; i < 3; i++) {
            players.add(new Player("P" + shirt, "Keeper", team, shirt++, Position.P, 10, false));
        }
        for (int i = 0; i < 6; i++) {
            players.add(new Player("D" + shirt, "Defender", team, shirt++, Position.D, 10, false));
        }
        for (int i = 0; i < 6; i++) {
            players.add(new Player("C" + shirt, "Midfielder", team, shirt++, Position.C, 15, false));
        }
        for (int i = 0; i < 3; i++) {
            players.add(new Player("A" + shirt, "Forward", team, shirt++, Position.A, 20, false));
        }
        return players;
    }
}
