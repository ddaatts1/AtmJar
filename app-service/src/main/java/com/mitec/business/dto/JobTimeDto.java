package com.mitec.business.dto;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.mitec.business.utils.CustomFormatDate;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class JobTimeDto {

	private Long id;
	private String serialNumber;
	private String address;
	private LocalDateTime checkInTime;
	private LocalDateTime checkOutTime;
	
	private String user;
	private String workDetail;
	private String checkInStr;
	private String checkOutStr;
	
	
	public JobTimeDto(Long id, String serialNumber, String address, LocalDateTime checkInTime,
			LocalDateTime checkOutTime, String user, String phone, boolean isMaintenance, boolean isKpsc) {
		super();
		this.id = id;
		this.serialNumber = serialNumber;
		this.address = address;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;

		String phoneNumber = StringUtils.isNotBlank(phone) ? phone : "";
		this.user = user + " / Sđt: " + phoneNumber;
		
		StringBuilder sb = new StringBuilder();
		if (isMaintenance) {
    		sb.append("Bảo trì định kỳ ");
    	}
    	if (isMaintenance && isKpsc) {
    		sb.append("và ");
    	}
    	if (isKpsc) {
    		sb.append("Khắc phục sự cố");
    	}
    	
    	this.workDetail = sb.toString();
		
		if (checkInTime != null) {
			this.checkInStr = CustomFormatDate.formatToInputDateTimeLocal(checkInTime);
		}
		if (checkOutTime != null) {
			this.checkOutStr = CustomFormatDate.formatToInputDateTimeLocal(checkOutTime);
		}
	}
}
