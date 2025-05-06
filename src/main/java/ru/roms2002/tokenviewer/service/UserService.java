package ru.roms2002.tokenviewer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.roms2002.tokenviewer.dto.ChangeDepartmentDTO;
import ru.roms2002.tokenviewer.dto.ChangeRoleDTO;
import ru.roms2002.tokenviewer.dto.ChangeStudgroupDTO;
import ru.roms2002.tokenviewer.dto.SendMailDTO;
import ru.roms2002.tokenviewer.dto.UserDTO;
import ru.roms2002.tokenviewer.dto.UserInListDTO;
import ru.roms2002.tokenviewer.entity.GroupEntity;
import ru.roms2002.tokenviewer.entity.ProfessorEntity;
import ru.roms2002.tokenviewer.entity.StudentEntity;
import ru.roms2002.tokenviewer.entity.UserEntity;
import ru.roms2002.tokenviewer.exceptions.MailServerException;
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
	private ProfessorRepository professorRepository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private DataTransferService dataTransferService;

	public Integer getIdByToken(String token) {
		return userRepository.findByRegToken(token).getFirst().getId();
	}

	public UserDTO getById(Integer id) {

		UserEntity user = userRepository.findById(id).get();

		if (user.getRole().equals("Студент")) {
			StudentEntity student = user.getStudent();
			return new UserDTO(id, user.getFirstName(), user.getLastName(), user.getPatronymic(),
					"Студент", user.getEnabledFrom(), user.getEnabledUntil(),
					student.getReimbursement(), student.getGroup().getName(), null, null, null,
					user.isBlocked(), null, user.getStudent().getGroup().getFaculty());
		}

		ProfessorEntity professor = user.getProfessor();
		return new UserDTO(id, user.getFirstName(), user.getLastName(), user.getPatronymic(),
				"Преподаватель", user.getEnabledFrom(), user.getEnabledUntil(), null, null,
				professor.getDepartment(), professor.getAcademicTitle(),
				professor.getAcademicDegree(), user.isBlocked(), null, null);
	}

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
			if (!checkForRoleChange(userDTO)) {
				if (userDTO.getRole().equals("Студент")) {
					student = studentRepository.findByUserId(user.getId());
				} else {
					professor = professorRepository.findByUserId(user.getId());
				}
			} else {
				dataTransferService.sendUserChangeRole(new ChangeRoleDTO(userDTO.getId(),
						userDTO.getRole(), userDTO.getDepartment(), userDTO.getGroupName()));
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

			SendMailDTO sendMailDTO = new SendMailDTO(regToken, userDTO.getEmail());
			if (!dataTransferService.sendMail(sendMailDTO))
				throw new MailServerException("error processing message sending");
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
			if (student.getGroup() != null
					&& !student.getGroup().getName().equals(userDTO.getGroupName())) {
				dataTransferService.sendUserChangeStudgroup(
						new ChangeStudgroupDTO(userDTO.getId(), userDTO.getGroupName()));
			}

			student.setUser(user);
			student.setReimbursement(userDTO.getReimbursement());
			GroupEntity group = groupService.findByName(userDTO.getGroupName()).getFirst();
			student.setGroup(group);

			studentRepository.save(student);
			break;
		case "Преподаватель":
			if (professor.getDepartment() != null
					&& !professor.getDepartment().equals(userDTO.getDepartment())) {
				dataTransferService.sendUserChangeDepartment(
						new ChangeDepartmentDTO(userDTO.getId(), userDTO.getDepartment()));
			}

			professor.setUser(user);
			professor.setDepartment(userDTO.getDepartment());
			professor.setAcademicDegree(userDTO.getAcademicDegree());
			professor.setAcademicTitle(userDTO.getAcademicTitle());

			professorRepository.save(professor);
			break;
		}

		if (user.isBlocked()) {
			dataTransferService.sendBlocked(user.getId());
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

	public void deleteById(int id) {
		UserEntity user = userRepository.findById(id).get();
		if (user == null)
			return;
		if (user.getRole().equals("Студент"))
			studentRepository.delete(user.getStudent());
		if (user.getRole().equals("Преподаватель"))
			professorRepository.delete(user.getProfessor());
		userRepository.delete(user);
		dataTransferService.sendDeleteUser(user.getId());
	}

	public List<UserDTO> findByParamStartsWith(String param, String value) {
		List<UserDTO> userDTOs = new ArrayList<>();
		List<UserEntity> users;

		switch (param) {
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

			if (user.getRole().equals("Студент")) {
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

	public List<UserEntity> findByRegTokenAndLastName(String regToken, String lastName) {
		return userRepository.findByRegTokenAndLastName(regToken, lastName);
	}

	public List<UserInListDTO> getByLastName(String lastName) {
		return userRepository.findByLastNameStartsWithIgnoreCase(lastName).stream().map(user -> {
			if (user.getRole().equals("Студент")) {
				return new UserInListDTO(user.getId(), getFullName(user), user.getRole(), null,
						user.getStudent().getGroup().getName());
			}
			if (user.getRole().equals("Преподаватель")) {
				return new UserInListDTO(user.getId(), getFullName(user), user.getRole(),
						user.getProfessor().getDepartment(), null);
			}
			return null;
		}).toList();
	}

	public List<String> getAllDepartments() {
		return professorRepository.findAllDepartments();
	}

	private String getFullName(UserEntity user) {
		if (user.getPatronymic() != null) {
			return String.format("%s %s %s", user.getLastName(), user.getFirstName(),
					user.getPatronymic());
		}
		return String.format("%s %s", user.getLastName(), user.getFirstName());
	}
}
