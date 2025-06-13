package com.mitec.business.dto;

import java.util.List;

import lombok.Data;

@Data
public class ATMDto {
	private String serialNumber;
	private String atmId;
	private String address;
	private SeriesDto series;
	private RegionDto region;
	private DepartmentDto department;
	private List<ContractDto> contracts;
	private Integer status;
	private String statusDesc;
}
