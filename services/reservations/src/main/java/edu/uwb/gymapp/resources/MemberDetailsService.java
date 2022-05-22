package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class MemberDetailsService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(MemberDetailsService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String endpointURL = "http://localhost:18001/user-management/api/v1/members?email=" + email;

        Member member = restTemplate.getForObject(endpointURL, Member.class);

        if (member == null) {
            logger.debug("User not found: " + email);
            throw new UsernameNotFoundException("User not found: " + email);
        }

        logger.info("Authenticated user with email: " + email);
        return new MemberDetails(member.getEmail(), member.getPassword());
    }
}
