package com.mitec.business.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorDeviceDto {
	private Long id;
	private String name;	
	private List<AccessoryDto> accessories;
	private List<SeriesDto> series;
	
	public ErrorDeviceDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
}
