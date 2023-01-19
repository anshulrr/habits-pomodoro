package com.anshul.atomichabits.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity(name="user_details")
public class User {

	protected User() {
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank(message = "Name is mandatory")
	@Size(min=2, message = "Name should have atleast 2 characters")
	private String name;
	
	@Email
	@NotBlank(message = "Email is mandatory")
	private String email;
	
	private String password = "default";

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Project> projects;
	
	public User(Long id, String name, String email, String password) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<Project> getProjects() {
		return projects;
	}

	// dt how  would we set multiple posts at a time	
	public void setPosts(List<Project> projects) {
		this.projects = projects;
	}
}
