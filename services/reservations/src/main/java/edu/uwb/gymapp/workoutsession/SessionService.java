package edu.uwb.gymapp.workoutsession;

import edu.uwb.gymapp.models.Session;
import edu.uwb.gymapp.models.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for interfacing with the Session table in the database
 */
@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    /**
     * Retrieves all sessions available in the database
     * @return  List of sessions available in the database
     */
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        sessionRepository.findAll().forEach(sessions::add);
        return sessions;
    }

    /**
     * Retrieves the session with the given id
     * @param id    The id of the session to be retrieve
     * @return  The session with the corresponding id
     */
    public Session getSession(Long id) {
        return sessionRepository.findById(id).get();
    }
}
