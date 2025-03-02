package ru.roms2002.tokenviewer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "professor")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfessorEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@OneToOne(cascade = {})
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private UserEntity user;
	
	@Column(name = "department")
	private String department;
	
	@Column(name = "academic_title")
	private String academicTitle;
	
	@Column(name = "academic_degree")
	private String academicDegree;
}
