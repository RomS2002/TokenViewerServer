package ru.roms2002.tokenviewer.controller;

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

import ru.roms2002.tokenviewer.entity.GroupEntity;
import ru.roms2002.tokenviewer.service.GroupService;

@RestController
@RequestMapping("/group")
public class GroupController {

	@Autowired
	private GroupService groupService;
	
	@GetMapping("/")
	public List<GroupEntity> getAll() {
		return groupService.findAll();
	}
	
	@GetMapping("/name/{startsWith}")
	public List<GroupEntity> getAllStartsWith(@PathVariable String startsWith) {
		return groupService.findNameStartsWith(startsWith);
	}
	
	@GetMapping("/get")
	public List<GroupEntity> getGroups(@RequestParam int count) {
		return groupService.getGroups(count);
	}
	
	@GetMapping("/id/{groupId}")
	public GroupEntity getById(@PathVariable Integer groupId) {
		return groupService.findById(groupId);
	}
	
	@PostMapping("/")
	public void saveOrUpdateGroup(@RequestBody GroupEntity groupEntity) {
		groupService.save(groupEntity);
	}
	
	@DeleteMapping("/{groupId}")
	public void deleteGroupById(@PathVariable Integer groupId) {
		groupService.deleteById(groupId);
	}
}
