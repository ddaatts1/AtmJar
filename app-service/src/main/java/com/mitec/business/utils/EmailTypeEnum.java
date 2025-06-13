package com.mitec.business.utils;

public enum EmailTypeEnum {
	INTERNAL(1, "Nội bộ"),
	CUSTOMER(2, "Khách hàng");
	
	
	private Integer key;

	private String value;

	public Integer getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private EmailTypeEnum(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public static EmailTypeEnum fromKey(Integer key) {
		for (EmailTypeEnum periodEnum : EmailTypeEnum.values()) {
			if (periodEnum.key.equals(key)) {
				return periodEnum;
			}
		}
		throw new IllegalArgumentException("No constant with key " + key + " found");
	}
}
