package com.anshul.atomichabits.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "accountability_partners")
@Table(uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "subject_id", "partner_id" }) 
})
public class AccountabilityPartner {

	@Id
	@GeneratedValue
	private Long id;
	
    @ManyToOne
    @JoinColumn(name = "subject_id")
    User subject;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    User partner;
	
	@CreationTimestamp
	@JsonIgnore
	private Instant createdAt;
	
	@UpdateTimestamp
	@JsonIgnore
	private Instant updatedAt;
}
