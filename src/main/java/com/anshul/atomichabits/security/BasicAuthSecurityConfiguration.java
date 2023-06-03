package com.anshul.atomichabits.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class BasicAuthSecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests()
			.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("*")
						.allowedOrigins("http://localhost:3000");
			}
		};
	}

	// required for default spring security Todo: why
	@Bean
	public UserDetailsService userDetailService(DataSource dataSource) {
		
		var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

		return jdbcUserDetailsManager;
	}
}
