package edu.uwb.gymapp.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReservationController {

    @Autowired
    public ReservationService reservationService;

    @RequestMapping("/reservations")
    public List<Reservation> getAllReservations(@RequestParam("email") String memberEmail) {
        return reservationService.getAllReservations(memberEmail);
    }
}
