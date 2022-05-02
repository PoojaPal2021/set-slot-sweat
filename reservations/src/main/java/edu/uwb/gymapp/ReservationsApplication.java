package edu.uwb.gymapp;

import edu.uwb.gymapp.models.ReservationRepository;
import edu.uwb.gymapp.models.SessionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = {SessionRepository.class,
											 ReservationRepository.class})
public class ReservationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationsApplication.class, args);
	}


}
