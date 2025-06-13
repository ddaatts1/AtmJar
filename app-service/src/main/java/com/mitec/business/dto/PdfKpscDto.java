package com.mitec.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfKpscDto {
	private int index;
	private String device;
	private String error;
	private String jobPerform;
	
	public PdfKpscDto(int index, String device, String error, String jobPerform) {
		super();
		this.index = index;
		this.device = device;
		this.error = error;
		this.jobPerform = jobPerform;
	}

	public PdfKpscDto() {
		super();
	}
	
}
