   
package com.mitec.business.dto;
import lombok.Data;
@Data
public class StatisticalDepartments {

	private Long id;
	private Long regionId;
	private Integer quantity;
	private Long contractId;
	private String department;
	private Integer totalAtm;
	private Long totalService;
	private Integer totalAccessory;
	private String average;
	

	public StatisticalDepartments() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StatisticalDepartments(Long id, Long regionId, Integer quantity, Long contractId, String department) {
		super();
		this.id = id;
		this.regionId = regionId;
		this.quantity = quantity;
		this.contractId = contractId;
		this.department = department;
	
	}
	

}