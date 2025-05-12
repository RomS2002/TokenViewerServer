package ru.roms2002.tokenviewer.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
	private int id;

	private String name;

	private String faculty;

	@Column(name = "program_type")
	private String programType;

	private String program;

	@Column(name = "study_form")
	private String studyForm;

	@JsonIgnore
	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
	private List<StudentEntity> students;
}
