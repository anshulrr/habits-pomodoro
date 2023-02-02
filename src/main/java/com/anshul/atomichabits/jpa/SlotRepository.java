package com.anshul.atomichabits.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Slot;
import com.anshul.atomichabits.model.User;

public interface SlotRepository extends JpaRepository<Slot, Long> {

	@Query("select p from slots p where p.user = ?1 and p.id = ?2")
	public Optional<Slot> findUserSlotById(User user, Long task_id);
}
