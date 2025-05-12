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

	@Query("from UserEntity where role = 'Студент' and LOWER(student.group.name) LIKE ?#{[0].toLowerCase()}%")
	List<UserEntity> findByGroupNameStartsWithIgnoreCase(String str);

	@Query("from UserEntity where role = 'Преподаватель' and LOWER(professor.department) LIKE ?#{[0].toLowerCase()}%")
	List<UserEntity> findByDepartmentNameStartsWithIgnoreCase(String str);

	List<UserEntity> findByRegTokenAndLastName(String regToken, String lastName);
}
