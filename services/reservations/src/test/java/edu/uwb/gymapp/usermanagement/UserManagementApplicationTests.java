package edu.uwb.gymapp.usermanagement;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Session;
import edu.uwb.gymapp.resources.MemberDetails;
import edu.uwb.gymapp.resources.ReservationController;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserManagementApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ReservationController reservationController;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
		assertThat(reservationController).isNotNull();
	}

	@Test
	public void loginShouldSucceed() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> authMap = new LinkedMultiValueMap<>();
		authMap.add("username", "jardiamj@gymapp.com");
		authMap.add("password", "Password");

		URI url = new URI("http://localhost:" + port + "/login");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(authMap, headers);
		// login
		ResponseEntity<String> postResponse = this.restTemplate.postForEntity(url, request, String.class);


		url = new URI("http://localhost:" + port + "/sessions");
		ResponseEntity<Session[]> response = this.restTemplate.getForEntity(url, Session[].class);

		List<Session> sessions = Arrays.asList(response.getBody());

		System.out.println(sessions);
	}


}
