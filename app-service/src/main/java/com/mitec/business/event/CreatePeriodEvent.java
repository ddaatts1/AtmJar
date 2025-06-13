package com.mitec.business.event;

import org.springframework.context.ApplicationEvent;

import com.mitec.business.model.Contract;

import lombok.Getter;

@Getter
public class CreatePeriodEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Contract contract;
	
	public CreatePeriodEvent(Object source, Contract contract) {
		super(source);
		this.contract = contract;
	}
	
}
