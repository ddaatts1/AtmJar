package com.mitec.business.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ForgotPwdEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1655413380578761065L;

	private String email;
	
	private String newPwd;
	
	public ForgotPwdEvent(Object source, String email, String newPwd) {
		super(source);
		this.email = email;
		this.newPwd = newPwd;
	}
}
