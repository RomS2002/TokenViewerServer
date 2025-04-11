package ru.roms2002.tokenviewer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.roms2002.tokenviewer.dto.UserDTO;
import ru.roms2002.tokenviewer.entity.GroupEntity;
import ru.roms2002.tokenviewer.entity.ProfessorEntity;
import ru.roms2002.tokenviewer.entity.StudentEntity;
import ru.roms2002.tokenviewer.entity.UserEntity;
import ru.roms2002.tokenviewer.repository.ProfessorRepository;
import ru.roms2002.tokenviewer.repository.StudentRepository;
import ru.roms2002.tokenviewer.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	ProfessorRepository professorRepository;

	@Autowired
	GroupService groupService;

	private String generateNewRegToken() {

		String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		StringBuilder regToken = new StringBuilder();

		Random rand = new Random();

		for (int i = 0; i < 16; i++) {
			char ch = charset.charAt(rand.nextInt(charset.length()));
			regToken.append(ch);
		}

		return regToken.toString();
	}

	private boolean checkRegToken(String regToken) {
		return userRepository.findByRegToken(regToken).isEmpty();
	}

	public void createOrSaveUser(UserDTO userDTO) {

		UserEntity user = new UserEntity();

		ProfessorEntity professor = new ProfessorEntity();
		StudentEntity student = new StudentEntity();
		
		// Если пользователь уже существует
		if (userDTO.getId() != null) {
			user.setId(userDTO.getId());
			user.setRegToken(userRepository.findById(userDTO.getId()).get().getRegToken());
			user.setBlocked(userDTO.getIsBlocked());
			// Проверка на изменение типа пользователя
			if(!checkForRoleChange(userDTO)) {
				if(userDTO.getRole().equals("Студент")) {
					student.setId(studentRepository.findByUserId(user.getId()).getId());
				} else {
					professor.setId(professorRepository.findByUserId(user.getId()).getId());
				}
			}
		} else {
			// Иначе - генерируем токен
			String regToken;

			do {
				regToken = generateNewRegToken();
			} while (!checkRegToken(regToken));

			user.setRegToken(regToken);
			// Новый пользователь всегда не заблокирован
			user.setBlocked(false);
		}

		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setPatronymic(userDTO.getPatronymic());
		user.setRole(userDTO.getRole());
		user.setEnabledFrom(userDTO.getEnabledFrom());
		user.setEnabledUntil(userDTO.getEnabledUntil());
		user = userRepository.save(user);

		switch (userDTO.getRole()) {
		case "Студент":
			student.setUser(user);
			student.setReimbursement(userDTO.getReimbursement());
			GroupEntity group = groupService.findByName(userDTO.getGroupName()).getFirst();
			student.setGroup(group);

			studentRepository.save(student);
			break;
		case "Преподаватель":
			professor.setUser(user);
			professor.setDepartment(userDTO.getDepartment());
			professor.setAcademicDegree(userDTO.getAcademicDegree());
			professor.setAcademicTitle(userDTO.getAcademicTitle());

			professorRepository.save(professor);
			break;
		}
	}

	private boolean checkForRoleChange(UserDTO user) {
		Optional<UserEntity> oldUser = userRepository.findById(user.getId());
		if (!oldUser.isPresent())
			return true;
		// Если роль изменилась, удаляем запись из базы данных
		if (!oldUser.get().getRole().equals(user.getRole())) {
			if (oldUser.get().getRole().equals("Студент")) {
				studentRepository.deleteByUser(oldUser.get());
				return true;
			} else {
				professorRepository.deleteByUser(oldUser.get());
				return true;
			}
		}
		return false;
	}
	
	public void deleteUserById(int id) {
		UserEntity user = userRepository.findById(id).get();
		if(user == null) return;
		if(user.getRole().equals("Студент"))
			studentRepository.delete(user.getStudent());
		if(user.getRole().equals("Преподаватель"))
			professorRepository.delete(user.getProfessor());
		userRepository.delete(user);
	}
	
	public List<UserDTO> findByParamStartsWith(String param, String value) {
		List<UserDTO> userDTOs = new ArrayList<>();
		List<UserEntity> users;
		
		switch(param) {
		case "lastName":
			users = userRepository.findByLastNameStartsWithIgnoreCase(value);
			break;
		case "group":
			users = userRepository.findByGroupNameStartsWithIgnoreCase(value);
			break;
		case "department":
			users = userRepository.findByDepartmentNameStartsWithIgnoreCase(value);
			break;
		default:
			users = new ArrayList<>();	
		}
		
		users.forEach(user -> {
			UserDTO userDTO = new UserDTO();
			userDTO.setId(user.getId());
			userDTO.setFirstName(user.getFirstName());
			userDTO.setLastName(user.getLastName());
			userDTO.setPatronymic(user.getPatronymic());
			userDTO.setRole(user.getRole());
			userDTO.setEnabledFrom(user.getEnabledFrom());
			userDTO.setEnabledUntil(user.getEnabledUntil());
			userDTO.setIsBlocked(user.isBlocked());
			
			if(user.getRole().equals("Студент")) {
				StudentEntity student = user.getStudent();
				userDTO.setGroupName(student.getGroup().getName());
				userDTO.setReimbursement(student.getReimbursement());
			} else {
				ProfessorEntity professor = user.getProfessor();
				userDTO.setAcademicDegree(professor.getAcademicDegree());
				userDTO.setAcademicTitle(professor.getAcademicTitle());
				userDTO.setDepartment(professor.getDepartment());
			}

			userDTOs.add(userDTO);
		});
		
		return userDTOs;
	}
}
