package edu.uwb.gymapp.workoutsession;

import edu.uwb.gymapp.models.Session;
import edu.uwb.gymapp.models.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        sessionRepository.findAll().forEach(sessions::add);
        return sessions;
    }
}
