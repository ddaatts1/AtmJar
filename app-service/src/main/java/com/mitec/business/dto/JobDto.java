package com.mitec.business.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class JobDto {
	private Long id;
	private ATMDto atm;
	private String checkInTime;
	private String checkOutTime;
	private String jobReason;
	private Integer status;
	private String note;
	private String jobError;
	private UserDto user;
	private String completeTime;
	private List<KpscDto> kpscs;
	
	@JsonProperty(value = "isMaintenance")
	private boolean isMaintenance;
	
	@JsonProperty(value = "isKpsc")
	private boolean isKpsc;
	private String filePath;
}
