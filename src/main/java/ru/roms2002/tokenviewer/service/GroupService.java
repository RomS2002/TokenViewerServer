package ru.roms2002.tokenviewer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.roms2002.tokenviewer.entity.GroupEntity;
import ru.roms2002.tokenviewer.repository.GroupRepository;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;
	
	public List<GroupEntity> findAll() {
		return groupRepository.findAll();
	}
	
	public List<GroupEntity> findNameStartsWith(String str) {
		return groupRepository.findByNameStartsWith(str);
	}
	
	public List<GroupEntity> findByName(String str) {
		return groupRepository.findByName(str);
	}
	
	public GroupEntity findById(int id) {
		return groupRepository.findById(id).get();
	}
	
	public void save(GroupEntity group) {
		groupRepository.save(group);
	}
	
	public void deleteById(int id) {
		groupRepository.deleteById(id);
	}
}
