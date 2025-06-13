package com.mitec.business.dto;

import lombok.Data;

@Data
public class InventoryDto {
	private Long id;
	private String name;
	private Long regionId;
	private String regionName;
	private String address;
	private Long[] userIds;
}
