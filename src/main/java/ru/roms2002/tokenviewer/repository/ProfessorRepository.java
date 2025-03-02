package ru.roms2002.tokenviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.roms2002.tokenviewer.entity.ProfessorEntity;
import ru.roms2002.tokenviewer.entity.UserEntity;

public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Integer> {

	public void deleteByUser(UserEntity user);
	
	@Query(value = "SELECT * FROM professor WHERE user_id = :id", nativeQuery = true)
	public ProfessorEntity findByUserId(Integer id);
}
