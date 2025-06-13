package com.mitec.business.dto;

import java.util.Map;

import lombok.Data;

@Data
public class ResultApi {
	private boolean isSuccess;
	private String message;
	private Map<String, Object> data;
}
