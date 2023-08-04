package com.anshul.atomichabits.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.anshul.atomichabits.model.User;

@DataJpaTest
@AutoConfigureTestDatabase
class UserRepositoryTest {

	@Autowired
	private UserRepository repository;
	
	@Test
	void findAll() {
		List<User> users = repository.findAll();
		assertEquals(3, users.size());
	}

}
