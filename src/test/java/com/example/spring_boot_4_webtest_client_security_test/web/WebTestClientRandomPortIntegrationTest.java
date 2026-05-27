package com.example.spring_boot_4_webtest_client_security_test.web;

import static com.example.spring_boot_4_webtest_client_security_test.config.SecurityConfiguration.MY_USER_ROLE;
import static com.example.spring_boot_4_webtest_client_security_test.config.SecurityConfiguration.SECRET_ADMIN;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;

import com.example.spring_boot_4_webtest_client_security_test.config.SecurityConfiguration;

/**
 * Integration tests for {@link ExampleController} using {@link WebTestClient}.
 * Cases covered:
 * - Case 1 - authenticated user with role MY_USER -> should return 200 with body
 * - Case 2 - unauthenticated request -> should return 401
 * - Case 3 - authenticated user without role MY_USER -> should return 403
 * - Case 4 - @WithMockUser with role MY_USER -> should return 200 with body
 * - Case 5 - WebTestClient mutateWith basic auth -> should return 200 with body
 * - Case 6 - WebTestClient mutateWith mock user -> should throw NullPointerException
 */
@SpringBootTest
	(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WebTestClientRandomPortIntegrationTest
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
				SECRET_ADMIN,
				SECRET_ADMIN))
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
	@WithMockUser(roles = MY_USER_ROLE)
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

	/**
	 * Case 5 - WebTestClient mutateWith basic auth -> should return 200 with body
	 */
	@Test
	void givenMutatedWebTestClientWithBasicAuth_whenGetExample_thenReturnsOkWithBody()
	{
		// given
		final WebTestClient authenticatedWebTestClient = webTestClient.mutateWith((builder, httpHandlerBuilder, connector) -> builder.defaultHeaders(
			headers -> headers.setBasicAuth(SecurityConfiguration.SECRET_ADMIN, SecurityConfiguration.SECRET_ADMIN)));

		// when & then
		authenticatedWebTestClient.get()
			.uri(ENDPOINT)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo(EXPECTED_BODY);
	}

	/**
	 * Case 6 - WebTestClient mutateWith mock user -> should throw NullPointerException
	 */
	@Test
	void givenMutatedWebTestClientWithMockUser_whenMutateWith_thenThrowsNullPointerException()
	{
		// given
		webTestClient.mutateWith(
			SecurityMockServerConfigurers.mockUser(SECRET_ADMIN)
				.roles(MY_USER_ROLE));

		// NOTE: this will cause an NPE inside of
	}
}
