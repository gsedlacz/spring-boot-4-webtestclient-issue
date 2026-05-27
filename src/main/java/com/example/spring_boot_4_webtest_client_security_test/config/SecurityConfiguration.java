package com.example.spring_boot_4_webtest_client_security_test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration
{

	public static final String MY_USER_ROLE = "MY_USER";
	public static final String SECRET_ADMIN = "admin";

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		http
			// require all requests to be authenticated
			.authorizeHttpRequests(authorize -> authorize.anyRequest()
				.authenticated())
			// enable basic auth
			.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService()
	{
		var admin = User.withDefaultPasswordEncoder()
			.username(SECRET_ADMIN)
			.password(SECRET_ADMIN)
			.roles(MY_USER_ROLE)
			.build();
		return new InMemoryUserDetailsManager(admin);
	}
}
