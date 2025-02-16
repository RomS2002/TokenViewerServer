package ru.roms2002.tokenviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.roms2002.tokenviewer.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {
	
	public List<GroupEntity> findByNameStartsWith(String str);
	
	public List<GroupEntity> findByName(String name);
}
