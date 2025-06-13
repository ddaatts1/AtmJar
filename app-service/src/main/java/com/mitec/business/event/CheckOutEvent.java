package com.mitec.business.event;

import org.springframework.context.ApplicationEvent;

import com.mitec.business.model.Job;

import lombok.Getter;

@Getter
public class CheckOutEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 1339224107914225860L;

	private Job job;
	
	public CheckOutEvent(Object source, Job job) {
		super(source);
		this.job = job;
	}
}
