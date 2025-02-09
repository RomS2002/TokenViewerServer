package ru.roms2002.tokenviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.roms2002.tokenviewer.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

}
