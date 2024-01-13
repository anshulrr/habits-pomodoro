package com.anshul.atomichabits.security;

import java.util.Optional;
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
import com.anshul.atomichabits.model.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	private UserService userService;

	private AuthorityService authorityService;

	public CustomUserDetailsService(UserService userService, AuthorityService authorityService) {
		this.userService = userService;
		this.authorityService = authorityService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optional_user = userService.getUserByUsernameOrEmail(username);		
		log.debug("from custom user detail service: " + optional_user);
		User user = optional_user.get();

		Set<GrantedAuthority> authorities = authorityService.getAuthorities(user).stream()
				.map((authority) -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toSet());
		log.trace("from custom user detail service: " + authorities);

		return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassword(),
				authorities);
	}
}
