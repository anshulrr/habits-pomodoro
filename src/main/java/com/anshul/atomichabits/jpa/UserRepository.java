package com.anshul.atomichabits.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from users u where u.username = :name or u.email = :name")
	public Optional<User> findByUsernameOrEmail(String name);
}
