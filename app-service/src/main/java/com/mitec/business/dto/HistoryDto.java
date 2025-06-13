package com.mitec.business.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HistoryDto {
	private Long id;
	private String address;
	private ATMDto atm;
	private LocalDateTime createdDate;
	private String createdBy;
	private String seriesName;
	private String regionName;
	private String departmentName;
	private String contractName;
	
}
