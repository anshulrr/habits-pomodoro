package com.anshul.atomichabits.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Authority;
import com.anshul.atomichabits.model.User;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	@Query("select a from authorities a where a.user = ?1")
	public List<Authority> findByUser(User user);
}
