package com.example.spring_boot_4_webtest_client_security_test.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.example.spring_boot_4_webtest_client_security_test.config.SecurityConfiguration;

/**
 * Integration tests for {@link ExampleController}.
 * Cases covered:
 * - Case 1 - authenticated user with role MY_USER -> should return 200 with body
 * - Case 2 - unauthenticated request -> should return 401
 * - Case 3 - authenticated user without role MY_USER -> should return 403
 * - Case 4 - @WithMockUser with role MY_USER -> should return 200 with body
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MockMvcIntegrationTest
{
	private static final String ENDPOINT = ExampleController.ENDPOINT;
	private static final String EXPECTED_BODY = "Hello World";
	private static final String WRONG_ROLE = "OTHER_ROLE";

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Case 1 - authenticated user with role MY_USER -> should return 200 with body
	 */
	@Test
	void givenAuthenticatedUserWithCorrectRole_whenGetExample_thenReturnsOkWithBody() throws Exception
	{
		// given
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT)
			.with(SecurityMockMvcRequestPostProcessors.httpBasic(
				SecurityConfiguration.SECRET_ADMIN,
				SecurityConfiguration.SECRET_ADMIN)));

		// when & then
		resultActions.andExpect(status().isOk())
			.andExpect(content().string(EXPECTED_BODY));
	}

	/**
	 * Case 2 - unauthenticated request -> should return 401
	 */
	@Test
	void givenUnauthenticatedRequest_whenGetExample_thenReturnsUnauthorized() throws Exception
	{
		// when & then
		mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
			.andExpect(status().isUnauthorized());
	}

	/**
	 * Case 3 - authenticated user without role MY_USER -> should return 403
	 */
	@Test
	@WithMockUser(roles = WRONG_ROLE)
	void givenAuthenticatedUserWithWrongRole_whenGetExample_thenReturnsForbidden() throws Exception
	{
		// when & then
		mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
			.andExpect(status().isForbidden());
	}

	/**
	 * Case 4 - @WithMockUser with role MY_USER -> should return 200 with body
	 */
	@Test
	@WithMockUser(roles = SecurityConfiguration.MY_USER_ROLE)
	void givenMockUserWithCorrectRole_whenGetExample_thenReturnsOkWithBody() throws Exception
	{
		// when & then
		mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
			.andExpect(status().isOk())
			.andExpect(content().string(EXPECTED_BODY));
	}

}
