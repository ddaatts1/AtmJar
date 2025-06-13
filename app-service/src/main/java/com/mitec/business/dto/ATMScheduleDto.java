package com.mitec.business.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ATMScheduleDto {
	private String serialNumber;
	private Integer status;
	private LocalDateTime endTime;
}
