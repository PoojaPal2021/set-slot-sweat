package edu.uwb.gymapp.usermanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.models.Reservation;
import edu.uwb.gymapp.models.Session;
import edu.uwb.gymapp.resources.MemberDetails;
import edu.uwb.gymapp.resources.ReservationController;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
		String apiBasePath = "/reservation-service/api/v1";

		// Given: a valid user username and password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "jardiamj@gymapp.com");
		authMap.put("password", "Password");

		// When: a member attempts to login
		URI url = new URI("http://localhost:" + port + "/reservation-service/api/v1/member/login");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		ResponseEntity<String> postResponse = this.restTemplate.postForEntity(url, request, String.class);

		// Then: Login should succeed
		Assertions.assertEquals(postResponse.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void loginWithInvalidPasswordShouldFail() throws Exception {
		String apiBasePath = "/reservation-service/api/v1";

		// Given: an invalid user username and password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "jardiamj@gymapp.com");
		authMap.put("password", "XXX");

		// When: a member attempts to login
		URI url = new URI("http://localhost:" + port + "/reservation-service/api/v1/member/login");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);

		Exception exception = assertThrows(ResourceAccessException.class, () -> {
			ResponseEntity<String> postResponse = this.restTemplate.postForEntity(url, request, String.class);
		});


		// Then: Login should fail (an ResourceAccessException is thrown)
		Assertions.assertEquals(ResourceAccessException.class, exception.getClass());
	}

	@Test
	public void loginWithoutPasswordShouldFail() throws Exception {
		String apiBasePath = "/reservation-service/api/v1";

		// Given: a user without a password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "jardiamj@gymapp.com");

		// When: a member attempts to login
		URI url = new URI("http://localhost:" + port + "/reservation-service/api/v1/member/login");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);

		Exception exception = assertThrows(ResourceAccessException.class, () -> {
			ResponseEntity<String> postResponse = this.restTemplate.postForEntity(url, request, String.class);
		});


		// Then: Login should fail (an ResourceAccessException is thrown)
		Assertions.assertEquals(ResourceAccessException.class, exception.getClass());
	}

	@Test
	public void successfulLoginShouldReturnListOfReservations() throws Exception {
		String apiBasePath = "/reservation-service/api/v1";

		// Given: a valid user username and password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "jardiamj@gymapp.com");
		authMap.put("password", "Password");

		// When: a member attempts to login
		URI url = new URI("http://localhost:" + port + "/reservation-service/api/v1/member/login");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		ResponseEntity<Reservation[]> postResponse = this.restTemplate.postForEntity(url, request, Reservation[].class);

		// Then: The response should contain a list of reservations
		Assertions.assertEquals(postResponse.getStatusCode(), HttpStatus.OK);
		Reservation[] reservations = postResponse.getBody();
		List<Reservation> reservationList = Arrays.asList(reservations);
		Assertions.assertFalse(reservationList.isEmpty());
	}

}
