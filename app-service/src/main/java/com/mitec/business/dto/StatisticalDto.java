package com.mitec.business.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class StatisticalDto {

	private Long id;
	private Long jobId;
	private Long contractId;
	private String serialNumber; 
	private String username;
	private Long departmentId;
	private Long regionId;
	private Integer totalAtm;
	private LocalDateTime jobCompleteTime;
	private boolean isMaintenance;
	private Integer quantity;
	private Integer status;
	
}
