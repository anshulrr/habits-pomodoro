package com.anshul.atomichabits.jpa;

import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.anshul.atomichabits.model.User;
import com.anshul.atomichabits.model.UserSettings;

@DataJpaTest
@AutoConfigureTestDatabase
class UserSettingsRepositoryTest {

	@Autowired
	private UserSettingsRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void findUserSettings() {
		UserSettings userSettings = repository
				.findUserSettings(userRepository.findByUsernameOrEmail("ajay@xyz.com").get().getId());
		assertNotNull(userSettings);
	}

}
