package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.UpdateRequest;

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
		Optional<User> userEntry = userRepository.findByUsernameOrEmail(username);
		if (userEntry.isEmpty())
			throw new ResourceNotFoundException("user: " + username);
	  return userEntry;
	}
	
	public boolean updatePassword(Long user_id, String password) {
		Optional<User> userEntry = userRepository.findById(user_id);
		
		userEntry.get().setPassword(password);
		userRepository.save(userEntry.get());
		
		return true;
	}

	public boolean updatePhone(Long user_id, String phone) {
		Optional<User> userEntry = userRepository.findById(user_id);
		
		userEntry.get().setPhone(phone);
		userRepository.save(userEntry.get());
		
		// TODO: validate phone
		UpdateRequest request = new UpdateRequest(userEntry.get().getUsername())
				.setPhoneNumber(phone);
		
		try {
			UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
		} catch (FirebaseAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
