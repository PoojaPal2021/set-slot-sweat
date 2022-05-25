package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.ResponseMessage;
import edu.uwb.gymapp.models.Session;
import edu.uwb.gymapp.models.SessionHistory;
import edu.uwb.gymapp.workoutsession.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservation-service/api/v1")
public class ReservationController {

    Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    public ReservationService reservationService;

    @Autowired
    public SessionService sessionService;

    private Authentication authentication;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping("/")
    public String welcome(Principal principal) {
        return "Welcome: " + principal.getName();
    }

    /**
     * Authenticates member credentials and sets an authentication token for future requests
     * @param memberDetails The member details with its username (email) and password
     * @return  The list of reservations for the given member
     */
    @RequestMapping(value="/member/login", method = RequestMethod.POST)
    public List<Reservation> login(@RequestBody MemberDetails memberDetails) {

        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(memberDetails.getUsername(),
                                                        memberDetails.getPassword());
        try {
            authentication = authenticationManager.authenticate(authReq);
        } catch (Exception e) {
            logger.debug("Unable to authenticate user with email: " + memberDetails.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to authenticate user. Try again.");
        }

        List<Reservation> reservations = reservationService.getAllReservations(memberDetails.getUsername());

        logger.info("Successful login for Member: " + memberDetails.getUsername());
        return reservations;
    }

    /**
     * Log member out of the service API
     */
    @RequestMapping(value="/member/logout", method = RequestMethod.POST)
    public void logout() {
        String username = "";
        if (authentication != null) {
            username = authentication.getName();
        }

        authentication = null;
        logger.info("Successful logout for Member: " + username);
    }

    /**
     * Books the session with the given id for the member with the given email
     * @param email The email of the member booking the session
     * @param sessionId The id of the session to be booked
     * @return  The list of sessions currently booked and not booked
     */
    @RequestMapping(value="/session/book/{sessionId}", params = "email", method = RequestMethod.POST)
    public List<Reservation> addReservation(@RequestParam("email") String email,
                               @PathVariable Long sessionId) {
        // Authenticate
        if (authentication == null || !authentication.getName().equals(email)) {
            logger.debug("Blocked booking access to user: " + email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not allowed for: " + email);
        }

        Session session;

        try {
            session = sessionService.getSession(sessionId);
        } catch (NoSuchElementException ex) {
            logger.debug("Session ID couldn't be found: " + sessionId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session ID couldn't be found: " + sessionId);
        }

        Reservation reservation = new Reservation();
        reservation.setMemberEmail(email);
        reservation.setSession(session);

        String status;
        try {
            status = reservationService.addReservation(reservation);
        } catch (RuntimeException ex) {
            logger.debug("Failed to book session with id: " + sessionId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Failed: " + ex.getCause().getCause().getMessage());
        }

        if (!status.equals("SUCCESS")) {
            logger.debug(status + ": Failed to book session with id " + sessionId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, status);
        }
        List<Reservation> reservations = reservationService.getAllReservations(email);

        logger.info("Successfully booked Session with ID: " + sessionId);
        return reservations;
    }

    /**
     * Cancel the reservation with the given id and member email address
     * @param email The email address of the member cancelling the reservation
     * @param reservationId The id of the reservation to be cancelled
     * @return  Success or failure message string
     */
    @RequestMapping(value="/session/cancel/{reservationId}", method=RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage cancelReservation(@RequestParam("email") String email,
                                             @PathVariable Long reservationId) {
        // Authenticate
        if (authentication == null || !authentication.getName().equals(email)) {
            logger.debug("Blocked reservation cancelling access to user: " + email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not allowed for: " + email);
        }

        try {
            reservationService.deleteReservation(reservationId);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug("No reservation exists for id: " + reservationId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reservation exists for id: " + reservationId);
        }

        logger.info("Successfully cancelled reservation with id: " + reservationId);
        ResponseMessage responseMessage = new ResponseMessage("Successfully cancelled reservation.");
        return responseMessage;
    }

    /**
     * Retrieve the list of reservations for the given member email
     * @param email The email address of the member retrieving the list of reservations
     * @return The list of reservations for the given member
     */
    @RequestMapping(value="/reservations", params="email", method = RequestMethod.GET)
    public List<Reservation> getAllReservations(@RequestParam("email") String email) {
        // Verify that we are accessing the reservations for the current user
        if (authentication == null || !authentication.getName().equals(email)) {
            logger.debug("Blocked reservations access to user: " + email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access not allowed for: " + email);
        }

        logger.info("All reservations have been retrieved for user: " + email);
        return reservationService.getAllReservations(email);
    }

    /**
     * Retrieve the list of reservations booked by the given member email
     * @param email The email address of the member retrieving the list of reservations
     * @return The list of reservations booked by the given member
     */
    @RequestMapping(value="/reservations/booked", params="email", method = RequestMethod.GET)
    public List<Reservation> getAllBookedReservations(@RequestParam("email") String email) {
        // Verify that we are accessing the reservations for the current user
        if (authentication == null || !authentication.getName().equals(email)) {
            logger.debug("Blocked reservations access to user: " + email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access not allowed for: " + email);
        }

        logger.info("All reservations have been retrieved for user: " + email);
        return reservationService.getAllBookedReservations(email);
    }

    /**
     * Retrieve a summary of the reservation history of the given member, since they joined the gym
     * @param email The email of the gym member
     * @return  The history of the member's reservations since they joined the gym
     */
    @RequestMapping(value="/reservations/history", params="email", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionHistory getReservationHistory(@RequestParam("email") String email) {
        // Verify that we are accessing the reservations for the current user
        if (authentication == null || !authentication.getName().equals(email)) {
            logger.debug("Blocked reservations access to user: " + email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access not allowed for: " + email);
        }

        logger.info("Reservation history has been retrieved for user: " + email);
        return reservationService.getHistory(email);
    }


    /**
     * Retrieve the list of reservations
     * @return The list of reservations
     */
    @RequestMapping("/reservations")
    public List<Reservation> getAllReservations() {
        logger.info("All reservations have been retrieved by user: " + authentication.getName());
        return reservationService.getAllReservations();
    }
}
