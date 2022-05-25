package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Trainer;
import edu.uwb.gymapp.models.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for interfacing with the Trainer table in the database
 */
@Service
public class TrainerService {
    @Autowired
    private TrainerRepository trainerRepository;

    /**
     * Retrieves the list of trainers from the database
     * @return  The list of trainers
     */
    public List<Trainer> getAllTrainers() {
        List<Trainer> trainers = new ArrayList<>();
        trainerRepository.findAll().forEach(trainers::add);
        return trainers;
    }

    /**
     * Retrieves the trainer with the given email
     * @param email The trainer email address
     * @return  The trainer with the given email
     */
    public Trainer getTrainer(String email) {
        return trainerRepository.findByEmail(email).get();
    }

    /**
     * Retrieves the trainer with the given ID
     * @param id    The trainer ID
     * @return  The trainer with the given ID
     */
    public Trainer getTrainer(Long id) {
        return trainerRepository.findById(id).get();
    }

    /**
     * Add a new trainer to the database
     * @param trainer   The new trainer
     * @return  The trainer that was saved to the database
     */
    public Trainer addTrainer(Trainer trainer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(trainer.getPassword());
        trainer.setPassword(encodedPassword);
        return trainerRepository.save(trainer);
    }

    /**
     * Updates the trainer information in the database
     * @param trainer   The trainer information to be updated
     * @return  The trainer as it was saved to the database
     */
    public Trainer updateTrainer(Trainer trainer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(trainer.getPassword());
        trainer.setPassword(encodedPassword);
        return trainerRepository.save(trainer);
    }

    /**
     * Deletes a trainer from the database
     * @param id    The ID of the trainer to be deleted
     */
    public void deleteTrainer(Long id) {
        trainerRepository.deleteById(id);
    }
}
