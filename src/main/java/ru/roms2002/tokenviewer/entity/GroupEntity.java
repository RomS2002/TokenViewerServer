package ru.roms2002.tokenviewer.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "studgroup")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "faculty")
	private String faculty;
	
	@Column(name = "program_type")
	private String programType;
	
	@Column(name = "program")
	private String program;
	
	@Column(name = "study_form")
	private String studyForm;
	
	@JsonIgnore
	@OneToMany(mappedBy = "group")
	private List<StudentEntity> students;
}
