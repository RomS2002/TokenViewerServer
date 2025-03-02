package ru.roms2002.tokenviewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.roms2002.tokenviewer.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	List<UserEntity> findByRegToken(String regToken);
	
	Optional<UserEntity> findById(Integer id);
	
	List<UserEntity> findByLastNameStartsWithIgnoreCase(String str);
}
