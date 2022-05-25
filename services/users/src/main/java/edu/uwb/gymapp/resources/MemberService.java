package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for interfacing with the Member table in the database
 */
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Retrieves all the Gym Members
     * @return  The List of Gym Members
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        memberRepository.findAll().forEach(members::add);
        return members;
    }

    /**
     * Retrieves the Gym Member with the given ID
     * @param id    The ID of the Gym member
     * @return  The Gym Member with the given ID
     */
    public Member getMember(Long id) {
        return memberRepository.findById(id).get();
    }

    /**
     * Retrieve the Gym Member with the given email address
     * @param email The email address of the Gym Member
     * @return  The Gym Member with the given ID
     */
    public Member getMember(String email) {
        return memberRepository.findByEmail(email).get();
    }

    /**
     * Adds a new member to the database
     * @param member    The member information
     * @return  The newly added member
     */
    public Member addMember(Member member) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        return memberRepository.save(member);
    }

    /**
     * Updates the Member information in the database
     * @param member    The information to be updated
     */
    public void updateMember(Member member) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        memberRepository.save(member);
    }

    /**
     * Deletes a Member from the database
     * @param id    The ID of the Gym Member to be deleted
     */
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
