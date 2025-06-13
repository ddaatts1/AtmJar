package com.mitec.business.dto;

import lombok.Data;
@Data
public class StatisticalDepartmeDto {
	private Long id;
	private Long regionId;
	private Integer quantity;
	private Long contractId;
	private String department;
	private Integer totalAtm;
	private Long totalService;
	private Integer totalAccessory;
	private String average;
	

}
