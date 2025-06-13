package com.mitec.business.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.mitec.business.model.Job;

import lombok.Getter;

@Getter
public class DeleteJobEvent extends ApplicationEvent {

	private static final long serialVersionUID = -8248718403964881864L;

	private Long jobId;
	
	private List<Job> jobs;
	
	public DeleteJobEvent(Object source, Long jobId, List<Job> jobs) {
		super(source);
		this.jobId = jobId;
		this.jobs = jobs;
	}
	
}
