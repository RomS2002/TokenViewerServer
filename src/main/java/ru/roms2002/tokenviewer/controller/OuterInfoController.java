package ru.roms2002.tokenviewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.roms2002.tokenviewer.dto.CheckTokenDTO;
import ru.roms2002.tokenviewer.service.UserService;

@RestController
public class OuterInfoController {

	@Autowired
	private UserService userService;

	@PostMapping("/checktoken")
	public boolean checkToken(@RequestBody CheckTokenDTO checkTokenDTO) {

		return !userService.findByRegTokenAndLastName(checkTokenDTO.getToken(), checkTokenDTO.getLastName()).isEmpty();
	}
}
