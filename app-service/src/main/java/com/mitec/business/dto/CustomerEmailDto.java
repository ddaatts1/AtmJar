package com.mitec.business.dto;

import lombok.Data;

@Data
public class CustomerEmailDto {
	private Long id;
	private String email;
	private boolean isEnabled;
	private ContractDto contract;
}
