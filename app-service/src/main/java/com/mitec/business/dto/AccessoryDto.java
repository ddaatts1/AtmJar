package com.mitec.business.dto;

import java.util.List;

import lombok.Data;

@Data
public class AccessoryDto {
	private Long id;
	private String name;
	private Long errorDeviceId;
	private ErrorDeviceDto errorDevice;
	List<SeriesDto> series;
//	private String seriesStr;
}
