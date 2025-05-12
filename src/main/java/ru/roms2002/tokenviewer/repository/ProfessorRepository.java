package ru.roms2002.tokenviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.roms2002.tokenviewer.entity.ProfessorEntity;
import ru.roms2002.tokenviewer.entity.UserEntity;

public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Integer> {

	public void deleteByUser(UserEntity user);

	@Query("from ProfessorEntity where user.id = :id")
	public ProfessorEntity findByUserId(Integer id);

	@Query("select distinct department from ProfessorEntity")
	public List<String> findAllDepartments();
}
