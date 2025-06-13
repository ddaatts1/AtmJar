package com.mitec.business.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceDto {
	private Long id;
	private String name;
	private List<ErrorDto> errors;
	
	public DeviceDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
}
