package org.generation.italy.LeagueSim.web;

import org.generation.italy.LeagueSim.service.MatchdaySimulationService;
import org.generation.italy.LeagueSim.web.dto.MatchdayDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/admin/matchdays")
public class AdminSimulationController {

    private final MatchdaySimulationService matchdaySimulationService;

    public AdminSimulationController(MatchdaySimulationService matchdaySimulationService) {
        this.matchdaySimulationService = matchdaySimulationService;
    }

    @PostMapping("/{number}/simulate")
    public MatchdayDto simulate(@PathVariable int number) {
        try {
            return MatchdayDto.from(matchdaySimulationService.simulate(number));
        } catch (java.util.NoSuchElementException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }
}
