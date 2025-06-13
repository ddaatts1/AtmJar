package com.mitec.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticalContract {

	private Long regionId;
	private String regionName;
	private Long departmentId;
	private String departmentName;
	private Long contractId;
	private String contractName;
	private Long countAtm;
	private Long countService;
	private Long sumQuantity;
	private String average;
	
	// Load data view
	public StatisticalContract(Long regionId, String regionName, Long contractId, String contractName, Long countAtm) {
		super();
		this.regionId = regionId;
		this.regionName = regionName;
		this.contractId = contractId;
		this.contractName = contractName;
		this.countAtm = countAtm;
	}

	// Mapping when load data export
	public StatisticalContract(Long regionId, Long departmentId, Long contractId, String contractName, Long countAtm,
			Long countService, Long sumQuantity) {
		super();
		this.regionId = regionId;
		this.departmentId = departmentId;
		this.contractId = contractId;
		this.contractName = contractName;
		this.countAtm = countAtm;
		this.countService = countService;
		this.sumQuantity = sumQuantity;
	}

	public StatisticalContract() {
		super();
	}
	
}