package com.mitec.business.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class CreateSuggestionEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String phone;
	private String department;
	private String address;
	
	public CreateSuggestionEvent(Object source, String name, String phone, String department, String address) {
		super(source);
		this.name = name;
		this.phone = phone;
		this.department = department;
		this.address = address;
	}

}
