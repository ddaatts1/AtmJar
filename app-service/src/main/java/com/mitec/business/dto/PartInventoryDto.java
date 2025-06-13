package com.mitec.business.dto;

import com.mitec.business.utils.PartInventoryTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PartInventoryDto {

	@Setter
	private Long id;
	@Getter
	private Long partId;
	@Setter
	private String name;
	@Setter
	private Integer type;
	
	private String typeStr;
	
	public PartInventoryDto(Long id, Long partId, String name) {
		super();
		this.id = id;
		this.partId = partId;
		this.name = name;
	}

	public PartInventoryDto(Long id, Long partId, String name, Integer type) {
		super();
		this.id = id;
		this.partId = partId;
		this.name = name;
		this.type = type;
	}
	
	public String getTypeStr() {
		return PartInventoryTypeEnum.fromKey(type).getValue();
	}

	@Override
	public String toString() {
		return "PartInventoryDto [id=" + id + ", name=" + name + ", type=" + type + ", typeStr=" + typeStr + "]";
	}
}
