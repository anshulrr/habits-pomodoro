package com.anshul.atomichabits.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
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
