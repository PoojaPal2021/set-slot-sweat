package edu.uwb.gymapp.usermanagement;

import edu.uwb.gymapp.models.Member;
import edu.uwb.gymapp.resources.MemberController;
import edu.uwb.gymapp.resources.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserManagementTests {

	@LocalServerPort
	private int port;

	@Autowired
	private MemberController memberController;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private MemberService memberService;

	@Test
	void contextLoads() {
		assertThat(memberController).isNotNull();
	}

	@Test
	public void signupShouldSucceed() {
		String endpoint = "http://localhost:" + port + "/user-management/api/v1/member/signup";
		String memberEmail = "testuser@gymapp.com";

		// GIVEN
		Member member = new Member();
		member.setEmail(memberEmail);
		member.setPassword("SuperSecure");

		// WHEN
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Member> request = new HttpEntity<>(member, headers);
		ResponseEntity<String> response = this.restTemplate.postForEntity(endpoint, request, String.class);

		// THEN
		String responseString = response.getBody();
		Assertions.assertEquals("Your profile was successfully created with set-slot-sweat.", responseString);

		// Clean up
		Member addedMember = memberService.getMember(memberEmail);
		memberService.deleteMember(addedMember.getId());
	}

	@Test
	public void signupWithoutEmailShouldFail() {
		String endpoint = "http://localhost:" + port + "/user-management/api/v1/member/signup";

		// GIVEN
		Member member = new Member();
		member.setPassword("SuperSecure");

		// WHEN
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Member> request = new HttpEntity<>(member, headers);

		ResponseEntity<Member> response = this.restTemplate.postForEntity(endpoint, request, Member.class);

		// THEN
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void signupWithoutPasswordShouldFail() {
		String endpoint = "http://localhost:" + port + "/user-management/api/v1/member/signup";

		// GIVEN
		Member member = new Member();
		member.setEmail("testuser@gymapp.com");

		// WHEN
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Member> request = new HttpEntity<>(member, headers);

		ResponseEntity<Member> response = this.restTemplate.postForEntity(endpoint, request, Member.class);

		// THEN
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void signupTwiceWithSameEmailShouldFail() {
		String endpoint = "http://localhost:" + port + "/user-management/api/v1/member/signup";
		String memberEmail = "testuser@gymapp.com";

		// GIVEN
		Member member = new Member();
		member.setEmail(memberEmail);
		member.setPassword("SuperSecure");

		// WHEN
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Member> request = new HttpEntity<>(member, headers);
		ResponseEntity<String> goodResponse = this.restTemplate.postForEntity(endpoint, request, String.class);

		// AND: first succeeds
		String goodResponseString = goodResponse.getBody();
		Assertions.assertEquals("Your profile was successfully created with set-slot-sweat.", goodResponseString);

		// THEN: Second attempt to signup with same email should fail
		Member doubleMember = new Member();
		member.setEmail("testuser@gymapp.com");
		member.setPassword("SuperSecure");
		request = new HttpEntity<>(doubleMember, headers);
		ResponseEntity<String> badResponse = this.restTemplate.postForEntity(endpoint, request, String.class);

		// Failure returns a BAD_REQUEST
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, badResponse.getStatusCode());

		// Clean up database
		Member addedMember = memberService.getMember(memberEmail);
		memberService.deleteMember(addedMember.getId());
	}

}


