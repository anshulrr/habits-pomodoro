package com.anshul.atomichabits.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
public class JwtSecurityConfiguration {

	private Environment env;
	
	private JwtAuthenticationFilter authenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. Authorize Requests using the new lambda syntax
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/manage/**").hasAuthority("admin")
            .anyRequest().authenticated()
        )
        
        // 2. Stateless Session Management
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // 3. Disable HTTP Basic and CSRF
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)

        // 4. Enable CORS with default bean lookup (bypassing the authorization checks for OPTIONS requests)
        .cors(Customizer.withDefaults())

        // 5. OAuth2 Resource Server with JWT
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(Customizer.withDefaults())
        )

        // 6. Custom Filter placement (filter for custom security context)
        .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class);

    return http.build();
}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    
	    // Use specific origins from env to avoid security risks
	    String clientUrl = env.getProperty("atomichabits.client.url");
	    configuration.setAllowedOrigins(List.of(clientUrl != null ? clientUrl : "http://localhost:3000"));
	    
	    // Explicitly list methods instead of "*" for better security/performance
	    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    
	    // Essential for modern APIs (JWT, Content-Type, etc.)
	    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
	    
	    // Allow browser to cache the preflight response for 1 hour (3600 seconds)
	    configuration.setMaxAge(3600L);
	    
	    // Required if you use cookies or specific auth headers
	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
}
