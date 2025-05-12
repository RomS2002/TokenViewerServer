package ru.roms2002.tokenviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.roms2002.tokenviewer.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {

	public List<GroupEntity> findByNameStartsWithAllIgnoreCase(String str);

	public List<GroupEntity> findByName(String name);

	public List<GroupEntity> findByFacultyStartsWithAllIgnoreCase(String str);

	public List<GroupEntity> findByNameLike(String likePattern);

	public List<GroupEntity> findByStudyFormStartsWithAllIgnoreCase(String str);

	@Query("select distinct faculty from GroupEntity")
	public List<String> findAllFaculties();

	@Query("from GroupEntity order by name limit :count")
	public List<GroupEntity> findFirst(int count);
}
