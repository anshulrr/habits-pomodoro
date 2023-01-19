package com.anshul.atomichabits.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anshul.atomichabits.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
