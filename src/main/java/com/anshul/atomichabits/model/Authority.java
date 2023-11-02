package com.anshul.atomichabits.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "authorities")
@Table(indexes= {
		@Index(name="authorities_user_index", columnList="username")
})
public class Authority {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "username", referencedColumnName = "username")
	@JsonIgnore
	private User user;

	private String authority;
	
	@CreationTimestamp
	@JsonIgnore
	private Instant createdAt;
	
	@UpdateTimestamp
	@JsonIgnore
	private Instant updatedAt;

	public Authority(User user, String authority) {
		super();
		this.user = user;
		this.authority = authority;
	}

	@Override
	public String toString() {
		return "Authority [id=" + id + ", user=" + user + ", authority=" + authority + "]";
	}
}
