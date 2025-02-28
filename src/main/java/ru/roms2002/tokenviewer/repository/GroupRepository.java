package ru.roms2002.tokenviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.roms2002.tokenviewer.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {
	
	public List<GroupEntity> findByNameStartsWith(String str);
	
	public List<GroupEntity> findByName(String name);
	
	@Query(value="SELECT * FROM studgroup ORDER BY name LIMIT :count", nativeQuery = true)
	public List<GroupEntity> findFirst(int count);
}
