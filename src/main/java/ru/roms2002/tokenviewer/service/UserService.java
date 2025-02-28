package ru.roms2002.tokenviewer.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.roms2002.tokenviewer.dto.UserDTO;
import ru.roms2002.tokenviewer.entity.GroupEntity;
import ru.roms2002.tokenviewer.entity.ProfessorEntity;
import ru.roms2002.tokenviewer.entity.StudentEntity;
import ru.roms2002.tokenviewer.entity.UserEntity;
import ru.roms2002.tokenviewer.repository.ProfessorRepository;
import ru.roms2002.tokenviewer.repository.StudentRepository;
import ru.roms2002.tokenviewer.repository.UserRepository;

@Service
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
		
		for(int i = 0; i < 16; i++) {
			char ch = charset.charAt(rand.nextInt(charset.length()));
			regToken.append(ch);
		}
		
		return regToken.toString();
	}
	
	private boolean checkRegToken(String regToken) {
		return userRepository.findByRegToken(regToken).isEmpty();
	}
	
	public void createNewUser(UserDTO userDTO) {
		
		UserEntity user = new UserEntity();
		
		// Генерация токена
		String regToken;
		
		do {
			regToken = generateNewRegToken();
		} while(!checkRegToken(regToken));
		
		user.setRegToken(regToken);
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setPatronymic(userDTO.getPatronymic());
		user.setRole(userDTO.getRole());
		user.setEnabledFrom(userDTO.getEnabledFrom());
		user.setEnabledUntil(userDTO.getEnabledUntil());
		user.setBlocked(false);
		user = userRepository.save(user);
		
		switch(userDTO.getRole()) {
		case "student":
			StudentEntity student = new StudentEntity();
			student.setUser(user);
			student.setReimbursement(userDTO.getReimbursement());
			GroupEntity group = groupService.findByName(userDTO.getGroupName()).getFirst();
			student.setGroup(group);
			
			studentRepository.save(student);
			break;
		case "professor":
			ProfessorEntity professor = new ProfessorEntity();
			professor.setUser(user);
			professor.setDepartment(userDTO.getDepartment());
			professor.setAcademicDegree(userDTO.getAcademicDegree());
			professor.setAcademicTitle(userDTO.getAcademicTitle());
			
			professorRepository.save(professor);
			break;
		}
	}
}
