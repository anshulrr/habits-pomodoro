package com.anshul.atomichabits.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.business.AuthorityService;
import com.anshul.atomichabits.business.UserService;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.model.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	private UserService userService;

	private AuthorityService authorityService;

	private SignupService signup;

	public CustomUserDetailsService(UserService userService, AuthorityService authorityService, SignupService s) {
		this.userService = userService;
		this.authorityService = authorityService;
		this.signup = s;
	}

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		User user = null;
		try {			
			user = userService.getUserByUsernameOrEmail(usernameOrEmail);		
		} catch (ResourceNotFoundException e) {
			log.info("first time user: {}", usernameOrEmail);
			user = signup.saveUser(usernameOrEmail);
		}
		log.trace("from custom user detail service: " + user);

		Set<GrantedAuthority> authorities = authorityService.getAuthorities(user).stream()
				.map((authority) -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toSet());
		log.trace("from custom user detail service: " + authorities);

		return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassword(),
				authorities);
	}
}
