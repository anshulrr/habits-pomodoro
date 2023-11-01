package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

@Service
@CacheConfig(cacheNames = "userCache")
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Cacheable(value = "users")
	public List<User> retriveAllUsers() {
		List<User> users = userRepository.findAll();
		return users;
	}
	
	@Cacheable(value = "user", key = "#username")
	public Optional<User> getUserByUsernameOrEmail(String username) {
	  return userRepository.findByUsernameOrEmail(username);
	}
	
	public boolean updatePassword(Long user_id, String password) {
		Optional<User> userEntry = userRepository.findById(user_id);
		
		userEntry.get().setPassword(password);
		userRepository.save(userEntry.get());
		
		return true;
	}
}
