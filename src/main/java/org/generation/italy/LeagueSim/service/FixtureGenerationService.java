package org.generation.italy.LeagueSim.service;

import org.generation.italy.LeagueSim.domain.Match;
import org.generation.italy.LeagueSim.domain.Matchday;
import org.generation.italy.LeagueSim.domain.Team;
import org.generation.italy.LeagueSim.repository.MatchRepository;
import org.generation.italy.LeagueSim.repository.MatchdayRepository;
import org.generation.italy.LeagueSim.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a full double round-robin calendar (andata e ritorno) for every
 * {@link Team} present in the database, using the standard "circle method".
 */
@Service
public class FixtureGenerationService {

    private final TeamRepository teamRepository;
    private final MatchdayRepository matchdayRepository;
    private final MatchRepository matchRepository;

    public FixtureGenerationService(TeamRepository teamRepository, MatchdayRepository matchdayRepository,
                                     MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchdayRepository = matchdayRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public void generateSeasonIfEmpty() {
        if (matchdayRepository.count() > 0) {
            return;
        }
        List<Team> teams = teamRepository.findAll();
        if (teams.size() < 2) {
            return;
        }

        List<List<Team[]>> rounds = buildDoubleRoundRobin(teams);
        LocalDate firstMatchday = LocalDate.now().plusWeeks(1);

        int number = 1;
        for (List<Team[]> pairings : rounds) {
            Matchday matchday = matchdayRepository.save(new Matchday(number, firstMatchday.plusWeeks(number - 1L)));
            for (Team[] pairing : pairings) {
                matchRepository.save(new Match(matchday, pairing[0], pairing[1]));
            }
            number++;
        }
    }

    private List<List<Team[]>> buildDoubleRoundRobin(List<Team> teamsIn) {
        List<Team> teams = new ArrayList<>(teamsIn);
        if (teams.size() % 2 != 0) {
            teams.add(null);
        }

        int n = teams.size();
        int roundsCount = n - 1;
        List<List<Team[]>> firstLeg = new ArrayList<>();

        for (int round = 0; round < roundsCount; round++) {
            List<Team[]> pairings = new ArrayList<>();
            for (int i = 0; i < n / 2; i++) {
                Team home = teams.get(i);
                Team away = teams.get(n - 1 - i);
                if (home == null || away == null) {
                    continue;
                }
                pairings.add(round % 2 == 0 ? new Team[]{home, away} : new Team[]{away, home});
            }
            firstLeg.add(pairings);

            Team fixed = teams.get(0);
            List<Team> rest = new ArrayList<>(teams.subList(1, n));
            Team last = rest.remove(rest.size() - 1);
            rest.add(0, last);
            teams = new ArrayList<>();
            teams.add(fixed);
            teams.addAll(rest);
        }

        List<List<Team[]>> allRounds = new ArrayList<>(firstLeg);
        for (List<Team[]> pairings : firstLeg) {
            List<Team[]> reverseLeg = new ArrayList<>();
            for (Team[] pairing : pairings) {
                reverseLeg.add(new Team[]{pairing[1], pairing[0]});
            }
            allRounds.add(reverseLeg);
        }

        return allRounds;
    }
}
