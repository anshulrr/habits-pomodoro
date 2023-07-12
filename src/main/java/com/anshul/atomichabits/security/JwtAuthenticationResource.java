package com.anshul.atomichabits.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtAuthenticationResource {

	private JwtEncoder jwtEncoder;

	private final AuthenticationManager authenticationManager;

	public JwtAuthenticationResource(JwtEncoder jwtEncoder, AuthenticationManager authenticationManager) {
		this.jwtEncoder = jwtEncoder;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/authenticate")
	public JwtRespose authenticate(@RequestBody JwtRequest jwtRequest) {
		var authenticationToken = new UsernamePasswordAuthenticationToken(
				jwtRequest.username(),
				jwtRequest.password());

		var authentication = authenticationManager.authenticate(authenticationToken);

		System.out.println(authentication);

		return new JwtRespose(createToken(authentication));
	}

	private String createToken(Authentication authentication) {
		var claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(60 * 360))
				.subject(authentication.getName())
				.claim("scope", createScope(authentication))
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims))
				.getTokenValue();
	}

	private String createScope(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.map(a -> a.getAuthority())
				.collect(Collectors.joining(" "));
	}

}

record JwtRespose(String token) {
}

record JwtRequest(String username, String password) {
}