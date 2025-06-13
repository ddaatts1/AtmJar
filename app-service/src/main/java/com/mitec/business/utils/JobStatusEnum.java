package com.mitec.business.utils;

public enum JobStatusEnum {
	IN_PROCESS(0, "Đang thực hiện"),
	CANCEL(1, "Hủy"),
	COMPLETED(2, "Hoàn thành"),
	NOT_COMPLETED(3, "Không hoàn thành");
	
	
	private Integer key;

	private String value;

	public Integer getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private JobStatusEnum(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public static JobStatusEnum fromKey(Integer key) {
		for (JobStatusEnum jobStatusEnum : JobStatusEnum.values()) {
			if (jobStatusEnum.key.equals(key)) {
				return jobStatusEnum;
			}
		}
		throw new IllegalArgumentException("No constant with key " + key + " found");
	}
}
