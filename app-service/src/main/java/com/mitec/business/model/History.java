package com.mitec.business.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "history")
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "serial_number")
	private String serialNumber;
	
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "series_id")
	private Long seriesId;
	
	@Column(name = "region_id")
	private Long regionId;
	
	@Column(name = "department_id")
	private Long departmentId;
	
	@Column(name = "contract_id")
	private Long contractId;
	
	@Column(name = "status")
	private Integer status;
	
	@PrePersist
	public void prePersist() {
		createdDate = LocalDateTime.now();
		createdBy = "admin";
	}
}
