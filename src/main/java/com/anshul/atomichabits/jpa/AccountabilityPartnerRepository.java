package com.anshul.atomichabits.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.UserForList;
import com.anshul.atomichabits.model.AccountabilityPartner;

public interface AccountabilityPartnerRepository extends JpaRepository<AccountabilityPartner, Long> {

	@Query("select a.partner.id id, a.partner.email email from accountability_partners a where a.subject.id = :user_id")
	public List<UserForList> getPartners(Long user_id);
	
	@Query("select a.subject.id id, a.subject.email email from accountability_partners a where a.partner.id = :user_id")
	public List<UserForList> getSubjects(Long user_id);
}
