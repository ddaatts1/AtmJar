package com.mitec.business.dto;

import lombok.Data;

@Data
public class ErrorDto {
	private Long id;
	private String name;
	private Long deviceId;
	DeviceDto device;
}
