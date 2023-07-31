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

	@NotBlank(message = "Username is mandatory")
	@Size(min = 2, message = "Username should have atleast 2 characters")
	@Column(unique = true)
	private String username;

	@Email
	@NotBlank(message = "Email is mandatory")
	private String email;

	// @NotBlank(message = "Password is mandatory")
	private String password;

	// required for spring security
	private boolean enabled = true;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Project> projects;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Authority> authorities;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Pomodoro> pomodoros;

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + "]";
	}
}
