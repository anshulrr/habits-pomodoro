package com.anshul.atomichabits.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "project_categories")
@Table(uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "user_id", "level" }) 
},
indexes= {
		@Index(name="project_categories_user_index", columnList="user_id")
})
public class ProjectCategory {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer level;

	@Column(columnDefinition = "boolean default true")
	private boolean statsDefault = true;

	@Column(columnDefinition = "varchar(255) default '#00FFFF'")
	private String color = "#00FFFF";

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
