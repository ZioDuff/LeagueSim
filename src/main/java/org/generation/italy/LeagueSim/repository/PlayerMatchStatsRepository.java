package org.generation.italy.LeagueSim.repository;

import org.generation.italy.LeagueSim.domain.Matchday;
import org.generation.italy.LeagueSim.domain.PlayerMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStats, Long> {

    List<PlayerMatchStats> findByMatch_Matchday(Matchday matchday);
}
