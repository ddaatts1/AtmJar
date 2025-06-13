package com.mitec.business.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class UpdatePartEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long partId;
	private String partName;
	private Integer partType;
	
	public UpdatePartEvent(Object source, Long partId, String partName, Integer partType) {
		super(source);
		this.partId = partId;
		this.partName = partName;
		this.partType = partType;
	}

}