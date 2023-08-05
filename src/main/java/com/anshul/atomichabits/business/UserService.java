package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public List<User> retriveAllUsers() {
		List<User> users = userRepository.findAll();
		return users;
	}
	
	public boolean updatePassword(Long user_id, String password) {
		Optional<User> userEntry = userRepository.findById(user_id);
		
		userEntry.get().setPassword(password);
		userRepository.save(userEntry.get());
		
		return true;
	}
}
