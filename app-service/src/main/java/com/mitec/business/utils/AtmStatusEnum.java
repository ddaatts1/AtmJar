package com.mitec.business.utils;

public enum AtmStatusEnum {
	PAUSE(0, "Tạm ngừng"),
	PROVIDING(1, "Đang cung cấp dịch vụ"),
	NOT_PROVIDED_YET(2, "Tạm ngừng cung cấp dịch vụ");
	
	private Integer key;

	private String value;

	public Integer getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private AtmStatusEnum(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public static AtmStatusEnum fromKey(Integer key) {
		for (AtmStatusEnum atmStatusEnum : AtmStatusEnum.values()) {
			if (atmStatusEnum.key.equals(key)) {
				return atmStatusEnum;
			}
		}
		throw new IllegalArgumentException("No constant with key " + key + " found");
	}
}
