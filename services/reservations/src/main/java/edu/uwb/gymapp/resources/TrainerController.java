package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/reservation-service/api/v1")
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    @RequestMapping(value="/trainers", params="email", method= RequestMethod.GET)
    public Trainer getTrainer(@RequestParam("email") String email) {
        return trainerService.getTrainer(email);
    }

    @RequestMapping(value="/trainer/{id}")
    public Trainer getTrainer(@PathVariable Long id) {
        return trainerService.getTrainer(id);
    }

    @RequestMapping(method= RequestMethod.POST, value="/trainer/signup")
    public Trainer addTrainer(@RequestBody Trainer trainer) {
        try {
            Trainer newTrainer = trainerService.addTrainer(trainer);
            return newTrainer;
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Failed to create user profile. Review your information and try again.");
        }
    }

    @RequestMapping(method=RequestMethod.PUT, value="/trainer/{id}")
    public void updateMember(@RequestBody Trainer trainer) {
        trainerService.updateTrainer(trainer);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/trainer/{id}")
    public void deleteMember(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
    }
}