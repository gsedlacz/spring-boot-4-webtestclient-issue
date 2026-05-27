package com.example.spring_boot_4_webtest_client_security_test.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.spring_boot_4_webtest_client_security_test.config.SecurityConfiguration;

/**
 * Integration tests for {@link ExampleController} using {@link WebTestClient}.
 * Cases covered:
 * - Case 1 - authenticated user with role MY_USER -> should return 200 with body
 * - Case 2 - unauthenticated request -> should return 401
 * - Case 3 - authenticated user without role MY_USER -> should return 403
 * - Case 4 - @WithMockUser with role MY_USER -> should return 200 with body
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WebTestClientIntegrationTest
{
	private static final String ENDPOINT = ExampleController.ENDPOINT;
	private static final String EXPECTED_BODY = "Hello World";
	private static final String WRONG_ROLE = "OTHER_ROLE";

	@Autowired
	private WebTestClient webTestClient;

	/**
	 * Case 1 - authenticated user with role MY_USER -> should return 200 with body
	 */
	@Test
	void givenAuthenticatedUserWithCorrectRole_whenGetExample_thenReturnsOkWithBody()
	{
		// given & when & then
		webTestClient.get()
			.uri(ENDPOINT)
			.headers(headers -> headers.setBasicAuth(
				SecurityConfiguration.SECRET_ADMIN,
				SecurityConfiguration.SECRET_ADMIN))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo(EXPECTED_BODY);
	}

	/**
	 * Case 2 - unauthenticated request -> should return 401
	 */
	@Test
	void givenUnauthenticatedRequest_whenGetExample_thenReturnsUnauthorized()
	{
		// when & then
		webTestClient.get()
			.uri(ENDPOINT)
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}

	/**
	 * Case 3 - authenticated user without role MY_USER -> should return 403
	 */
	@Test
	@WithMockUser(roles = WRONG_ROLE)
	void givenAuthenticatedUserWithWrongRole_whenGetExample_thenReturnsForbidden()
	{
		// when & then
		webTestClient.get()
			.uri(ENDPOINT)
			.exchange()
			.expectStatus()
			.isForbidden();
	}

	/**
	 * Case 4 - @WithMockUser with role MY_USER -> should return 200 with body
	 */
	@Test
	@WithMockUser(roles = SecurityConfiguration.MY_USER_ROLE)
	void givenMockUserWithCorrectRole_whenGetExample_thenReturnsOkWithBody()
	{
		// when & then
		webTestClient.get()
			.uri(ENDPOINT)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo(EXPECTED_BODY);
	}
}
