package com.anshul.atomichabits.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anshul.atomichabits.model.Authority;
import com.anshul.atomichabits.model.User;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	public List<Authority> findByUser(User user);
}
