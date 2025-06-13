package com.mitec.business.constant;

public enum MailLabelEnum {
	
	INBOX("INBOX"),
	SPAM("SPAM"),
	TRASH("TRASH"),
	IMPORTANT("IMPORTANT"),
	SENT("SENT");
	
	private String value;
	
	public String getValue() {
		return value;
	}
	
	private MailLabelEnum(String value) {
		this.value = value;
	}
}
