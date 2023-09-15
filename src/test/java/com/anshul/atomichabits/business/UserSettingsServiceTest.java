package com.anshul.atomichabits.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.anshul.atomichabits.dto.UserSettingsRequestDto;
import com.anshul.atomichabits.jpa.UserSettingsRepository;
import com.anshul.atomichabits.model.UserSettings;

@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {

	@InjectMocks
	private UserSettingsService userSettingsService;

	@Mock
	private UserSettingsRepository userSettingsRepositoryMock;

	@Test
	void retrieveUserSettings() {
		when(userSettingsRepositoryMock.findUserSettings(1L))
			.thenReturn(new UserSettings());
		
		UserSettings userSettings = userSettingsService.retriveUserSettings(1L);
		
		assertNotNull(userSettings);
	}
	
	@Test
	void updateUserSettings() {
		UserSettings userSettings = new UserSettings();
		userSettings.setBreakLength(5);
		when(userSettingsRepositoryMock.findUserSettings(1L))
			.thenReturn(userSettings);
		
		UserSettingsRequestDto settingsRequest = new UserSettingsRequestDto();
		settingsRequest.setBreakLength(0);
		
		userSettingsService.updateUserSettings(1L, settingsRequest);
		
		ArgumentCaptor<UserSettings> captor = ArgumentCaptor.forClass(UserSettings.class);
		verify(userSettingsRepositoryMock).save(captor.capture());
		
		assertEquals(userSettings.getBreakLength(), captor.getValue().getBreakLength());
		assertEquals(0, captor.getValue().getBreakLength());
	}
}
