package com.anshul.atomichabits.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "project_categories")
@Table(indexes= {
		@Index(name="project_categories_user_index", columnList="user_id"),
		@Index(name="project_categories_updated_at_index", columnList="updatedAt")
})
public class ProjectCategory implements Persistable<UUID>  {

	@Id
	private UUID id;
	
	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer level;

	@Column(columnDefinition = "boolean default true")
	private boolean statsDefault = true;
	
	@Column(columnDefinition = "boolean default true")
	private boolean visibleToPartners = true;

	@Column(columnDefinition = "varchar(255) default '#818181'")
	private String color = "#818181";

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	@CreationTimestamp
	@JsonIgnore
	private Instant createdAt;
	
	@UpdateTimestamp
	@JsonIgnore
	private Instant updatedAt;
	
	@Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() { return isNew; }

    @PostLoad
    @PrePersist
    void markNotNew() { this.isNew = false; }

	@OneToMany(mappedBy = "projectCategory")
	@JsonIgnore
	private List<Project> projects;

	// constructor used in unit tests	
	public ProjectCategory(UUID id, String name, User user) {
		super();
		this.id = id;
		this.name = name;
		this.user = user;
	}
}
