package com.anshul.atomichabits.jpa;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.anshul.atomichabits.model.User;

@DataJpaTest
@AutoConfigureTestDatabase
class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Test
	void findByEmail() {
		Optional<User> userEntry = repository.findByUsernameOrEmail("ajay@xyz.com");
		assertEquals("Ajay", userEntry.get().getUsername());
	}
	
	@Test
	void findByUsername() {
		Optional<User> userEntry = repository.findByUsernameOrEmail("Ajay");
		assertEquals("Ajay", userEntry.get().getUsername());
	}
}
