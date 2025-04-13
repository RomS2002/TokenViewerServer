package ru.roms2002.tokenviewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.roms2002.tokenviewer.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	List<UserEntity> findByRegToken(String regToken);
	
	Optional<UserEntity> findById(Integer id);
	
	List<UserEntity> findByLastNameStartsWithIgnoreCase(String str);
	
	@Query(value = "SELECT * FROM tokenviewer_db.user WHERE id IN (SELECT user_id FROM tokenviewer_db.student "
			+ "INNER JOIN tokenviewer_db.studgroup ON group_id=studgroup.id WHERE "
			+ "LOWER(studgroup.name) LIKE ?#{[0].toLowerCase()}%)", nativeQuery = true)
	List<UserEntity> findByGroupNameStartsWithIgnoreCase(String str);
	
	@Query(value = "SELECT * FROM tokenviewer_db.user WHERE id IN (SELECT user_id FROM tokenviewer_db.professor "
			+ "WHERE LOWER(professor.department) LIKE ?#{[0].toLowerCase()}%)", nativeQuery = true)
	List<UserEntity> findByDepartmentNameStartsWithIgnoreCase(String str);
	
	List<UserEntity> findByRegTokenAndLastName(String regToken, String lastName);
}
