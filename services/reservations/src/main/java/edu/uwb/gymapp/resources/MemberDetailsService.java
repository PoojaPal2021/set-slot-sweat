package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class MemberDetailsService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

//        System.out.println("Loading user info:");
//        System.out.println("Email: " + email);

        String endpointURL = "http://localhost:18001/user-management/api/v1/members?email=" + email;

        // The user has to login into the users service running on port 18001
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        Map<String, String> authMap = new HashMap<>();
//        authMap.add("username", "jardiamj@gymapp.com");
//        authMap.add("password", "Password");
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(authMap, headers);
//
//        ResponseEntity<String> postResponse = this.restTemplate.postForEntity("http://localhost:18001/login", request, String.class);
//        System.out.println(postResponse.getBody());

        Member member = restTemplate.getForObject(endpointURL, Member.class);

        if (member == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

//        System.out.println("User to authenticate:");
//        System.out.println(member);

        return new MemberDetails(member.getEmail(), member.getPassword());
    }
}
