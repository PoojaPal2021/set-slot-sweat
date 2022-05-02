package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
public class ReservationController {

    @Autowired
    public ReservationService reservationService;

    @RequestMapping("/")
    public String welcome(Principal principal) {
        return "Welcome: " + principal.getName();
    }

    @RequestMapping(value="/reservations", params="email", method = RequestMethod.GET)
    public List<Reservation> getAllReservations(@RequestParam("email") String email, Principal principal) {

        // Verify that we are accessing the reservations for the current user
        if (principal == null || !principal.getName().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not allowed for: " + email);
        }

        return reservationService.getAllReservations(email);
    }

    @RequestMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @RequestMapping(value="/reservations", params = "email", method = RequestMethod.POST)
    public void addReservation(@RequestParam("email") String email,
                               @RequestBody Session session,
                               Principal principal) {

        if (principal == null || !principal.getName().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not allowed for: " + email);
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
    }

//    private void authenticate(Principal principal) {
//
//    }
}
