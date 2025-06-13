package com.mitec.business.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.mitec.business.model.ATM;

import lombok.Getter;

@Getter
public class ChangeStatusAtmEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ATM> list;
	
	public ChangeStatusAtmEvent(Object source, List<ATM> list) {
		super(source);
		this.list = list;
	}
}
