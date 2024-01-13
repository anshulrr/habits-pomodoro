package com.anshul.atomichabits.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

//import io.jsonwebtoken.*;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JwtDecoder jwtDecoder;

	private UserDetailsService userDetailsService;
	
	private SignupService signup;

	public JwtAuthenticationFilter(JwtDecoder jwtDecoder, UserDetailsService userDetailsService, SignupService signup) {
		this.jwtDecoder = jwtDecoder;
		this.userDetailsService = userDetailsService;
		this.signup = signup;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// get JWT token from http request
		String token = getTokenFromRequest(request);
		

        // decode token and set context holder
		if (StringUtils.hasText(token)) {
			Map<String, Object> claims = jwtDecoder.decode(token).getClaims();
			log.debug("token claims: {}", claims);
			String uid = (String) claims.get("user_id"); 
			String email = (String) claims.get("email");
			String phone = (String) claims.get("phone_number");

			UserDetails userDetails = null;
			try {
				userDetails = userDetailsService.loadUserByUsername(uid);
			} catch (Exception e) {
				log.info("first time user: {}", uid);
				signup.saveUser(uid, email, phone);
				userDetails = userDetailsService.loadUserByUsername(uid);
			}

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());

			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}

		filterChain.doFilter(request, response);
	}

	private String getTokenFromRequest(HttpServletRequest request) {

		String bearerToken = request.getHeader("Authorization");
		log.trace("token: {}", bearerToken);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}

		return null;
	}

}
