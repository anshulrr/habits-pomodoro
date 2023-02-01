package com.anshul.atomichabits.model;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity(name="users")
public class User {

	protected User() {
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank(message = "Username is mandatory")
	@Size(min=2, message = "Username should have atleast 2 characters")
	@Column(unique=true)
	private String username;

	@Email
	@NotBlank(message = "Email is mandatory")
	private String email;
	

	@NotBlank(message = "Password is mandatory")
	private String password;
	
	// required for spring security
	private boolean enabled = true;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Project> projects;
	
	public User(Long id, String name, String email, String password) {
		super();
		this.id = id;
		this.username = name;
		this.email = email;
//		this.password = (new BCryptPasswordEncoder()).encode(password);
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String name) {
		this.username = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public void setPassword(String password) {
//		this.password = password;
//		this.password = (new BCryptPasswordEncoder()).encode(password);
		this.password = (PasswordEncoderFactories.createDelegatingPasswordEncoder()).encode(password);
	}
	
	public List<Project> getProjects() {
		return projects;
	}

	// dt how  would we set multiple posts at a time	
	public void setPosts(List<Project> projects) {
		this.projects = projects;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + "]";
	}
}
