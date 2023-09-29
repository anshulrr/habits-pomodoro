package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query("select p from tags p where p.user.id = :user_id and p.id = :tag_id")
	public Optional<Tag> findUserTagById(Long user_id, Long tag_id);

	@Query(value = "select * from tags where user_id = :user_id order by priority limit :limit offset :offset", nativeQuery = true)
	public List<Tag> findUserTags(Long user_id, int limit, int offset);

	@Query(value = "select count(*) from tags where user_id = :user_id", nativeQuery = true)
	public Integer getUserTagsCount(Long user_id);
}
