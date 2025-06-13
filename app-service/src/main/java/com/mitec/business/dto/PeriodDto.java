package com.mitec.business.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PeriodDto {
	private String name;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private boolean isMaintenance;
	
	private LocalDateTime originStartTime;
	
	@Override
	public String toString() {
		return "Period [name=" + name + ", startTime=" + startTime + ", endTime=" + endTime + ", isMaintenance="
				+ isMaintenance + "]";
	}
	
}
