package com.mitec.business.dto;

import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticalRegion {
	private Long regionId;
	private String regionName;
	private Long countAtm;
	private Long countService;
	private Long sumQuantity;
	private Long countContract;
	private List<StatisticalContract> statisticalContract;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatisticalRegion other = (StatisticalRegion) obj;
		return Objects.equals(regionId, other.regionId) && Objects.equals(regionName, other.regionName);
	}
	@Override
	public int hashCode() {
		return Objects.hash(regionId, regionName);
	}
	
	public StatisticalRegion() {
		super();
	}
	
	public StatisticalRegion(Long regionId, String regionName) {
		super();
		this.regionId = regionId;
		this.regionName = regionName;
		this.countAtm = 0L;
		this.countService = 0L;
		this.sumQuantity = 0L;
		this.countContract = 0L;
	}
}
