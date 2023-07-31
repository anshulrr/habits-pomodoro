package com.anshul.atomichabits.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("select u from users u where u.username = ?1 or u.email = ?1")
	public Optional<User> findByUsername(String name);
	
	public Optional<User> findByEmail(String email);
	
	public Optional<User> findByUsernameOrEmail(String username, String email);
}
