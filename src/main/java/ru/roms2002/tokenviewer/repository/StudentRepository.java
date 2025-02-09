package ru.roms2002.tokenviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.roms2002.tokenviewer.entity.StudentEntity;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {

}