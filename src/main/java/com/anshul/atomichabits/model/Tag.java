package com.anshul.atomichabits.model;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tags")
@Table(indexes= {
		@Index(name="tags_user_index", columnList="user_id")
})
public class Tag {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "integer default 1")
	private Integer priority = 1;

	@Column(columnDefinition = "varchar(255) default '#818181'")
	private String color = "#818181";

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	@ManyToMany(fetch = FetchType.LAZY,
		cascade = {
			CascadeType.ALL
	    },
		mappedBy = "tags")
	@JsonIgnore
	private Set<Task> tasks;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", priority=" + priority + "]";
	}

	// constructor used in unit tests	
	public Tag(Long id, String title, User user) {
		super();
		this.id = id;
		this.name = title;
		this.user = user;
	}
}
