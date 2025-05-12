package ru.roms2002.tokenviewer.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import ru.roms2002.tokenviewer.entity.GroupEntity;
import ru.roms2002.tokenviewer.repository.GroupRepository;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private DataTransferService dataTransferService;

	public List<GroupEntity> findAll() {
		return groupRepository.findAll();
	}

	public List<GroupEntity> findNameStartsWith(String str) {
		return groupRepository.findByNameStartsWithAllIgnoreCase(str);
	}

	public List<GroupEntity> findByName(String str) {
		return groupRepository.findByName(str);
	}

	@Cacheable("groups")
	public GroupEntity findById(int id) {
		return groupRepository.findById(id).get();
	}

	@CachePut("groups")
	public void save(GroupEntity group) {
		groupRepository.save(group);
		dataTransferService.sendNewStudgroup(group.getName());
	}

	@CacheEvict("groups")
	public void deleteById(int id) {
		GroupEntity group = findById(id);
		if (group != null) {
			groupRepository.deleteById(id);
			dataTransferService.sendDeleteGroup(group.getName());
		}
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

	public List<String> getFaculties() {
		return groupRepository.findAllFaculties();
	}

	private String formLikePatternByCourse(int course) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH);

		int lastDigit;
		if (curMonth < 9) {
			lastDigit = (curYear - course) % 10;
		} else {
			lastDigit = (curYear - course + 1) % 10;
		}

		return "%-" + lastDigit + "%";
	}
}
