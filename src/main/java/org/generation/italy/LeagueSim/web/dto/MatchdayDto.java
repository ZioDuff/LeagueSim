package org.generation.italy.LeagueSim.web.dto;

import org.generation.italy.LeagueSim.domain.Matchday;

import java.time.LocalDate;

public record MatchdayDto(int number, LocalDate date, boolean closed) {
    public static MatchdayDto from(Matchday matchday) {
        return new MatchdayDto(matchday.getNumber(), matchday.getDate(), matchday.isClosed());
    }
}
