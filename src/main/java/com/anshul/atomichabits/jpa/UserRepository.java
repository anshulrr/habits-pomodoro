package com.anshul.atomichabits.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anshul.atomichabits.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByUsername(String name);
}
