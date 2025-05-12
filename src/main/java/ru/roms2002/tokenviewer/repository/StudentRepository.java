package ru.roms2002.tokenviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.roms2002.tokenviewer.entity.StudentEntity;
import ru.roms2002.tokenviewer.entity.UserEntity;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {

	public void deleteByUser(UserEntity user);

	@Query(value = "from StudentEntity WHERE user.id = :id")
	public StudentEntity findByUserId(Integer id);
}