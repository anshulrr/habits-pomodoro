package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@CacheConfig(cacheNames = "userCache")
public class UserService {

	private UserRepository userRepository;
	
	@Cacheable(value = "users")
	public List<User> retriveAllUsers() {
		return userRepository.findAll();
	}
	
	@Cacheable(value = "user", key = "#username")
	public User getUserByUsernameOrEmail(String username) {
		Optional<User> userEntry = userRepository.findByUsernameOrEmail(username);
		if (userEntry.isEmpty())
			throw new ResourceNotFoundException("user: " + username);
		
		return userEntry.get();
	}
	
	public boolean updatePassword(Long userId, String password) {
		Optional<User> userEntry = userRepository.findById(userId);
		if (userEntry.isEmpty())
			throw new ResourceNotFoundException("user id:" + userId);
		
		userEntry.get().setPassword(password);
		userRepository.save(userEntry.get());
		
		return true;
	}
}
