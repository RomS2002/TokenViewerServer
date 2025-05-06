package ru.roms2002.tokenviewer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDepartmentDTO {

	@JsonProperty("user_id")
	private int userId;

	private String department;
}
