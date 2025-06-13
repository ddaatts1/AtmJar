package com.mitec.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractAtmDto {
	
	private Long id;
	private ATMDto atmDto;
	private ContractDto contractDto;
	private Long orderNumber;
	private Long contractId;
}
