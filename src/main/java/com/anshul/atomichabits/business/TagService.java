package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.TagRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Tag;
import com.anshul.atomichabits.model.User;

@Service
public class TagService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TagRepository tagRepository;
	
	public Tag retriveTag(Long user_id, Long id) {
		Optional<Tag> tagEntry = tagRepository.findUserTagById(user_id, id);
		if (tagEntry.isEmpty())
			throw new ResourceNotFoundException("tag id:" + id);
		return tagEntry.get();
	}
	
	public List<Tag> retrieveAllTags(Long user_id, Integer limit, Integer offset) {
		return tagRepository.findUserTags(user_id, limit, offset);
	}
	
	public Integer retrieveAllTagsCount(Long user_id) {
		return tagRepository.getUserTagsCount(user_id);
	}
	
	public Tag createTag(Long user_id, Tag tag) {
		Optional<User> userEntry = userRepository.findById(user_id);

		tag.setUser(userEntry.get());
		
		return tagRepository.save(tag);
	}
	
	public Tag updateTag(Long user_id, Long id, Tag tag) {
		Optional<Tag> tagEntry = tagRepository.findUserTagById(user_id, id);
		if (tagEntry.isEmpty())
			throw new ResourceNotFoundException("project tag id:" + id);
		
		tagEntry.get().setTitle(tag.getTitle());
		tagEntry.get().setColor(tag.getColor());
		tagEntry.get().setPriority(tag.getPriority());
		return tagRepository.save(tagEntry.get());
	}	
}
