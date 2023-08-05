package com.anshul.atomichabits.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "users")
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true, nullable = false)
	@NotBlank(message = "Username is mandatory")
	@Size(min = 2, message = "Username should have atleast 2 characters")
	private String username;

	@Column(unique = true, nullable = false)
	@Email
	@NotBlank(message = "Email is mandatory")
	private String email;

	// @NotBlank(message = "Password is mandatory")
	private String password;

	// required for spring security
	@Column(columnDefinition = "boolean default false")
	private boolean enabled = false;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Authority> authorities;

	public User(
			@NotBlank(message = "Username is mandatory") @Size(min = 2, message = "Username should have atleast 2 characters") String username,
			@Email @NotBlank(message = "Email is mandatory") String email) {
		super();
		this.username = username;
		this.email = email;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + "]";
	}
}
