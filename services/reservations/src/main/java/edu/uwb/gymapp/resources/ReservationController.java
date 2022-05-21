package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.Session;
import edu.uwb.gymapp.workoutsession.SessionService;
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

    @RequestMapping(value="/member/login", method = RequestMethod.POST)
    public List<Reservation> login(@RequestBody MemberDetails memberDetails) {

        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(memberDetails.getUsername(),
                                                        memberDetails.getPassword());
        try {
            authentication = authenticationManager.authenticate(authReq);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to authenticate user. Try again.");
        }

        List<Reservation> reservations = reservationService.getAllReservations(memberDetails.getUsername());

        return reservations;
    }

    @RequestMapping(value="/member/logout", method = RequestMethod.POST)
    public void logout() {
        authentication = null;
    }

    @RequestMapping(value="/session/book/{sessionId}", params = "email", method = RequestMethod.POST)
    public List<Reservation> addReservation(@RequestParam("email") String email,
                               @PathVariable Long sessionId) {
        // Authenticate
        if (authentication == null || !authentication.getName().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not allowed for: " + email);
        }

        Session session;

        try {
            session = sessionService.getSession(sessionId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session ID couldn't be found: " + sessionId);
        }

        Reservation reservation = new Reservation();
        reservation.setMemberEmail(email);
        reservation.setSession(session);

        String status;
        try {
            status = reservationService.addReservation(reservation);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Reservation failed: " + ex.getCause().getCause().getMessage());
        }

        if (!status.equals("SUCCESS")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, status);
        }
        List<Reservation> reservations = reservationService.getAllReservations(email);
        return reservations;
    }

    @RequestMapping(value="/session/cancel/{reservationId}", method=RequestMethod.DELETE)
    public String cancelReservation(@RequestParam("email") String email,
                                    @PathVariable Long reservationId) {
        // Authenticate
        if (authentication == null || !authentication.getName().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not allowed for: " + email);
        }

        try {
            reservationService.deleteReservation(reservationId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reservation exists for id: " + reservationId);
        }

        return "Successfully cancelled reservation.";
    }

    @RequestMapping(value="/reservations", params="email", method = RequestMethod.GET)
    public List<Reservation> getAllReservations(@RequestParam("email") String email) {
        // Verify that we are accessing the reservations for the current user
        if (authentication == null || !authentication.getName().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access not allowed for: " + email);
        }

        return reservationService.getAllReservations(email);
    }

    @RequestMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }
}
