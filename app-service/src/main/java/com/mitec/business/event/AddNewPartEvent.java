package com.mitec.business.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class AddNewPartEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long partId;
	private String partName;
	private Integer type;
	
	public AddNewPartEvent(Object source, Long partId, String partName, Integer type) {
		super(source);
		this.partId = partId;
		this.partName = partName;
		this.type = type;	
	}
}
