package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_MEMBER_ENDPOINT = "http://localhost:18001/user-management/api/v1/members?email=";

    Logger logger = LoggerFactory.getLogger(ReservationService.class);

    public List<Reservation> getAllReservations(String memberEmail) {
        List<Reservation> reservations = new ArrayList<>();
        Set<Long> idSet = new HashSet<>();
        LocalDateTime currentTime = LocalDateTime.now();
        DayOfWeek today = currentTime.getDayOfWeek();
        LocalTime now = LocalTime.now();
        reservationRepository
                .findByScheduledTimeGreaterThanAndMemberEmailOrderByScheduledTimeAsc(currentTime, memberEmail)
                .forEach(r -> {
                    if (!r.getSession().getDayOfWeek().equals(today) || !(r.getSession().getStartTime().compareTo(now) <= 0)) {
                        r.setBooked(true);
                        reservations.add(r);
                        idSet.add(r.getSession().getId());
                    }
                });

        // Add non booked sessions to list with isBooked set to false. We do this so we have one list with all
        // available and booked sessions together.
        for (Session session : sessionRepository.findAll()) {
            if (!idSet.contains(session.getId()) &&
                    !(session.getDayOfWeek().equals(today) && session.getStartTime().compareTo(now) <= 0)) {
                Reservation reservation = new Reservation();
                reservation.setSession(session);
                reservation.setBooked(false);
                reservations.add(reservation);
            }
        }

        reservations.sort(Comparator.comparing(Reservation::getSession));
        return reservations;
    }

    public List<Reservation> getAllBookedReservations(String memberEmail) {
        List<Reservation> reservations = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        reservationRepository
                .findByScheduledTimeGreaterThanAndMemberEmailOrderByScheduledTimeAsc(now, memberEmail)
                .forEach(reservation -> {
                    reservation.setBooked(true);
                    reservations.add(reservation);
                });

        return reservations;
    }

    /**
     * Retrieve a summary of the reservation history of the given member, since they joined the gym
     * @param memberEmail   email The email of the gym member
     * @return  The history of the member's reservations since they joined the gym
     */
    public SessionHistory getHistory(String memberEmail) {
        Member member = restTemplate.getForObject(GET_MEMBER_ENDPOINT + memberEmail, Member.class);
        if (member == null) {
            logger.debug("User not found: " + memberEmail);
            throw new UsernameNotFoundException("User not found: " + memberEmail);
        }

        // Calculate the total number of available sessions per workout type
        LocalDateTime joinDate = member.getJoinDate();
        LocalDateTime now = LocalDateTime.now();
        long totalWeeks = ChronoUnit.WEEKS.between(joinDate, now);
        Map<String, Integer> workoutMap = new HashMap<>();
        List<Session> sessions = new ArrayList<>();
        sessionRepository.findAll().forEach(session -> {
            Integer curInt = workoutMap.getOrDefault(session.getWorkout().getName(), 0);
            workoutMap.put(session.getWorkout().getName(), curInt + 1);
            sessions.add(session);
        });

        // Get the total number of sessions attended until now. Add them to the history
        SessionHistory sessionHistory = new SessionHistory();
        sessionHistory.setJoinDate(joinDate.toLocalDate());
        sessionHistory.setWeeksSinceJoined(totalWeeks);
        Map<String, Integer> attendedWorkouts = new HashMap<>();
        reservationRepository
                .findByScheduledTimeBeforeAndAndMemberEmail(now, memberEmail)
                .forEach(reservation -> {
                    Integer curInt = attendedWorkouts.getOrDefault(reservation.getSession().getWorkout().getName(), 0);
                    attendedWorkouts.put(reservation.getSession().getWorkout().getName(), curInt + 1);
                    sessionHistory.setTotalAttended(sessionHistory.getTotalAttended() + 1);
                });


        // Calculate total number of session available
        sessionHistory.setTotalSessions(sessions.size() * totalWeeks);
        for (String name : workoutMap.keySet()) {
            WorkoutHistory workoutHistory = new WorkoutHistory();
            workoutHistory.setName(name);
            workoutHistory.setTotal(workoutMap.get(name) * totalWeeks);
            workoutHistory.setAttended(attendedWorkouts.getOrDefault(name, 0));
            sessionHistory.addWorkoutHistory(workoutHistory);
        }

        return sessionHistory;
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
                .findByScheduledTimeGreaterThanAndSessionId(LocalDateTime.now(), sessionId);
        System.out.println(curBookings);
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
