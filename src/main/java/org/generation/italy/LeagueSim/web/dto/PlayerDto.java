package org.generation.italy.LeagueSim.web.dto;

import org.generation.italy.LeagueSim.domain.Player;

public record PlayerDto(
        Long id,
        String firstName,
        String lastName,
        String realTeamName,
        int shirtNumber,
        String position,
        int price,
        boolean injured
) {
    public static PlayerDto from(Player player) {
        return new PlayerDto(
                player.getId(),
                player.getFirstName(),
                player.getLastName(),
                player.getTeam().getName(),
                player.getShirtNumber(),
                player.getPosition().name(),
                player.getPrice(),
                player.isInjured()
        );
    }
}
