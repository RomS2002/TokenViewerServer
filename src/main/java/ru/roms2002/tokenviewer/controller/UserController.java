package ru.roms2002.tokenviewer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.roms2002.tokenviewer.dto.UserDTO;
import ru.roms2002.tokenviewer.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/")
	public void createOrSaveUser(@RequestBody UserDTO userDTO) {
		userService.createOrSaveUser(userDTO);
	}

	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable Integer userId) {
		userService.deleteUserById(userId);
	}

	@GetMapping("/find")
	public List<UserDTO> getAllStartsWith(
			@RequestParam(required = false) String lastName,
			@RequestParam(required = false) String group,
			@RequestParam(required = false) String department) {

		if (lastName != null)
			return userService.findByParamStartsWith("lastName", lastName);
		if (group != null)
			return userService.findByParamStartsWith("group", group);
		if (department != null)
			return userService.findByParamStartsWith("department", department);

		return new ArrayList<>();
	}
}
