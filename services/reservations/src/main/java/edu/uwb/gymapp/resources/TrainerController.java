package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/reservation-service/api/v1")
public class TrainerController {

    Logger logger = LoggerFactory.getLogger(TrainerController.class);

    @Autowired
    private TrainerService trainerService;

    /**
     * Retrieve information about the trainer with the given email address
     * @param email The trainer email address
     * @return  The information about the trainer with the given email address
     */
    @RequestMapping(value="/trainers", params="email", method= RequestMethod.GET)
    public Trainer getTrainer(@RequestParam("email") String email) {
        logger.info("Retrieved information for trainer: " + email);
        return trainerService.getTrainer(email);
    }

    /**
     * Retrieve information about the trainer with the given id
     * @param id    The trainer id
     * @return  The information about the trainer with the given id
     */
    @RequestMapping(value="/trainer/{id}")
    public Trainer getTrainer(@PathVariable Long id) {
        logger.info("Retrieved information for trainer with id: " + id);
        return trainerService.getTrainer(id);
    }

    /**
     * Add a new trainer to the database. The trainer information must be given in the request body in JSON format
     * @param trainer   JSON object with all trainer information
     * @return  Success or failure message string
     */
    @RequestMapping(method= RequestMethod.POST, value="/trainer/signup")
    public Trainer addTrainer(@RequestBody Trainer trainer) {
        try {
            Trainer newTrainer = trainerService.addTrainer(trainer);
            logger.info("Successfully created profile for trainer with email: " + trainer.getEmail());
            return newTrainer;
        } catch (DataIntegrityViolationException ex) {
            logger.debug("Sign up failed. Email already exists in the system: " + trainer.getEmail());
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sign Up Failed. Email already exists in the system: " + trainer.getEmail());
        } catch (RuntimeException ex) {
            logger.debug("Failed to create trainer profile.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Failed to create user profile. Review your information and try again.");
        }
    }

    /**
     * Authenticates member credentials and sets an authentication token for future requests
     * @param memberDetails The member details with its username (email) and password
     * @return  The list of reservations for the given member
     */
//    @RequestMapping(value="/trainer/login", method = RequestMethod.POST)
//    public List<Reservation> login(@RequestBody MemberDetails memberDetails) {
//
//        UsernamePasswordAuthenticationToken authReq =
//                new UsernamePasswordAuthenticationToken(memberDetails.getUsername(),
//                        memberDetails.getPassword());
//        try {
//            authentication = authenticationManager.authenticate(authReq);
//        } catch (Exception e) {
//            logger.debug("Unable to authenticate user with email: " + memberDetails.getUsername());
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to authenticate user. Try again.");
//        }
//
//        List<Reservation> reservations = reservationService.getAllReservations(memberDetails.getUsername());
//
//        logger.info("Successful login for Member: " + memberDetails.getUsername());
//        return reservations;
//    }


    /**
     * Update information for the given trainer
     * @param trainer   JSON object with all trainer information
     */
    @RequestMapping(method=RequestMethod.PUT, value="/trainer/{id}")
    public void updateTrainer(@RequestBody Trainer trainer) {
        trainerService.updateTrainer(trainer);
    }

    /**
     * Delete trainer with the given id
     * @param id    The id of the trainer to be deleted
     */
    @RequestMapping(method=RequestMethod.DELETE, value="/trainer/{id}")
    public void deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
    }
}