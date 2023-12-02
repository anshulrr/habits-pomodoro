package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anshul.atomichabits.dto.UserForList;
import com.anshul.atomichabits.jpa.AccountabilityPartnerRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.AccountabilityPartner;
import com.anshul.atomichabits.model.User;

@Service
public class AccountabilityPartnerService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountabilityPartnerRepository accountablityPartnerRepository;
	
	public List<UserForList> getPartners(Long user_id) {
		return accountablityPartnerRepository.getPartners(user_id);
	}
	
	public List<UserForList> getSubjects(Long user_id) {
		return accountablityPartnerRepository.getSubjects(user_id);
	}
	
	public boolean isSubject(Long user_id, Long subject_id) {
		return accountablityPartnerRepository.getSubject(user_id, subject_id) != 0;
	}
	
	public AccountabilityPartner addPartner(Long user_id, String email) {
		AccountabilityPartner accountabilityPartner = new AccountabilityPartner();
		
		Optional<User> subjectEntry = userRepository.findById(user_id);
		Optional<User> partnerEntry = userRepository.findByUsernameOrEmail(email);
				
		accountabilityPartner.setSubject(subjectEntry.get());
		accountabilityPartner.setPartner(partnerEntry.get());
		
		return accountablityPartnerRepository.save(accountabilityPartner);
	}
	
	@Transactional
	public void deletePartner(Long user_id, Long partner_id) {
		accountablityPartnerRepository.deletePartner(user_id, partner_id);
	}
}
