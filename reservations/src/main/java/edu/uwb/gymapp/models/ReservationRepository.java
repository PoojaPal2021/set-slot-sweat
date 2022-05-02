package edu.uwb.gymapp.models;

import edu.uwb.gymapp.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

//    public List<Reservation> findByMemberId(Long memberId);

    public List<Reservation> findByMemberEmail(String email);
    public List<Reservation> findByScheduledTimeAndMemberEmail(LocalDateTime scheduledTime, String email);
    public List<Reservation> findByScheduledTimeGreaterThanAndMemberEmail(LocalDateTime scheduledTime, String email);

    public List<Reservation> findByScheduledTimeGreaterThanAndSessionId(LocalDateTime scheduledTime, Long id);
//    @Query("SELECT)
//    public List<Reservation> getAllFutureReservations();
}
