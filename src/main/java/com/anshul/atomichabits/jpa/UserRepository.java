package com.anshul.atomichabits.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anshul.atomichabits.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
