package com.mitec.business.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATMForStatistical {
	private String serialNumber;
	private String address;
	private List<PeriodDto> periods;
	private Long totalKpsc;
	private boolean maxMaintenance;
	private Long orderNumber;
	private Long remoteServices;
	
	@Override
	public String toString() {
		return "ATMForStatistical [serialNumber=" + serialNumber + ", address=" + address + ", periods=" + periods
				+ ", totalKpsc=" + totalKpsc + ", sumMaintenance=" + maxMaintenance + "]";
	}

	public ATMForStatistical(String serialNumber, String address, List<PeriodDto> periods, Long totalKpsc,
			boolean maxMaintenance, Long orderNumber, Long remoteServices) {
		super();
		this.serialNumber = serialNumber;
		this.address = address;
		this.periods = periods;
		this.totalKpsc = totalKpsc;
		this.maxMaintenance = maxMaintenance;
		this.orderNumber = orderNumber;
		this.remoteServices = remoteServices;
	}

	public ATMForStatistical() {
		super();
	}
}
