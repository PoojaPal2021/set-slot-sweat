package edu.uwb.gymapp.models;

import edu.uwb.gymapp.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

}
