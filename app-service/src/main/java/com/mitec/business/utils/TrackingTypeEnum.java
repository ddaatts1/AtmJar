package com.mitec.business.utils;

public enum TrackingTypeEnum {
//	CHUYEN_KHO(0, "Chuyển kho"),
	XUAT_KHO(0, "Xuất kho"),
	NHAP_KHO(1, "Nhập kho"),
	SUA_CHUA(2, "Sửa chữa");
	
	private Integer key;

	private String value;

	public Integer getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private TrackingTypeEnum(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public static TrackingTypeEnum fromKey(Integer key) {
		for (TrackingTypeEnum typeEnum : TrackingTypeEnum.values()) {
			if (typeEnum.key.equals(key)) {
				return typeEnum;
			}
		}
		throw new IllegalArgumentException("No constant with key " + key + " found");
	}
}
