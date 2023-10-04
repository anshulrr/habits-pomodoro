package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query("select p from tags p where p.user.id = :user_id and p.id = :tag_id")
	public Optional<Tag> findUserTagById(Long user_id, Long tag_id);
	
	@Query("select p from tags p where p.user.id = :user_id and p.id in :tag_ids")
	public Set<Tag> findUserTagByIds(Long user_id, List<Long> tag_ids);

	@Query(value = "select * from tags where user_id = :user_id order by priority, id desc limit :limit offset :offset", nativeQuery = true)
	public List<Tag> findUserTags(Long user_id, int limit, int offset);

	@Query(value = "select count(*) from tags where user_id = :user_id", nativeQuery = true)
	public Integer getUserTagsCount(Long user_id);
}
