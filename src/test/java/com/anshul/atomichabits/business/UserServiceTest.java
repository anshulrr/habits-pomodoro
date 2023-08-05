package com.anshul.atomichabits.business;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepositoryMock;

	@Test
	void retrieve() {
		when(userRepositoryMock.findAll()).thenReturn(
				Arrays.asList(new User("Ansh", "ansh@gmail.com"))
				);
		
		List<User> users = userService.retriveAllUsers();
		
		assertEquals("Ansh", users.get(0).getUsername());
	}
}
