package com.mitec.business.utils;

public enum TrackingStatusEnum {
	DANG_CHO(0, "Đang chờ"),
	DA_NHAN(1, "Đã nhận"),
	GUI_CHO_BAN(2, "Gửi cho bạn");
	
	private Integer key;

	private String value;

	public Integer getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private TrackingStatusEnum(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public static TrackingStatusEnum fromKey(Integer key) {
		for (TrackingStatusEnum typeEnum : TrackingStatusEnum.values()) {
			if (typeEnum.key.equals(key)) {
				return typeEnum;
			}
		}
		throw new IllegalArgumentException("No constant with key " + key + " found");
	}
}
