package edu.uwb.gymapp.schedule;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    public List<Reservation> findByMemberId(Long memberId);

    public List<Reservation> findByMemberEmail(String email);
}
