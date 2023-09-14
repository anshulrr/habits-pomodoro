package com.anshul.atomichabits.business;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
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
				Arrays.asList(new User("Samay", "samay@xyz.com"))
				);
		
		List<User> users = userService.retriveAllUsers();
		
		assertEquals("Samay", users.get(0).getUsername());
	}
	
	@Test
	void udpatePassword() {
		
		when(userRepositoryMock.findById(1L)).thenReturn(
				Optional.of(new User("Samay", "samay@xyz.com"))
				);
		
		boolean result = userService.updatePassword(1L, "new_password");
		
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepositoryMock).save(captor.capture());
		
		assertEquals("new_password", captor.getValue().getPassword());
		assertEquals(true, result);
	}
}
