package com.mitec.business.dto;

import lombok.Data;

@Data
public class ReplacementAccessoryDto {
	private Long id;
//	private Long deviceId;
//	private Long accessoryId;
	private Integer quantity;
	private ErrorDeviceDto errorDevice;
	private AccessoryDto accessory;
}
