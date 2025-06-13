package com.mitec.business.utils;

public enum PartInventoryTypeEnum {
	DIVICE(1, "Thiết bị"),
	ACCESSORY(2, "Linh kiện");
	
	private Integer key;

	private String value;

	public Integer getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private PartInventoryTypeEnum(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public static PartInventoryTypeEnum fromKey(Integer key) {
		for (PartInventoryTypeEnum typeEnum : PartInventoryTypeEnum.values()) {
			if (typeEnum.key.equals(key)) {
				return typeEnum;
			}
		}
		throw new IllegalArgumentException("No constant with key " + key + " found");
	}
	
	public static PartInventoryTypeEnum fromValue(String value) {
		for (PartInventoryTypeEnum typeEnum : PartInventoryTypeEnum.values()) {
			if (typeEnum.value.equals(value)) {
				return typeEnum;
			}
		}
		throw new IllegalArgumentException("No constant with value " + value + " found");
	}
}
