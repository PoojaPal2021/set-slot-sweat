package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
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

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        reservationRepository.findAll().forEach(reservations::add);
        return reservations;
    }

    public String addReservation(Reservation reservation) {
        // calculate scheduled date and time from current date and the session start time
        DayOfWeek sessionDay = reservation.getSession().getDayOfWeek();
        LocalDate bookDate = LocalDate.now();

        // If the start time is after the local time we book for the same day, else we book the session
        // for the following week
        LocalDate scheduledDate;
        LocalTime startTime = reservation.getSession().getStartTime();
        int comparedTime = startTime.compareTo(LocalTime.now());
        if ( comparedTime > 0) {
            scheduledDate = bookDate.with(TemporalAdjusters.nextOrSame(sessionDay));
        } else {
            scheduledDate = bookDate.with(TemporalAdjusters.next(sessionDay));
        }

        LocalDateTime scheduledTime = LocalDateTime.of(scheduledDate, startTime);
        reservation.setScheduledTime(scheduledTime);

        // Check that the scheduled time doesn't conflict with a session already booked
        List<Reservation> conflictReservations = reservationRepository
                    .findByScheduledTimeAndMemberEmail(scheduledTime, reservation.getMemberEmail());
        System.out.println(conflictReservations);
        if (!conflictReservations.isEmpty()) {
            return "This session has already been booked";
        }

        // Check that session still has capacity
        Long sessionId = reservation.getSession().getId();
        Integer sessionCapacity = reservation.getSession().getCapacity();
        List<Reservation> curBookings = reservationRepository
                .findByScheduledTimeGreaterThanAndSessionId(scheduledTime, sessionId);
        if (curBookings.size() >= sessionCapacity) {
            return "Session is at full capacity";
        }

        reservationRepository.save(reservation);
        return "SUCCESS";
    }

}
