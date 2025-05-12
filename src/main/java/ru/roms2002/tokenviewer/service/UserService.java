package ru.roms2002.tokenviewer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.roms2002.tokenviewer.dto.SendMailDTO;
import ru.roms2002.tokenviewer.dto.UserDTO;
import ru.roms2002.tokenviewer.dto.UserInListDTO;
import ru.roms2002.tokenviewer.entity.ProfessorEntity;
import ru.roms2002.tokenviewer.entity.StudentEntity;
import ru.roms2002.tokenviewer.entity.UserEntity;
import ru.roms2002.tokenviewer.exceptions.MailServerException;
import ru.roms2002.tokenviewer.repository.ProfessorRepository;
import ru.roms2002.tokenviewer.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfessorRepository professorRepository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private DataTransferService dataTransferService;

	public Integer getIdByToken(String token) {
		return userRepository.findByRegToken(token).getFirst().getId();
	}

	@Cacheable("users")
	public UserEntity findById(int userId) {
		Optional<UserEntity> tmp = userRepository.findById(userId);
		if (tmp.isEmpty())
			return null;
		return tmp.get();
	}

	public UserDTO getById(Integer id) {

		UserEntity user = findById(id);
		if (user == null)
			return null;

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
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setPatronymic(userDTO.getPatronymic());
		user.setEnabledFrom(userDTO.getEnabledFrom());
		user.setEnabledUntil(userDTO.getEnabledUntil());
		user.setRole(userDTO.getRole());

		if (userDTO.getId() == null) {
			String regToken;
			do {
				regToken = generateNewRegToken();
			} while (!checkRegToken(regToken));

			user.setRegToken(regToken);
			user.setBlocked(false);

			SendMailDTO sendMailDTO = new SendMailDTO(regToken, userDTO.getEmail());
			if (!dataTransferService.sendMail(sendMailDTO))
				throw new MailServerException("error processing message sending");
		} else {
			user.setId(userDTO.getId());
			user.setRegToken(findById(userDTO.getId()).getRegToken());
			user.setBlocked(userDTO.getIsBlocked());
		}

		if (userDTO.getRole().equals("Студент")) {
			StudentEntity student = new StudentEntity();
			student.setGroup(groupService.findByName(userDTO.getGroupName()).get(0));
			student.setReimbursement(userDTO.getReimbursement());
			student.setUser(user);
			user.setStudent(student);
		} else {
			ProfessorEntity professor = new ProfessorEntity();
			professor.setAcademicDegree(userDTO.getAcademicDegree());
			professor.setAcademicTitle(userDTO.getAcademicTitle());
			professor.setDepartment(userDTO.getDepartment());
			professor.setUser(user);
			user.setProfessor(professor);
		}

		user = save(user);
		System.out.println(user.getProfessor());
		System.out.println(user.getStudent());
	}

	private UserEntity save(UserEntity user) {
		return userRepository.save(user);
	}

	public void deleteById(int id) {
		UserEntity user = findById(id);
		if (user == null)
			return;
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
