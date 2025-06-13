package com.mitec.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticalAmountDto {
	private String username;
	private Long totalServices;
	private Long totalAccessories;
	
	//Thành thêm số lần hướng dẫn từ xa qua điện thoại
	private Long totalMobile;
	
	public StatisticalAmountDto(String username, Long totalServices, Long totalAccessories, Long totalMobile) {
		this.username = username;
		this.totalServices = totalServices;
		this.totalAccessories = totalAccessories;
		this.totalMobile = totalMobile;
	}

	@Override
	public String toString() {
		return "StatisticalAmountDto [username=" + username + ", totalServices=" + totalServices + ", totalAccessories="
				+ totalAccessories + " totalMobile="+ totalMobile + "]";
	}

	public StatisticalAmountDto() {
		super();
	}
	
}