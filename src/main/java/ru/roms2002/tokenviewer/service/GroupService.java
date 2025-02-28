package ru.roms2002.tokenviewer.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		return groupRepository.findByNameStartsWithAllIgnoreCase(str);
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
	
	public List<GroupEntity> getGroups(int count) {
		return groupRepository.findFirst(count);
	}
	
	public List<GroupEntity> findByFacultyStartsWith(String startsWith) {
		return groupRepository.findByFacultyStartsWithAllIgnoreCase(startsWith);
	}
	
	public List<GroupEntity> findByCourse(int course) {
		String likePattern = formLikePatternByCourse(course);
		return groupRepository.findByNameLike(likePattern);
	}
	
	public List<GroupEntity> findByStudyFormStartsWith(String startsWith) {
		return groupRepository.findByStudyFormStartsWithAllIgnoreCase(startsWith);
	}
	
	private String formLikePatternByCourse(int course) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH);
		
		int lastDigit;
		if(curMonth < 9) {
			lastDigit = (curYear - course) % 10;
		}
		else {
			lastDigit = (curYear - course + 1) % 10;
		}
		
		return "%-" + lastDigit + "%";
	}
}
