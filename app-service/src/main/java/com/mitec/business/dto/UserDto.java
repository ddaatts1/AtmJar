package com.mitec.business.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String username;
	private String deviceId;
	private boolean isActived;
	private String fullName;
	private String roles;
	private Long departmentId;
	private String departmentName;
	private String phoneNumber;
	
	public UserDto(Long id, String username) {
		super();
		this.id = id;
		this.username = username;
	}
	
	public UserDto(Long id, String username, String fullName) {
		super();
		this.id = id;
		this.username = username;
		this.fullName = fullName;
	}

	public UserDto(Long id, String username, String deviceId, boolean isActived, String fullName, String roles,
			String phoneNumber) {
		super();
		this.id = id;
		this.username = username;
		this.deviceId = deviceId;
		this.isActived = isActived;
		this.fullName = fullName;
		this.roles = roles;
		this.phoneNumber = phoneNumber;
	}
	
	
	public UserDto(String roles) {
		super();
		this.roles = roles;
	}
}
