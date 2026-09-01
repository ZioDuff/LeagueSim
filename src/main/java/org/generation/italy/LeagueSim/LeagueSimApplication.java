package org.generation.italy.LeagueSim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeagueSimApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeagueSimApplication.class, args);
	}

}
