package com.anshul.atomichabits.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.anshul.atomichabits.exceptions.ResourceNotFoundException;
import com.anshul.atomichabits.jpa.TagRepository;
import com.anshul.atomichabits.jpa.UserRepository;
import com.anshul.atomichabits.model.Tag;
import com.anshul.atomichabits.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TagService {

	private UserRepository userRepository;

	private TagRepository tagRepository;
	
	public Tag retriveTag(Long userId, Long id) {
		Optional<Tag> tagEntry = tagRepository.findUserTagById(userId, id);
		if (tagEntry.isEmpty())
			throw new ResourceNotFoundException("tag id:" + id);
		return tagEntry.get();
	}
	
	public List<Tag> retrieveAllTags(Long userId, Integer limit, Integer offset) {
		return tagRepository.findUserTags(userId, limit, offset);
	}
	
	public Integer retrieveAllTagsCount(Long userId) {
		return tagRepository.getUserTagsCount(userId);
	}
	
	public Tag createTag(Long userId, Tag tag) {
		Optional<User> userEntry = userRepository.findById(userId);
		if (userEntry.isEmpty())
			throw new ResourceNotFoundException("user id:" + userId);

		tag.setUser(userEntry.get());
		
		return tagRepository.save(tag);
	}
	
	public Tag updateTag(Long userId, Long id, Tag tag) {
		Optional<Tag> tagEntry = tagRepository.findUserTagById(userId, id);
		if (tagEntry.isEmpty())
			throw new ResourceNotFoundException("project tag id:" + id);
		
		tagEntry.get().setName(tag.getName());
		tagEntry.get().setColor(tag.getColor());
		tagEntry.get().setPriority(tag.getPriority());
		return tagRepository.save(tagEntry.get());
	}	
}
