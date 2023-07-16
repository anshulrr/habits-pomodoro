package com.anshul.atomichabits.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity(name = "project_categories")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "level" }) })
public class ProjectCategory {

	public ProjectCategory() {
	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	private Integer level;

	//	@Column(columnDefinition = "varchar(255) default '00FFFF'")
	//	private String color = "00FFFF";

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@OneToMany(mappedBy = "projectCategory")
	@JsonIgnore
	private List<Project> projects;

	public ProjectCategory(Long id, String name, Integer level) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	// dt what is use of this
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Project Category [id=" + id + ", name=" + name + ", level=" + level + "]";
	}
}
