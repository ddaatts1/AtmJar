package com.mitec.business.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "statistical")
public class Statistical {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "job_id", unique = true)
	private Long jobId;

	@Column(name = "contract_id")
	private Long contractId;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "username")
	private String username;

	@Column(name = "department_id")
	private Long departmentId;

	@Column(name = "region_id")
	private Long regionId;

	@Column(name = "job_complete_time")
	private LocalDateTime jobCompleteTime;

	@Column(name = "is_maintenance")
	private boolean isMaintenance;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "status")
	private Integer status;
	
	@Column(name = "is_remote_service")
	private Boolean isRemoteService;

}
