package com.mitec.business.utils;

public enum ContractTypeEnum {
	HOP_DONG_LE(1, "Hợp đồng lẻ"),
	HOP_DONG_TAP_TRUNG(2, "Hợp đồng tập trung");
	
	
	private Integer key;

	private String value;

	public Integer getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private ContractTypeEnum(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public static ContractTypeEnum fromKey(Integer key) {
		for (ContractTypeEnum periodEnum : ContractTypeEnum.values()) {
			if (periodEnum.key.equals(key)) {
				return periodEnum;
			}
		}
		throw new IllegalArgumentException("No constant with key " + key + " found");
	}
}
