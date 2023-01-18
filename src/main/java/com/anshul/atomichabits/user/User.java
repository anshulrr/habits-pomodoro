package com.anshul.atomichabits.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity(name="user_details")
public class User {

	protected User() {
	}
	
	@Id
	@GeneratedValue
	private int id;
	
	@NotBlank(message = "Name is mandatory")
	@Size(min=2, message = "Name should have atleast 2 characters")
	private String name;
	
	@Email
	@NotBlank(message = "Email is mandatory")
	private String email;
	
	
	private String password = "default";


	public User(int id, String name, String email, String password) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
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
}
