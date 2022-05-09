package edu.uwb.gymapp.usermanagement;

import edu.uwb.gymapp.models.Trainer;
import edu.uwb.gymapp.resources.TrainerController;
import edu.uwb.gymapp.resources.TrainerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TrainerController trainerController;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
        assertThat(trainerController).isNotNull();
    }

    @Test
    public void trainerSignupShouldSucceed() {
        String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/trainer/signup";

        // GIVEN
        Trainer trainer = new Trainer();
        trainer.setUsername("mrMuscle");
        trainer.setEmail("mrMuscle@gymapp.com");
        trainer.setPassword("strongPassword");

        // WHEN
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Trainer> request = new HttpEntity<>(trainer, headers);
        // Sign the trainer up
        ResponseEntity<Trainer> response = this.restTemplate.postForEntity(endpoint, request, Trainer.class);

        // THEN
        Trainer responseTrainer = response.getBody();
        Assertions.assertEquals(trainer.getUsername(), responseTrainer.getUsername());
        Assertions.assertEquals(trainer.getEmail(), responseTrainer.getEmail());

        // clean up
        if (responseTrainer.getId() != null) {
            trainerService.deleteTrainer(responseTrainer.getId());
        }
    }

    @Test
    public void trainerSignUpWithoutEmailShouldFail() {
        String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/trainer/signup";

        // GIVEN
        Trainer trainer = new Trainer();
        trainer.setUsername("mrMuscle");
        trainer.setPassword("strongPassword");

        // WHEN
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Trainer> request = new HttpEntity<>(trainer, headers);
        // Sign the trainer up
        ResponseEntity<Trainer> response = this.restTemplate.postForEntity(endpoint, request, Trainer.class);

        // THEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // clean up
        Trainer responseTrainer = response.getBody();
        if (responseTrainer.getId() != null) {
            trainerService.deleteTrainer(responseTrainer.getId());
        }
    }

    @Test
    public void trainerSignUpWithoutUsernameShouldFail() {
        String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/trainer/signup";

        // GIVEN
        Trainer trainer = new Trainer();
        trainer.setEmail("mrMuscle@gymapp.com");
        trainer.setPassword("strongPassword");

        // WHEN
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Trainer> request = new HttpEntity<>(trainer, headers);
        // Sign the trainer up
        ResponseEntity<Trainer> response = this.restTemplate.postForEntity(endpoint, request, Trainer.class);

        // THEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // clean up
        Trainer responseTrainer = response.getBody();
        if (responseTrainer.getId() != null) {
            trainerService.deleteTrainer(responseTrainer.getId());
        }
    }

    @Test
    public void trainerSignUpWithoutPasswordShouldFail() {
        String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/trainer/signup";

        // GIVEN
        Trainer trainer = new Trainer();
        trainer.setUsername("mrMuscle");
        trainer.setEmail("mrMuscle@gymapp.com");

        // WHEN
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Trainer> request = new HttpEntity<>(trainer, headers);
        // Sign the trainer up
        ResponseEntity<Trainer> response = this.restTemplate.postForEntity(endpoint, request, Trainer.class);

        // THEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // clean up
        Trainer responseTrainer = response.getBody();
        if (responseTrainer.getId() != null) {
            trainerService.deleteTrainer(responseTrainer.getId());
        }
    }

    @Test
    public void trainerSignupTwiceWithSameEmailShouldFail() {
        String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/trainer/signup";

        // GIVEN
        Trainer trainer = new Trainer();
        trainer.setUsername("mrMuscle");
        trainer.setEmail("mrMuscle@gymapp.com");
        trainer.setPassword("strongPassword");

        // WHEN
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Trainer> request = new HttpEntity<>(trainer, headers);
        // Sign the trainer up
        ResponseEntity<Trainer> goodResponse = this.restTemplate.postForEntity(endpoint, request, Trainer.class);

        // AND: First signup succeeds
        Trainer responseTrainer = goodResponse.getBody();
        Assertions.assertEquals(trainer.getUsername(), responseTrainer.getUsername());
        Assertions.assertEquals(trainer.getEmail(), responseTrainer.getEmail());

        // THEN: Second attempt to signup with same email should fail
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("moreMuscle");
        trainer2.setEmail("mrMuscle@gymapp.com");
        trainer2.setPassword("whatever");
        request = new HttpEntity<>(trainer2, headers);
        ResponseEntity<Trainer> badResponse = this.restTemplate.postForEntity(endpoint, request, Trainer.class);

        // Failure: returns a BAD_REQUEST status
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, badResponse.getStatusCode());

        // clean up
        if (responseTrainer.getId() != null) {
            trainerService.deleteTrainer(responseTrainer.getId());
        }
    }
}
