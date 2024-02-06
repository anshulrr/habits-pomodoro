package com.anshul.atomichabits.business;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.jpa.AuthorityRepository;
import com.anshul.atomichabits.model.Authority;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@CacheConfig(cacheNames = "authorityCache")
public class AuthorityService {

	private AuthorityRepository authorityRepository;
	
	@Cacheable(value = "authorities")
	public List<Authority> getAuthorities(User user) {
	  return authorityRepository.findByUser(user);
	}
}
