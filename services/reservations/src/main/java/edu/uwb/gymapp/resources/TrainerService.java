package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Trainer;
import edu.uwb.gymapp.models.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainerService {
    @Autowired
    private TrainerRepository trainerRepository;

    public List<Trainer> getAllTrainers() {
        List<Trainer> trainers = new ArrayList<>();
        trainerRepository.findAll().forEach(trainers::add);
        return trainers;
    }

    public Trainer getTrainer(String email) {
        return trainerRepository.findByEmail(email).get();
    }

    public Trainer getTrainer(Long id) {
        return trainerRepository.findById(id).get();
    }

    public Trainer addTrainer(Trainer trainer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(trainer.getPassword());
        trainer.setPassword(encodedPassword);
        return trainerRepository.save(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(trainer.getPassword());
        trainer.setPassword(encodedPassword);
        return trainerRepository.save(trainer);
    }

    public void deleteTrainer(Long id) {
        trainerRepository.deleteById(id);
    }
}
