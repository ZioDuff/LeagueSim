package org.generation.italy.LeagueSim.repository;

import org.generation.italy.LeagueSim.domain.Matchday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchdayRepository extends JpaRepository<Matchday, Long> {

    Optional<Matchday> findByNumber(int number);

    List<Matchday> findByDateLessThanEqualAndClosedFalse(LocalDate date);
}
