package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anshul.atomichabits.dto.UserForList;
import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.AccountabilityPartnerRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.AccountabilityPartner;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AccountabilityPartnerService {

	private UserRepository userRepository;

	private AccountabilityPartnerRepository accountablityPartnerRepository;
	
	public List<UserForList> getPartners(Long userId) {
		return accountablityPartnerRepository.getPartners(userId);
	}
	
	public List<UserForList> getSubjects(Long userId) {
		return accountablityPartnerRepository.getSubjects(userId);
	}
	
	public boolean isSubject(Long userId, Long subjectId) {
		return accountablityPartnerRepository.getSubject(userId, subjectId) != 0;
	}
	
	public AccountabilityPartner addPartner(Long userId, String email) {
		AccountabilityPartner accountabilityPartner = new AccountabilityPartner();
		
		Optional<User> subjectEntry = userRepository.findById(userId);
		Optional<User> partnerEntry = userRepository.findByUsernameOrEmail(email);
				
		if (subjectEntry.isEmpty())
		 	throw new ResourceNotFoundException("subject id:" + userId);
		
		if (partnerEntry.isEmpty())
		 	throw new ResourceNotFoundException("partner id:" + email);
		
		accountabilityPartner.setSubject(subjectEntry.get());
		accountabilityPartner.setPartner(partnerEntry.get());
		
		return accountablityPartnerRepository.save(accountabilityPartner);
	}
	
	@Transactional
	public void deletePartner(Long userId, Long partnerId) {
		accountablityPartnerRepository.deletePartner(userId, partnerId);
	}
}
