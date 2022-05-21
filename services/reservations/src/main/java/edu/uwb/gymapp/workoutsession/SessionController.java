package edu.uwb.gymapp.workoutsession;

import edu.uwb.gymapp.models.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SessionController {

    Logger logger = LoggerFactory.getLogger(SessionController.class);

    @Autowired
    private SessionService sessionService;

    /**
     * Retrieve the list of available sessions
     * @return  The list of available sessions
     */
    @RequestMapping("/sessions")
    public List<Session> getAllSessions() {
        logger.info("List of available sessions has been retrieved");
        return sessionService.getAllSessions();
    }
}
