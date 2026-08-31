package org.generation.italy.LeagueSim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "matchday")
public class Matchday {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_matchday_id")
    @SequenceGenerator(name = "seq_matchday_id", sequenceName = "seq_matchday_id", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private int number;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean closed;

    protected Matchday() {
    }

    public Matchday(int number, LocalDate date) {
        this.number = number;
        this.date = date;
        this.closed = false;
    }

    public Long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isClosed() {
        return closed;
    }

    public void markClosed() {
        this.closed = true;
    }
}
