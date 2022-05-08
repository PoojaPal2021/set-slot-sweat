package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("reservation-service/api/v1")
public class ReservationController {

    @Autowired
    public ReservationService reservationService;


    private Authentication authentication;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping("/")
    public String welcome(Principal principal) {
        return "Welcome: " + principal.getName();
    }

    @RequestMapping(value="/member/login", method = RequestMethod.POST)
    public List<Reservation> login(@RequestBody MemberDetails memberDetails) {

        System.out.println("Authenticating");
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



    @RequestMapping(value="/session/book", params = "email", method = RequestMethod.POST)
    public void addReservation(@RequestParam("email") String email,
                               @RequestBody Session session) {
        if (authentication == null || !authentication.getName().equals(email)) {
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
