package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Loads the Gym Member details as UserDetails for authentication purposes
 */
public class MemberDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Load the Member information as UserDetails for authentication purposes
     * @param email The email of the Gym Member
     * @return  The UserDetails for the given member
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("In Service...");
        Optional<Member> member = memberRepository.findByEmail(email);
        member.orElseThrow(() -> new UsernameNotFoundException("user not found: " + email));
        return member.map(MemberDetails::new).get();
    }
}
