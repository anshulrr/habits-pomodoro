package com.anshul.atomichabits.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.jpa.AuthorityRepository;
import com.anshul.atomichabits.model.Authority;
import com.anshul.atomichabits.model.User;

@Service
@CacheConfig(cacheNames = "authorityCache")
public class AuthorityService {

	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Cacheable(value = "authorities")
	public List<Authority> getAuthorities(User user) {
	  return authorityRepository.findByUser(user);
	}
}
