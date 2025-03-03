package ru.roms2002.tokenviewer.entity;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "reg_token")
	private String regToken;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "patronymic")
	private String patronymic;
	
	@Column(name = "role")
	private String role;
	
	@Column(name = "enabled_from")
	private Date enabledFrom;
	
	@Column(name = "enabled_until")
	private Date enabledUntil;
	
	@Column(name = "blocked")
	private boolean blocked;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
	private StudentEntity student;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
	private ProfessorEntity professor;
}
