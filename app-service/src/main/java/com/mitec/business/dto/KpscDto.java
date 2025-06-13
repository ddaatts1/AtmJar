package com.mitec.business.dto;

import java.util.List;

import lombok.Data;

@Data
public class KpscDto {
	private Long id;
	private Long jobPerformId;
	private String errorDesc;
//	private Long deviceId;
	private DeviceDto device;
	private JobPerformDto jobPerform;
	private List<ReplacementAccessoryDto> replacementAccessories;
}
