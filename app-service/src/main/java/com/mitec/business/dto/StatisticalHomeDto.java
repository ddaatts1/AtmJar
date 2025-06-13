package com.mitec.business.dto;

import lombok.Data;

@Data
public class StatisticalHomeDto {
	private String serialNumber;
	private String address;
	private String contractName;
	private String cycleEndTime;
	private String serviceStatus;
	private String serviceState;
	
	public StatisticalHomeDto() {
		super();
	}

	public StatisticalHomeDto(String serialNumber, String address, String contractName, String cycleEndTime,
			String serviceStatus, String serviceState) {
		super();
		this.serialNumber = serialNumber;
		this.address = address;
		this.contractName = contractName;
		this.cycleEndTime = cycleEndTime;
		this.serviceStatus = serviceStatus;
		this.serviceState = serviceState;
	}
	
}
