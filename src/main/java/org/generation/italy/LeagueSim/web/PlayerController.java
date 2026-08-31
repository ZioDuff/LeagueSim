package org.generation.italy.LeagueSim.web;

import org.generation.italy.LeagueSim.domain.Player;
import org.generation.italy.LeagueSim.repository.PlayerRepository;
import org.generation.italy.LeagueSim.web.dto.PlayerDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<PlayerDto> listPlayers() {
        return playerRepository.findAllWithTeam().stream().map(PlayerDto::from).toList();
    }

    @GetMapping("/{id}")
    public PlayerDto getPlayer(@PathVariable Long id) {
        Player player = playerRepository.findByIdWithTeam(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Player " + id + " not found"));
        return PlayerDto.from(player);
    }
}
