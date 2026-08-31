package org.generation.italy.LeagueSim.repository;

import org.generation.italy.LeagueSim.domain.Match;
import org.generation.italy.LeagueSim.domain.Matchday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByMatchday(Matchday matchday);

    long countByMatchday(Matchday matchday);
}
