package com.anshul.atomichabits.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anshul.atomichabits.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
