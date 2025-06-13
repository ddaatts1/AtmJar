package com.mitec.business.dto;

import lombok.Data;

@Data
public class JobExportDto {
	private String checkIn;
	private String checkOut;
	private String time;
	private String username;
	private String serial;
	private String address;
	private String note;
	private String maintenance;
	private String status;
	private String accessory;
	private Long total;
	private String kpsc;
	private String jobPerform;
	private String serviceType;
}
