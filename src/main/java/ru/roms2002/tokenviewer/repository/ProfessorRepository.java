package ru.roms2002.tokenviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.roms2002.tokenviewer.entity.ProfessorEntity;

public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Integer> {

}
