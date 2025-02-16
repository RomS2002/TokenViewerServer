package ru.roms2002.tokenviewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.roms2002.tokenviewer.dto.UserDTO;
import ru.roms2002.tokenviewer.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/create")
	public void createNewUser(@RequestBody UserDTO userDTO) {
		userService.createNewUser(userDTO);
	}
}
