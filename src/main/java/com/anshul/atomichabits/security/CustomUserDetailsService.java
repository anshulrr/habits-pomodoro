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

import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	private SignupService signup;

	public CustomUserDetailsService(UserRepository userRepository, SignupService s) {
		this.userRepository = userRepository;
		this.signup = s;
	}

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		Optional<User> optional_user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

		User user;
		if (optional_user.isEmpty()) {
			user = signup.saveUser(usernameOrEmail);
		} else {
			user = optional_user.get();
		}

        // System.out.println("from custom user detail service: " + user);

		Set<GrantedAuthority> authorities = user.getAuthorities().stream()
				.map((authority) -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toSet());

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}
}
