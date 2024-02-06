package com.anshul.atomichabits.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.dto.UserForList;
import com.anshul.atomichabits.model.AccountabilityPartner;

public interface AccountabilityPartnerRepository extends JpaRepository<AccountabilityPartner, Long> {

	@Query("select a.partner.id id, a.partner.email email from accountability_partners a where a.subject.id = :userId")
	public List<UserForList> getPartners(Long userId);
	
	@Query("select a.subject.id id, a.subject.email email from accountability_partners a where a.partner.id = :userId")
	public List<UserForList> getSubjects(Long userId);
	
	@Query("select count(*) from accountability_partners a where a.partner.id = :userId and a.subject.id = :subjectId")
	public int getSubject(Long userId, Long subjectId);
	
	@Modifying
	@Query("delete from accountability_partners a where a.subject.id = :userId and a.partner.id = :partnerId")
	public void deletePartner(Long userId, Long partnerId);
}
