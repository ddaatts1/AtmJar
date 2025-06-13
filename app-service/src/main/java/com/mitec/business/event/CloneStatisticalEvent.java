package com.mitec.business.event;

import org.springframework.context.ApplicationEvent;

import com.mitec.business.model.Job;

import lombok.Getter;

@Getter
public class CloneStatisticalEvent extends ApplicationEvent {

	private static final long serialVersionUID = -8874699909411387448L;
	
	// clone job to statistical
	private Job job;
	
	public CloneStatisticalEvent(Object source, Job job) {
		super(source);
		this.job = job;
	}
	
	
}
