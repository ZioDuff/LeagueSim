package org.generation.italy.LeagueSim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_player_id")
    @SequenceGenerator(name = "seq_player_id", sequenceName = "seq_player_id", allocationSize = 1)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "shirt_number", nullable = false)
    private int shirtNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 1)
    private Position position;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private boolean injured;

    protected Player() {
    }

    public Player(String firstName, String lastName, Team team, int shirtNumber, Position position, int price, boolean injured) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
        this.shirtNumber = shirtNumber;
        this.position = position;
        this.price = price;
        this.injured = injured;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Team getTeam() {
        return team;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public Position getPosition() {
        return position;
    }

    public int getPrice() {
        return price;
    }

    public boolean isInjured() {
        return injured;
    }
}
