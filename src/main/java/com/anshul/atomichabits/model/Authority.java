package com.anshul.atomichabits.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "authorities")
public class Authority {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "username", referencedColumnName = "username")
	private User user;

	private String authority;

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
