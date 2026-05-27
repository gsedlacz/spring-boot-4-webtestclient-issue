package com.example.spring_boot_4_webtest_client_security_test.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ExampleController
{

	public static final String ENDPOINT = "/example";

	// authorization: does the user have the role "MY_USER"
	@PreAuthorize("hasRole('MY_USER')")
	@GetMapping(ENDPOINT)
	public String endpoint()
	{
		return "Hello World";
	}
}
