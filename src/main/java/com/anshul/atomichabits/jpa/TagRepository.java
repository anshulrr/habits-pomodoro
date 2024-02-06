package com.anshul.atomichabits.jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anshul.atomichabits.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query("select p from tags p where p.user.id = :userId and p.id = :tagId")
	public Optional<Tag> findUserTagById(Long userId, Long tagId);
	
	@Query("select p from tags p where p.user.id = :userId and p.id in :tagIds")
	public Set<Tag> findUserTagByIds(Long userId, List<Long> tagIds);

	@Query(value = "select * from tags where user_id = :userId order by priority, id desc limit :limit offset :offset", nativeQuery = true)
	public List<Tag> findUserTags(Long userId, int limit, int offset);

	@Query(value = "select count(*) from tags where user_id = :userId", nativeQuery = true)
	public Integer getUserTagsCount(Long userId);
}
