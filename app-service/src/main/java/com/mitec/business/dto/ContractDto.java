package com.mitec.business.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mitec.business.model.Bank;

import lombok.Data;

@Data
public class ContractDto {
	private Long id;
	private String name;
	private LocalDateTime startTime; 
	private LocalDateTime endTime;
	
	// for input
	private String startTimeStr; 
	private String endTimeStr;
	
	private String bankName;
	private Integer status;
	private Integer maintenanceCycle;
	private List<ATMDto> atms;
	private List<CustomerEmailDto> customerEmails;
	private Integer type;
	private Bank bank;
	private List<ContractAtmDto> contractAtmDtos;
}
