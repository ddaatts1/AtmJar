package com.mitec.business.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.mitec.business.model.ATM;

import lombok.Getter;

@Getter
public class CloneHistoryEvent extends ApplicationEvent {

	private static final long serialVersionUID = -5984228450394839318L;

	private List<ATM> atms;

	public CloneHistoryEvent(Object source, List<ATM> atms) {
		super(source);
		this.atms = atms;
	}
}
