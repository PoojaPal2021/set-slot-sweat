package edu.uwb.gymapp.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations(String memberEmail) {
        List<Reservation> reservations = new ArrayList<>();
        reservationRepository.findByMemberEmail(memberEmail).forEach(reservations::add);
        return reservations;
    }

}
