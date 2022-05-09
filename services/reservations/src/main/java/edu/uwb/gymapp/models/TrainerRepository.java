package edu.uwb.gymapp.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    public Optional<Trainer> findByEmail(String email);
}
