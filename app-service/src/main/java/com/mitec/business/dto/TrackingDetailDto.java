package com.mitec.business.dto;

import com.mitec.business.model.PartInventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrackingDetailDto {
	
	// partId == part_inventory.id
	private Long partId;
	private String partName;
	private Integer partType;
	private String partTypeStr;
	private Long quantity;
	
	public TrackingDetailDto(String partName, Long quantity) {
		super();
		this.partName = partName;
		this.quantity = quantity;
	}

	public TrackingDetailDto(String partName, Long quantity, Integer partType, String partTypeStr) {
		super();
		this.partName = partName;
		this.partType = partType;
		this.partTypeStr = partTypeStr;
		this.quantity = quantity;
	}
	
	public static TrackingDetailDto forQuantity(PartInventory part, Long quantity) {
		return new TrackingDetailDto(part.getId(), part.getName(), part.getType(), part.getTypeStr(), quantity);
	}

	@Override
	public String toString() {
		return "TrackingDetailDto [partId=" + partId + ", partName=" + partName + ", partType=" + partType
				+ ", partTypeStr=" + partTypeStr + ", quantity=" + quantity + "]";
	}
	
}
