package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.ReservationRepository;
import edu.uwb.gymapp.models.Session;
import edu.uwb.gymapp.models.SessionRepository;
import edu.uwb.gymapp.workoutsession.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.*;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WebApplicationContext appContext;

    @Autowired
    private RestTemplate restTemplate;

    public List<Reservation> getAllReservations(String memberEmail) {
        List<Reservation> reservations = new ArrayList<>();
        Set<Long> idSet = new HashSet<>();
        LocalDateTime currentTime = LocalDateTime.now();
        reservationRepository
                .findByScheduledTimeGreaterThanAndMemberEmail(currentTime, memberEmail)
                .forEach(r -> {r.setBooked(true); reservations.add(r); idSet.add(r.getSession().getId());});

        // Add non booked sessions to list with isBooked set to false. We do this so we have one list with all
        // available and booked sessions together.
        // Todo: Split the sessions logic into a separate microservice. Then, we will call it using a RestTemplate.
        Repositories repositories = new Repositories(appContext);
        List<Session> sessions = new ArrayList<>();
        JpaRepository<Session, Long> sessionRepository = (JpaRepository) repositories.getRepositoryFor(Session.class).get();
        sessionRepository.findAll().forEach(session -> {
                if (!idSet.contains(session.getId())) {
                    Reservation reservation = new Reservation();
                    reservation.setSession(session);
                    reservation.setBooked(false);
                    reservations.add(reservation);
                }
            }
        );

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

    public void deleteReservation(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }
}
