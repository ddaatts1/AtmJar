package com.mitec.business.event;

import java.time.LocalDateTime;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class UpdateInventoryCheckOutEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSONArray listJsonAcc;
	private String username;
	private LocalDateTime time;
	
	public UpdateInventoryCheckOutEvent(Object source, JSONArray listJsonAcc, String username, LocalDateTime time) {
		super(source);
		this.listJsonAcc = listJsonAcc;
		this.username = username;
		this.time = time;
	}
}
