package org.generation.italy.LeagueSim.service;

import org.generation.italy.LeagueSim.domain.Player;
import org.generation.italy.LeagueSim.domain.Position;
import org.generation.italy.LeagueSim.domain.Team;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Simulates a single match between two rosters, producing a scoreline and a
 * per-player statistics line (goals, assists, cards, rating, ...) for every
 * starter. The model is a lightweight, probabilistic approximation of a real
 * match, not a physics/tactics engine.
 */
@Service
public class MatchSimulationService {

    private static final int STARTERS_PER_TEAM = 11;
    private static final int STARTER_GOALKEEPERS = 1;
    private static final int STARTER_DEFENDERS = 4;
    private static final int STARTER_MIDFIELDERS = 3;
    private static final int STARTER_FORWARDS = 3;

    private static final double HOME_BASE_LAMBDA = 1.45;
    private static final double AWAY_BASE_LAMBDA = 1.10;
    private static final double MIN_STRENGTH_RATIO = 0.5;
    private static final double MAX_STRENGTH_RATIO = 2.0;

    private static final double OWN_GOAL_CHANCE = 0.03;
    private static final double PENALTY_CHANCE = 0.10;
    private static final double PENALTY_SCORE_PROB = 0.70;
    private static final double PENALTY_SAVE_PROB = 0.18;
    private static final double ASSIST_CHANCE = 0.65;

    private static final double STRAIGHT_RED_CHANCE = 0.015;
    private static final double YELLOW_CHANCE = 0.12;
    private static final double SECOND_YELLOW_CHANCE = 0.06;

    private static final double BASE_RATING = 6.0;
    private static final double GOAL_BONUS = 3.0;
    private static final double ASSIST_BONUS = 1.0;
    private static final double OWN_GOAL_MALUS = 2.0;
    private static final double YELLOW_MALUS = 0.5;
    private static final double RED_MALUS = 1.0;
    private static final double PENALTY_SAVED_BONUS = 3.0;
    private static final double PENALTY_FAILED_MALUS = 3.0;
    private static final double CLEAN_SHEET_BONUS = 1.0;
    private static final double CONCEDED_MALUS_STEP = 0.5;
    private static final double RATING_NOISE_RANGE = 0.75;
    private static final double MIN_RATING = 4.0;
    private static final double MAX_RATING = 10.0;

    private static final Map<Position, Double> ATTACK_WEIGHTS = weights(4.0, 0.6, 2.5, 0.05);
    private static final Map<Position, Double> ASSIST_WEIGHTS = weights(1.0, 1.0, 3.0, 2.0);
    private static final Map<Position, Double> OWN_GOAL_WEIGHTS = weights(0.2, 3.0, 1.0, 0.5);

    private final Random random = new Random();

    private static Map<Position, Double> weights(double forward, double defender, double midfielder, double goalkeeper) {
        Map<Position, Double> map = new EnumMap<>(Position.class);
        map.put(Position.A, forward);
        map.put(Position.D, defender);
        map.put(Position.C, midfielder);
        map.put(Position.P, goalkeeper);
        return map;
    }

    public SimulationOutcome simulate(Team homeTeam, Team awayTeam, List<Player> homeRoster, List<Player> awayRoster) {
        List<Player> homeStarters = selectStarters(homeRoster);
        List<Player> awayStarters = selectStarters(awayRoster);

        Map<Player, StatAccumulator> homeAcc = initAccumulators(homeStarters);
        Map<Player, StatAccumulator> awayAcc = initAccumulators(awayStarters);

        double homeLambda = expectedGoals(homeTeam.getStrength(), awayTeam.getStrength(), true);
        double awayLambda = expectedGoals(awayTeam.getStrength(), homeTeam.getStrength(), false);

        int homeChances = poisson(homeLambda);
        int awayChances = poisson(awayLambda);

        int homeGoals = resolveAttack(homeChances, homeStarters, homeAcc, awayStarters, awayAcc);
        int awayGoals = resolveAttack(awayChances, awayStarters, awayAcc, homeStarters, homeAcc);

        applyCards(homeStarters, homeAcc);
        applyCards(awayStarters, awayAcc);

        boolean homeCleanSheet = awayGoals == 0;
        boolean awayCleanSheet = homeGoals == 0;

        List<PlayerStatLine> homeLines = buildLines(homeStarters, homeAcc, awayGoals, homeCleanSheet);
        List<PlayerStatLine> awayLines = buildLines(awayStarters, awayAcc, homeGoals, awayCleanSheet);

        return new SimulationOutcome(homeGoals, awayGoals, homeLines, awayLines);
    }

    private List<Player> selectStarters(List<Player> roster) {
        List<Player> available = roster.stream().filter(p -> !p.isInjured()).collect(Collectors.toList());

        List<Player> starters = new ArrayList<>();
        starters.addAll(takeUpTo(available, Position.P, STARTER_GOALKEEPERS));
        starters.addAll(takeUpTo(available, Position.D, STARTER_DEFENDERS));
        starters.addAll(takeUpTo(available, Position.C, STARTER_MIDFIELDERS));
        starters.addAll(takeUpTo(available, Position.A, STARTER_FORWARDS));

        if (starters.size() < STARTERS_PER_TEAM) {
            List<Player> topUp = available.stream()
                    .filter(p -> !starters.contains(p))
                    .sorted(Comparator.comparingInt(Player::getPrice).reversed())
                    .toList();
            for (Player p : topUp) {
                if (starters.size() >= STARTERS_PER_TEAM) {
                    break;
                }
                starters.add(p);
            }
        }
        return starters;
    }

    private List<Player> takeUpTo(List<Player> pool, Position position, int n) {
        return pool.stream()
                .filter(p -> p.getPosition() == position)
                .sorted(Comparator.comparingInt(Player::getPrice).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    private Map<Player, StatAccumulator> initAccumulators(List<Player> starters) {
        Map<Player, StatAccumulator> map = new HashMap<>();
        for (Player p : starters) {
            map.put(p, new StatAccumulator());
        }
        return map;
    }

    private double expectedGoals(int attackStrength, int defendStrength, boolean isHome) {
        double ratio = (double) attackStrength / Math.max(1, defendStrength);
        ratio = Math.max(MIN_STRENGTH_RATIO, Math.min(MAX_STRENGTH_RATIO, ratio));
        double base = isHome ? HOME_BASE_LAMBDA : AWAY_BASE_LAMBDA;
        return base * ratio;
    }

    private int poisson(double lambda) {
        double l = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= random.nextDouble();
        } while (p > l && k < 10);
        return k - 1;
    }

    private int resolveAttack(int chances, List<Player> attackers, Map<Player, StatAccumulator> attackerAcc,
                               List<Player> defenders, Map<Player, StatAccumulator> defenderAcc) {
        int goals = 0;
        for (int i = 0; i < chances; i++) {
            if (!defenders.isEmpty() && random.nextDouble() < OWN_GOAL_CHANCE) {
                Player defender = pickWeighted(defenders, OWN_GOAL_WEIGHTS);
                defenderAcc.get(defender).ownGoals++;
                goals++;
                continue;
            }

            if (random.nextDouble() < PENALTY_CHANCE) {
                Player taker = pickWeighted(attackers, ATTACK_WEIGHTS);
                double roll = random.nextDouble();
                if (roll < PENALTY_SCORE_PROB) {
                    attackerAcc.get(taker).goals++;
                    goals++;
                } else if (roll < PENALTY_SCORE_PROB + PENALTY_SAVE_PROB) {
                    attackerAcc.get(taker).penaltyFailed++;
                    findGoalkeeper(defenders).ifPresent(gk -> defenderAcc.get(gk).penaltySaved++);
                } else {
                    attackerAcc.get(taker).penaltyFailed++;
                }
                continue;
            }

            Player scorer = pickWeighted(attackers, ATTACK_WEIGHTS);
            attackerAcc.get(scorer).goals++;
            goals++;

            if (random.nextDouble() < ASSIST_CHANCE) {
                Player assister = pickWeighted(exclude(attackers, scorer), ASSIST_WEIGHTS);
                if (assister != null) {
                    attackerAcc.get(assister).assists++;
                }
            }
        }
        return goals;
    }

    private void applyCards(List<Player> starters, Map<Player, StatAccumulator> acc) {
        for (Player p : starters) {
            StatAccumulator a = acc.get(p);
            double roll = random.nextDouble();
            if (roll < STRAIGHT_RED_CHANCE) {
                a.redCard = true;
            } else if (roll < STRAIGHT_RED_CHANCE + YELLOW_CHANCE) {
                a.yellowCards = 1;
                if (random.nextDouble() < SECOND_YELLOW_CHANCE) {
                    a.yellowCards = 2;
                    a.redCard = true;
                }
            }
        }
    }

    private List<PlayerStatLine> buildLines(List<Player> starters, Map<Player, StatAccumulator> acc,
                                             int goalsConceded, boolean cleanSheet) {
        List<PlayerStatLine> lines = new ArrayList<>();
        for (Player p : starters) {
            StatAccumulator a = acc.get(p);
            BigDecimal rating = computeRating(p, a, goalsConceded, cleanSheet);
            lines.add(new PlayerStatLine(p, rating, a.goals, goalsConceded, a.ownGoals, a.assists,
                    a.penaltySaved, a.penaltyFailed, cleanSheet, a.yellowCards, a.redCard, true));
        }
        return lines;
    }

    private BigDecimal computeRating(Player p, StatAccumulator a, int goalsConceded, boolean cleanSheet) {
        double rating = BASE_RATING;
        rating += a.goals * GOAL_BONUS;
        rating += a.assists * ASSIST_BONUS;
        rating -= a.ownGoals * OWN_GOAL_MALUS;
        rating -= a.yellowCards >= 1 ? YELLOW_MALUS : 0;
        rating -= a.redCard ? RED_MALUS : 0;
        rating += a.penaltySaved * PENALTY_SAVED_BONUS;
        rating -= a.penaltyFailed * PENALTY_FAILED_MALUS;

        boolean defensivePosition = p.getPosition() == Position.P || p.getPosition() == Position.D;
        if (cleanSheet && defensivePosition) {
            rating += CLEAN_SHEET_BONUS;
        }
        if (defensivePosition && goalsConceded >= 2) {
            rating -= CONCEDED_MALUS_STEP * (goalsConceded / 2);
        }

        rating += (random.nextDouble() - 0.5) * RATING_NOISE_RANGE;
        rating = Math.max(MIN_RATING, Math.min(MAX_RATING, rating));
        return BigDecimal.valueOf(rating).setScale(2, RoundingMode.HALF_UP);
    }

    private List<Player> exclude(List<Player> players, Player excluded) {
        return players.stream().filter(p -> !p.equals(excluded)).toList();
    }

    private java.util.Optional<Player> findGoalkeeper(List<Player> starters) {
        return starters.stream().filter(p -> p.getPosition() == Position.P).findFirst();
    }

    private Player pickWeighted(List<Player> pool, Map<Position, Double> weights) {
        if (pool.isEmpty()) {
            return null;
        }
        double total = pool.stream().mapToDouble(p -> weights.getOrDefault(p.getPosition(), 1.0)).sum();
        double r = random.nextDouble() * total;
        double cumulative = 0;
        for (Player p : pool) {
            cumulative += weights.getOrDefault(p.getPosition(), 1.0);
            if (r <= cumulative) {
                return p;
            }
        }
        return pool.get(pool.size() - 1);
    }

    private static class StatAccumulator {
        int goals;
        int ownGoals;
        int assists;
        int penaltySaved;
        int penaltyFailed;
        int yellowCards;
        boolean redCard;
    }
}
