package org.generation.italy.LeagueSim.repository;

import org.generation.italy.LeagueSim.domain.Player;
import org.generation.italy.LeagueSim.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeam(Team team);

    @Query("select p from Player p join fetch p.team")
    List<Player> findAllWithTeam();

    @Query("select p from Player p join fetch p.team where p.id = :id")
    Optional<Player> findByIdWithTeam(Long id);
}
