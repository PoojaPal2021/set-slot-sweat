package edu.uwb.gymapp.usermanagement;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MemberRepository extends CrudRepository<Member, Long> {

    public Member findByUsername(String username);

}
