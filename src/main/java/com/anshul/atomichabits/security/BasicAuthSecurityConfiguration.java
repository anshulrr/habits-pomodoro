package com.anshul.atomichabits.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BasicAuthSecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests()
			.requestMatchers("/signup").permitAll()	// removing auth for signup
			.anyRequest().authenticated();
		
		
		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		// disable form login
		// http.formLogin();
		
		http.httpBasic();
		
		//  disable csrf
		http.csrf().disable();
		
		return http.build();
	}
	
	// required for default spring security
	@Bean
	public UserDetailsService userDetailService(DataSource dataSource) {
		
		var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

		return jdbcUserDetailsManager;
	}
}
