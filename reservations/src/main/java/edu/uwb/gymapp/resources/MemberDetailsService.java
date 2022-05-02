package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

public class MemberDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        RestTemplate restTemplate = new RestTemplate();
        System.out.println("In detail service ---");
        System.out.println(email);
        Member member = restTemplate.getForObject("http://localhost:18001/members?email=" + email, Member.class);

        if (member == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        return new MemberDetails(member);
    }
}
