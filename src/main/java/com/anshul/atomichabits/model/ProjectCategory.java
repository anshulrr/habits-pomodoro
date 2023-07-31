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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "project_categories")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "level" }) })
public class ProjectCategory {

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

	@Override
	public String toString() {
		return "Project Category [id=" + id + ", name=" + name + ", level=" + level + "]";
	}
}
