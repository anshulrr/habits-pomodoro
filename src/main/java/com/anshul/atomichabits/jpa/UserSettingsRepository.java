package com.anshul.atomichabits.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.*;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

	@Query("select s from users_settings s where s.user.id = :user_id")
	public UserSettings findUserSettings(Long user_id);
}
