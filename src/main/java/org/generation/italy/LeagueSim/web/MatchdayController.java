package org.generation.italy.LeagueSim.web;

import org.generation.italy.LeagueSim.domain.Matchday;
import org.generation.italy.LeagueSim.repository.MatchdayRepository;
import org.generation.italy.LeagueSim.repository.PlayerMatchStatsRepository;
import org.generation.italy.LeagueSim.web.dto.MatchdayDto;
import org.generation.italy.LeagueSim.web.dto.PlayerMatchResultDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Comparator;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/matchdays")
public class MatchdayController {

    private final MatchdayRepository matchdayRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;

    public MatchdayController(MatchdayRepository matchdayRepository,
                               PlayerMatchStatsRepository playerMatchStatsRepository) {
        this.matchdayRepository = matchdayRepository;
        this.playerMatchStatsRepository = playerMatchStatsRepository;
    }

    @GetMapping
    public List<MatchdayDto> listMatchdays() {
        return matchdayRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Matchday::getNumber))
                .map(MatchdayDto::from)
                .toList();
    }

    @GetMapping("/{number}/results")
    public List<PlayerMatchResultDto> getResults(@PathVariable int number) {
        Matchday matchday = matchdayRepository.findByNumber(number)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Matchday " + number + " not found"));
        return playerMatchStatsRepository.findByMatch_Matchday(matchday).stream()
                .map(PlayerMatchResultDto::from)
                .toList();
    }
}
