package ru.roms2002.tokenviewer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.roms2002.tokenviewer.dto.CheckTokenDTO;
import ru.roms2002.tokenviewer.dto.UserDTO;
import ru.roms2002.tokenviewer.dto.UserInListDTO;
import ru.roms2002.tokenviewer.service.UserService;

@RestController
public class OuterInfoController {

	@Autowired
	private UserService userService;

	@PostMapping("/checktoken")
	public boolean checkToken(@RequestBody CheckTokenDTO checkTokenDTO) {

		return !userService
				.findByRegTokenAndLastName(checkTokenDTO.getToken(), checkTokenDTO.getLastName())
				.isEmpty();
	}

	@PostMapping("/getId")
	public Integer getIdByToken(@RequestBody String token) {
		return userService.getIdByToken(token);
	}

	@GetMapping("/getUserDetails")
	public UserDTO getUserById(@RequestParam Integer id) {
		return userService.getById(id);
	}

	@GetMapping("/getUsersByLastName")
	public List<UserInListDTO> getUsersByLastName(@RequestParam("last-name") String lastName) {
		return userService.getByLastName(lastName);
	}

	@GetMapping("/get-departments")
	public List<String> getAllDepartments() {
		return userService.getAllDepartments();
	}
}
