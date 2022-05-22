package edu.uwb.gymapp.usermanagement;

import edu.uwb.gymapp.models.*;
import edu.uwb.gymapp.resources.ReservationController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReservationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ReservationController reservationController;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
		assertThat(reservationController).isNotNull();
	}

	@Test
	public void loginShouldSucceed() {
		String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		// GIVEN: a valid user username and password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "unittest@setslotsweat.com");
		authMap.put("password", "Password");

		// WHEN: a member attempts to login
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		ResponseEntity<String> postResponse = this.restTemplate.postForEntity(endpoint, request, String.class);

		// THEN: Login should succeed
		Assertions.assertEquals(postResponse.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void loginWithInvalidPasswordShouldFail() {
		String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		// GIVEN: an invalid user username and password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "unittest@setslotsweat.com");
		authMap.put("password", "XXX");

		// WHEN: a member attempts to login
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);

		Exception exception = assertThrows(ResourceAccessException.class, () -> {
			ResponseEntity<String> postResponse = this.restTemplate.postForEntity(endpoint, request, String.class);
		});


		// THEN: Login should fail (an ResourceAccessException is thrown)
		Assertions.assertEquals(ResourceAccessException.class, exception.getClass());
	}

	@Test
	public void loginWithoutPasswordShouldFail() {
		String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		// Given: a user without a password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "unittest@setslotsweat.com");

		// When: a member attempts to login
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);

		Exception exception = assertThrows(ResourceAccessException.class, () -> {
			ResponseEntity<String> postResponse = this.restTemplate.postForEntity(endpoint, request, String.class);
		});

		// Then: Login should fail (an ResourceAccessException is thrown)
		Assertions.assertEquals(ResourceAccessException.class, exception.getClass());
	}

	@Test
	public void successfulLoginShouldReturnListOfReservations() throws Exception {
		String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		// Given: a valid user username and password
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "unittest@setslotsweat.com");
		authMap.put("password", "Password");

		// When: a member attempts to login
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		ResponseEntity<Reservation[]> postResponse = this.restTemplate.postForEntity(endpoint, request, Reservation[].class);

		// Then: The response should contain a list of reservations
		Assertions.assertEquals(postResponse.getStatusCode(), HttpStatus.OK);
		Reservation[] reservations = postResponse.getBody();
		List<Reservation> reservationList = Arrays.asList(reservations);
		Assertions.assertFalse(reservationList.isEmpty());
	}

	@Test
	public void bookingSessionShouldSucceed() {
		// TODO: setup a member that is only used for testing
		String email = "unittest@setslotsweat.com";
		Long sessionIdToBook = 5L;
		String bookingEndpoint = "http://localhost:" + port +
				                 "/reservation-service/api/v1/session/book/" + sessionIdToBook +
						         "?email=" + email;
		String loginEndpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		// GIVEN: a valid user logs in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", email);
		authMap.put("password", "Password");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		ResponseEntity<Reservation[]> loginResponse = this.restTemplate.postForEntity(loginEndpoint, request, Reservation[].class);

		ResponseEntity<Reservation[]> bookingResponse = this.restTemplate.postForEntity(bookingEndpoint, null, Reservation[].class);

		// THEN: Both returned lists are equal but session ID 2 is now booked
		Long newBookedReservationId = -1L;
		Boolean newSessionIsBooked = false;
		List<Reservation>	reservationList = Arrays.asList(bookingResponse.getBody());
		for (Reservation reservation : reservationList) {
			Long sessionId = reservation.getSession().getId();
			if (reservation.getId() != null && sessionId == sessionIdToBook) {
				newBookedReservationId = reservation.getId();
				newSessionIsBooked = reservation.isBooked();
				break;
			}
		}
		// Clean up
		if (newBookedReservationId > -1L) {
//			System.out.println("Cleaning Up ...");
//			System.out.println("Deleting reservation with ID: " + newBookedReservationId);
			reservationRepository.deleteById(newBookedReservationId);
		}

		// Assertions
		Assertions.assertEquals(loginResponse.getBody().length, bookingResponse.getBody().length);
		Assertions.assertTrue(newSessionIsBooked);
	}

	@Test
	public void bookingSessionAfterLoginOutShouldFail() {
		String email = "unittest@setslotsweat.com";
		String logoutEndPoint = "http://localhost:" + port + "/reservation-service/api/v1/member/logout";
		Long sessionIdToBook = 5L;
		String bookingEndpoint = "http://localhost:" + port +
				"/reservation-service/api/v1/session/book/" + sessionIdToBook +
				"?email=" + email;

		// GIVEN: User is logged out
		this.restTemplate.postForEntity(logoutEndPoint, new HttpEntity<>(null), String.class);

		// WHEN: Try to book a session
		ResponseEntity<String> response = this.restTemplate.postForEntity(bookingEndpoint, null, null);

		// THEN: Access is denied
		Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	@Test
	public void bookingSameSessionTwiceShouldFail() {
		String email = "unittest@setslotsweat.com";
		Long sessionIdToBook = 5L;
		String bookingEndpoint = "http://localhost:" + port +
				"/reservation-service/api/v1/session/book/" + sessionIdToBook +
				"?email=" + email;
		String loginEndpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		// GIVEN: a valid user logs in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", email);
		authMap.put("password", "Password");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		ResponseEntity<Reservation[]> loginResponse = this.restTemplate.postForEntity(loginEndpoint, request, Reservation[].class);

		// WHEN: Tries to book the same session twice
		ResponseEntity<Reservation[]> bookingResponse = this.restTemplate.postForEntity(bookingEndpoint, null, Reservation[].class);
//		System.out.println("Booking response status: " + bookingResponse.getStatusCode());

		// THEN: second attempt should return a CONFLIC Http status
		ResponseEntity<String> bookingResponse2 = this.restTemplate.postForEntity(bookingEndpoint, null, String.class);
		// Assert status code in CONFLICT
		Assertions.assertEquals(HttpStatus.CONFLICT, bookingResponse2.getStatusCode());

		// CLEAN UP
		Long newBookedReservationId = -1L;
		Boolean newSessionIsBooked = false;
		List<Reservation>	reservationList = Arrays.asList(bookingResponse.getBody());
		for (Reservation reservation : reservationList) {
			Long sessionId = reservation.getSession().getId();
			if (reservation.getId() != null && sessionId == sessionIdToBook) {
				newBookedReservationId = reservation.getId();
				newSessionIsBooked = reservation.isBooked();
				break;
			}
		}
		// Clean up
		if (newBookedReservationId > -1L) {
//			System.out.println("Cleaning Up ...");
//			System.out.println("Deleting reservation with ID: " + newBookedReservationId);
			reservationRepository.deleteById(newBookedReservationId);
		}
	}

	@Test
	public void cancelingRecentlyBookedReservationShouldSucceed() {
		String email = "unittest@setslotsweat.com";
		String cancellationEndpoint= "http://localhost:" + port +
				  				      "/reservation-service/api/v1/session/cancel/";
		Long sessionIdToBook = 5L;
		String bookingEndpoint = "http://localhost:" + port +
				"/reservation-service/api/v1/session/book/" + sessionIdToBook +
				"?email=" + email;
		String loginEndpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		List<Reservation> originalReservationList = reservationRepository.findByMemberEmail(email);

		// GIVEN: a valid user logs in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", email);
		authMap.put("password", "Password");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		// Login
		ResponseEntity<Reservation[]> loginResponse = this.restTemplate.postForEntity(loginEndpoint, request, Reservation[].class);

		// WHEN: Books and then cancels a session
		// Booking:
		ResponseEntity<Reservation[]> bookingResponse = this.restTemplate.postForEntity(bookingEndpoint, null, Reservation[].class);
//		System.out.println("Booking response status: " + bookingResponse.getStatusCode());
		// Cancellation:
		Long newBookedReservationId = -1L;
		List<Reservation>	reservationList = Arrays.asList(bookingResponse.getBody());
		for (Reservation reservation : reservationList) {
			Long sessionId = reservation.getSession().getId();
			if (reservation.getId() != null && sessionId == sessionIdToBook) {
				newBookedReservationId = reservation.getId();
				break;
			}
		}
//		System.out.println("Cancelling Reservarion with ID: " + newBookedReservationId);
		cancellationEndpoint = cancellationEndpoint + newBookedReservationId + "?email=" + email;
		ResponseEntity<String> cancellationResponse = this.restTemplate.exchange(cancellationEndpoint, HttpMethod.DELETE, null, String.class);
//		System.out.println("cancellationResponse: " + cancellationResponse.getBody());

		// THEN
		Assertions.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
		Assertions.assertEquals(HttpStatus.OK, bookingResponse.getStatusCode());
		Assertions.assertEquals(HttpStatus.OK, cancellationResponse.getStatusCode());

		List<Reservation> endReservationList = reservationRepository.findByMemberEmail(email);
		Assertions.assertEquals(originalReservationList.size(), endReservationList.size());
	}

	@Test
	public void cancelingUnbookedReservationShouldFail() {
		String email = "unittest@setslotsweat.com";
		String cancellationEndpoint= "http://localhost:" + port +
				"/reservation-service/api/v1/session/cancel/";
		String loginEndpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";

		// GIVEN: a valid user logs in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", email);
		authMap.put("password", "Password");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		// Login
		ResponseEntity<Reservation[]> loginResponse = this.restTemplate.postForEntity(loginEndpoint, request, Reservation[].class);

		// WHEN: he tries to cancel a reservation he hasn't booked
		String nonExistingReservationEndpoint = cancellationEndpoint + 20 + "?email=" + email;
		ResponseEntity<String> cancellationResponse = this.restTemplate.exchange(nonExistingReservationEndpoint,
																				 HttpMethod.DELETE, null, String.class);
//		System.out.println("cancellationResponse: " + cancellationResponse.getBody());

		// THEN
		Assertions.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
		Assertions.assertEquals(HttpStatus.NOT_FOUND, cancellationResponse.getStatusCode());
	}

	@Test
	public void memberLogoutShouldSucceed() {
		String endpoint = "http://localhost:" + port + "/reservation-service/api/v1/member/login";
		String logoutEndPoint = "http://localhost:" + port + "/reservation-service/api/v1/member/logout";

		// GIVEN: That a legit users has logged in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> authMap = new HashMap<>();
		authMap.put("username", "unittest@setslotsweat.com");
		authMap.put("password", "Password");

		// Loging in
		HttpEntity<Map<String, String>> request = new HttpEntity<>(authMap, headers);
		ResponseEntity<String> postResponse = this.restTemplate.postForEntity(endpoint, request, String.class);

		// Login should succeed
		Assertions.assertEquals(postResponse.getStatusCode(), HttpStatus.OK);

		// WHEN: He attempts to logout
		ResponseEntity<String> logoutResponse = this.restTemplate.postForEntity(logoutEndPoint, new HttpEntity<>(null), String.class);

		// THEN: The logout should be successful
		Assertions.assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());
	}
}
